package ru.mobileup.template.core.activity

import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class ActivityProvider {

    private val activityStateMutableFlow = MutableStateFlow<ComponentActivity?>(null)

    val activityStateFlow: StateFlow<ComponentActivity?>
        get() = activityStateMutableFlow

    val activity: ComponentActivity? get() = activityStateMutableFlow.value

    fun attachActivity(activity: ComponentActivity) {
        activityStateMutableFlow.value = activity
    }

    fun detachActivity() {
        activityStateMutableFlow.value = null
    }

    suspend fun awaitActivity(): ComponentActivity {
        return activityStateMutableFlow.filterNotNull().first()
    }
}
