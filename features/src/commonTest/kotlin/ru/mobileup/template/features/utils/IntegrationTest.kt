package ru.mobileup.template.features.utils

import io.kotest.core.spec.style.scopes.FunSpecRootScope
import ru.mobileup.template.core_testing.integration_test.IntegrationTestScope
import ru.mobileup.template.core_testing.integration_test.integrationTestImpl
import ru.mobileup.template.features.featureModules

fun FunSpecRootScope.integrationTest(
    name: String,
    block: suspend IntegrationTestScope.() -> Unit
) = integrationTestImpl(name, featureModules, block)