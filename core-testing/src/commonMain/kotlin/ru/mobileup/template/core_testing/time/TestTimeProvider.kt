package ru.mobileup.template.core_testing.time

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import me.aartikov.replica.time.TimeProvider
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class TestTimeProvider(
    private val scheduler: TestCoroutineScheduler
) : TimeProvider {

    override val currentTime: Instant
        get() = Instant.fromEpochMilliseconds(scheduler.currentTime)
}
