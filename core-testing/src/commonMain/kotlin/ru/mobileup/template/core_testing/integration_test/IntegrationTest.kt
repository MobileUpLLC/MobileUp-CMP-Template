package ru.mobileup.template.core_testing.integration_test

import io.kotest.core.spec.style.scopes.FunSpecRootScope
import io.kotest.core.test.testCoroutineScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.koin.core.module.Module

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
fun FunSpecRootScope.integrationTestImpl(
    name: String,
    featureModules: List<Module>,
    block: suspend IntegrationTestScope.() -> Unit,
) {
    test(name).config(coroutineTestScope = true) {
        // Use UnconfinedTestDispatcher to approximate Dispatchers.Main.immediate:
        // coroutines start eagerly so no runCurrent required.
        val testDispatcher = UnconfinedTestDispatcher(testCoroutineScheduler)

        // Use a separate scheduler so Replica behaviour timers do not
        // affect how far testCoroutineScheduler.advanceUntilIdle() jumps.
        val replicaBehaviourScheduler = TestCoroutineScheduler()
        val replicaBehaviourDispatcher = UnconfinedTestDispatcher(replicaBehaviourScheduler)

        val koin = createKoin(
            testScheduler = testCoroutineScheduler,
            testDispatcher = testDispatcher,
            replicaBehaviourDispatcher = replicaBehaviourDispatcher,
            featureModules = featureModules
        )

        val integrationScope = IntegrationTestScopeImpl(
            koin = koin,
            kotestScope = this,
            testScheduler = testCoroutineScheduler,
            replicaBehaviourScheduler = replicaBehaviourScheduler,
            testDispatcher = testDispatcher
        )

        var primaryFailure: Throwable? = null
        try {
            integrationScope.block()
        } catch (throwable: Throwable) {
            primaryFailure = throwable
            throw throwable
        } finally {
            try {
                integrationScope.finishTest(primaryFailure)
            } finally {
                koin.close()
            }
        }
    }
}

private suspend fun IntegrationTestScope.finishTest(primaryFailure: Throwable?) {
    try {
        advanceUntilIdle()
        mockServer.verify()
    } catch (throwable: Throwable) {
        if (primaryFailure == null) {
            throw throwable
        } else {
            primaryFailure.addSuppressed(throwable)
        }
    }
}
