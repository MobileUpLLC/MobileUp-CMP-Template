package ru.mobileup.template.features.pomodoro.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.template.features.pomodoro.domain.PomodoroState

interface PomodoroComponent {

    val state: StateFlow<PomodoroState>

    val totalCyclesBeforeLongBreak: Int

    fun onStartPauseClick()

    fun onResetClick()

    fun onSkipClick()
}
