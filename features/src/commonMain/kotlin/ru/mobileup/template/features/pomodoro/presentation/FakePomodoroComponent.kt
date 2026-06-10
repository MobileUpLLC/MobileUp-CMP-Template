package ru.mobileup.template.features.pomodoro.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.template.features.pomodoro.domain.PomodoroConfig
import ru.mobileup.template.features.pomodoro.domain.PomodoroPhase
import ru.mobileup.template.features.pomodoro.domain.PomodoroState
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class FakePomodoroComponent(
    initial: PomodoroState = WORK_RUNNING
) : PomodoroComponent {

    override val state = MutableStateFlow(initial)

    override val totalCyclesBeforeLongBreak: Int = PomodoroConfig.Default.cyclesBeforeLongBreak

    override fun onStartPauseClick() = Unit

    override fun onResetClick() = Unit

    override fun onSkipClick() = Unit

    companion object {
        val WORK_RUNNING = PomodoroState(
            phase = PomodoroPhase.Work,
            remaining = 17.minutes + 32.seconds,
            totalForPhase = 25.minutes,
            completedWorkCycles = 1,
            isRunning = true
        )

        val SHORT_BREAK_PAUSED = PomodoroState(
            phase = PomodoroPhase.ShortBreak,
            remaining = 3.minutes + 12.seconds,
            totalForPhase = 5.minutes,
            completedWorkCycles = 2,
            isRunning = false
        )

        val LONG_BREAK_RUNNING = PomodoroState(
            phase = PomodoroPhase.LongBreak,
            remaining = 12.minutes + 5.seconds,
            totalForPhase = 15.minutes,
            completedWorkCycles = 4,
            isRunning = true
        )
    }
}
