import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.kotest)
    alias(libs.plugins.module.graph)
}

kotlin {
    android {
        namespace = "ru.mobileup.template.features"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // for testing on Desktop
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))

            // Kotlin
            implementation(libs.kotlinx.datetime)
            implementation(libs.coroutines.core)

            // UI
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.components.resources)
            implementation(libs.bundles.coil)

            // DI
            implementation(libs.koin)

            // Logging
            implementation(libs.logger.kermit)

            // Network
            implementation(libs.bundles.ktor.shared)
            implementation(libs.ktorfit.lib)

            // Architecture
            implementation(libs.bundles.decompose)
            implementation(libs.bundles.replica.shared)
            implementation(libs.form.validation)
        }

        commonTest.dependencies {
            implementation(project(":core-testing"))
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.assertions.core)
        }

        jvmTest.dependencies {
            implementation(libs.kotest.runner.junit5)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling) // for preview
}

compose.resources {
    packageOfResClass = "ru.mobileup.template.features.generated.resources"
}

ktorfit {
    compilerPluginVersion.set(libs.versions.ktorfitCompiler.get())
}

composeCompiler {
    stabilityConfigurationFiles.add(
        rootProject.layout.projectDirectory.file("stability_config.conf")
    )
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Usage: ./gradlew generateModuleGraph detectGraphCycles
moduleGraph {
    featuresPackage.set("ru.mobileup.template.features")
    featuresDirectory.set(project.file("src/commonMain/kotlin/ru/mobileup/template/features"))
    outputDirectory.set(project.file("module_graph"))
}
