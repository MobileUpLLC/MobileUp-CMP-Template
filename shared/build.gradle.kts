import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    android {
        namespace = "ru.mobileup.template.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            export(project(":core"))
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            implementation(project(":features"))

            implementation(libs.koin)
            implementation(libs.logger.kermit)
            implementation(libs.replica.core)
            implementation(libs.decompose.core)
            implementation(libs.compose.runtime)
        }

        androidMain.dependencies {
            implementation(libs.activity.compose)
        }
    }
}
