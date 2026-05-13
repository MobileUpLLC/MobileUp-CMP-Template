import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
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

    iosArm64()
    iosSimulatorArm64()

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            api(libs.coroutines.core)
            api(libs.coroutines.test)
            api(libs.koin)
            api(libs.ktor.client.mock)
            api(libs.bundles.decompose)
            api(libs.bundles.replica.shared)
            api(libs.kotest.framework.engine)
        }
    }
}
