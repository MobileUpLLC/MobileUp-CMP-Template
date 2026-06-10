package ru.mobileup.template.features.pomodoro.domain

import kotlin.time.Duration

data class PomodoroState(
    val phase: PomodoroPhase,
    val remaining: Duration,
    val totalForPhase: Duration,
    val completedWorkCycles: Int,
    val isRunning: Boolean
) {
    val progress: Float = if (totalForPhase.inWholeMilliseconds == 0L) {
        0f
    } else {
        1f - remaining.inWholeMilliseconds.toFloat() / totalForPhase.inWholeMilliseconds.toFloat()
    }
}
