package ru.mobileup.template.features.pomodoro.presentation

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import org.jetbrains.compose.resources.StringResource
import ru.mobileup.template.core.message.data.MessageService
import ru.mobileup.template.core.message.domain.Message
import ru.mobileup.template.core.utils.componentScope
import ru.mobileup.template.core.utils.resourceDesc
import ru.mobileup.template.features.generated.resources.Res
import ru.mobileup.template.features.generated.resources.pomodoro_message_long_break
import ru.mobileup.template.features.generated.resources.pomodoro_message_short_break
import ru.mobileup.template.features.generated.resources.pomodoro_message_work
import ru.mobileup.template.features.pomodoro.domain.PomodoroConfig
import ru.mobileup.template.features.pomodoro.domain.PomodoroPhase
import ru.mobileup.template.features.pomodoro.domain.PomodoroState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

class RealPomodoroComponent(
    componentContext: ComponentContext,
    private val messageService: MessageService,
    private val config: PomodoroConfig = PomodoroConfig.Default
) : ComponentContext by componentContext, PomodoroComponent {

    override val state = MutableStateFlow(initialState())

    override val totalCyclesBeforeLongBreak: Int = config.cyclesBeforeLongBreak

    private var tickJob: Job? = null

    override fun onStartPauseClick() {
        if (state.value.isRunning) pause() else start()
    }

    override fun onResetClick() {
        tickJob?.cancel()
        tickJob = null
        state.value = initialState()
    }

    override fun onSkipClick() {
        val wasRunning = state.value.isRunning
        tickJob?.cancel()
        tickJob = null
        advancePhase(announce = false)
        if (wasRunning) start()
    }

    private fun start() {
        val current = state.value
        val endInstant = Clock.System.now() + current.remaining
        state.update { it.copy(isRunning = true) }
        tickJob = componentScope.launch {
            while (true) {
                val left = endInstant - Clock.System.now()
                if (left <= ZERO) {
                    state.update { it.copy(remaining = ZERO) }
                    advancePhase(announce = true)
                    start()
                    return@launch
                }
                state.update { it.copy(remaining = left) }
                delay(TICK_INTERVAL)
            }
        }
    }

    private fun pause() {
        tickJob?.cancel()
        tickJob = null
        state.update { it.copy(isRunning = false) }
    }

    private fun advancePhase(announce: Boolean) {
        val current = state.value
        val (nextPhase, nextCompleted) = nextPhaseFor(current)
        val nextDuration = config.durationOf(nextPhase)
        state.value = PomodoroState(
            phase = nextPhase,
            remaining = nextDuration,
            totalForPhase = nextDuration,
            completedWorkCycles = nextCompleted,
            isRunning = false
        )
        if (announce) showPhaseMessage(nextPhase)
    }

    private fun nextPhaseFor(current: PomodoroState): Pair<PomodoroPhase, Int> = when (current.phase) {
        PomodoroPhase.Work -> {
            val completed = current.completedWorkCycles + 1
            val next = if (completed % config.cyclesBeforeLongBreak == 0) {
                PomodoroPhase.LongBreak
            } else {
                PomodoroPhase.ShortBreak
            }
            next to completed
        }
        PomodoroPhase.ShortBreak,
        PomodoroPhase.LongBreak -> PomodoroPhase.Work to current.completedWorkCycles
    }

    private fun showPhaseMessage(phase: PomodoroPhase) {
        val resource: StringResource = when (phase) {
            PomodoroPhase.Work -> Res.string.pomodoro_message_work
            PomodoroPhase.ShortBreak -> Res.string.pomodoro_message_short_break
            PomodoroPhase.LongBreak -> Res.string.pomodoro_message_long_break
        }
        messageService.showMessage(Message(text = resource.resourceDesc()))
    }

    private fun initialState(): PomodoroState = PomodoroState(
        phase = PomodoroPhase.Work,
        remaining = config.workDuration,
        totalForPhase = config.workDuration,
        completedWorkCycles = 0,
        isRunning = false
    )

    companion object {
        private val TICK_INTERVAL: Duration = 50.milliseconds
    }
}
