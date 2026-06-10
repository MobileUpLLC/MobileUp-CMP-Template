package ru.mobileup.template.features.pomodoro.domain

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class PomodoroConfig(
    val workDuration: Duration,
    val shortBreakDuration: Duration,
    val longBreakDuration: Duration,
    val cyclesBeforeLongBreak: Int
) {
    fun durationOf(phase: PomodoroPhase): Duration = when (phase) {
        PomodoroPhase.Work -> workDuration
        PomodoroPhase.ShortBreak -> shortBreakDuration
        PomodoroPhase.LongBreak -> longBreakDuration
    }

    companion object {
        val Default = PomodoroConfig(
            workDuration = 25.minutes,
            shortBreakDuration = 5.minutes,
            longBreakDuration = 15.minutes,
            cyclesBeforeLongBreak = 4
        )
    }
}
