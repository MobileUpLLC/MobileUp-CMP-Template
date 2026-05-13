import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktorfit) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.module.graph) apply false
    alias(libs.plugins.kotest) apply false
    alias(libs.plugins.detekt)
}

configure<DetektExtension> {
    toolVersion = libs.versions.detekt.get()
    source.setFrom(files(rootDir))
    config.setFrom(files("$rootDir/code_quality/detekt-config.yml"))
    parallel = true
    ignoreFailures = false
    disableDefaultRuleSets = true
}

dependencies {
    add("detektPlugins", libs.detekt.formatting)
    add("detektPlugins", libs.detekt.compose.rules)
}

tasks.withType<Detekt>().configureEach {
    exclude(
        "**/build/**",
        "**/resources/**",
        "code_quality/**",
        "geminio/**",
        "git_hooks/**"
    )
    reports {
        xml {
            outputLocation.set(file("build/reports/detekt-results.xml"))
            required.set(true)
        }
        html {
            outputLocation.set(file("build/reports/detekt-results.html"))
            required.set(true)
        }
        txt.required.set(false)
        md.required.set(false)
        sarif.required.set(false)
    }
}
