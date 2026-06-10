package ru.mobileup.template.features.pomodoro

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import org.koin.dsl.module
import ru.mobileup.template.core.ComponentFactory
import ru.mobileup.template.features.pomodoro.presentation.PomodoroComponent
import ru.mobileup.template.features.pomodoro.presentation.RealPomodoroComponent

val pomodoroModule = module { }

fun ComponentFactory.createPomodoroComponent(
    componentContext: ComponentContext
): PomodoroComponent {
    return RealPomodoroComponent(componentContext, get())
}
