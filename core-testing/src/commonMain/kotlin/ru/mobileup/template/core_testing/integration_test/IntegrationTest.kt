package ru.mobileup.template.core_testing.integration_test

import io.kotest.core.spec.style.scopes.FunSpecRootScope
import io.kotest.core.test.testCoroutineScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.koin.core.module.Module

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
fun FunSpecRootScope.integrationTestImpl(
    name: String,
    featureModules: List<Module>,
    block: suspend IntegrationTestScope.() -> Unit,
) {
    test(name).config(coroutineTestScope = true) {
        val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
        val koin = createKoin(testDispatcher, featureModules)

        val integrationScope = IntegrationTestScopeImpl(
            koin = koin,
            kotestScope = this,
            testScheduler = testCoroutineScheduler,
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
