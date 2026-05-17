package ru.mobileup.template.features.utils

import io.kotest.core.spec.style.scopes.FunSpecRootScope
import ru.mobileup.template.core_testing.component_test.ComponentTestScope
import ru.mobileup.template.core_testing.component_test.componentTestImpl
import ru.mobileup.template.features.featureModules

fun FunSpecRootScope.componentTest(
    name: String,
    block: suspend ComponentTestScope.() -> Unit
) = componentTestImpl(name, featureModules, block)
