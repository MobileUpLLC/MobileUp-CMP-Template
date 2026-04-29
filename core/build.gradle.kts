import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
}

kotlin {
    android {
        namespace = "ru.mobileup.template.core"
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
            // Kotlin
            implementation(libs.kotlinx.datetime)
            implementation(libs.coroutines.core)

            // UI
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.components.resources)

            // Architecture
            implementation(libs.bundles.decompose)
            implementation(libs.bundles.replica.shared)
            implementation(libs.form.validation)

            // DI
            implementation(libs.koin)

            // Logging
            implementation(libs.logger.kermit)

            // Network
            implementation(libs.bundles.ktor.shared)
            implementation(libs.ktorfit.lib)
        }

        androidMain.dependencies {
            // Kotlin
            implementation(libs.coroutines.android)

            // UI
            implementation(libs.activity.compose)

            // Network
            implementation(libs.ktor.okhttp)
            implementation(libs.replica.android.network)

            // Security
            implementation(libs.security.crypto)
            implementation(libs.security.crypto.ktx)

            // Permissions
            implementation(libs.bundles.moko.permissions)
        }

        iosMain.dependencies {
            api(libs.ktor.darwin)

            // Permissions
            implementation(libs.bundles.moko.permissions)
        }

        jvmMain.dependencies {
            api(libs.ktor.okhttp)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "ru.mobileup.template.core.generated.resources"
}

ktorfit {
    compilerPluginVersion.set(libs.versions.ktorfitCompiler.get())
}

composeCompiler {
    stabilityConfigurationFiles.add(
        rootProject.layout.projectDirectory.file("stability_config.conf")
    )
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
