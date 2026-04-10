import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.plugins.ExtensionAware

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.sqldeligh)
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
}

val targetsProperty = providers.gradleProperty("targets").orNull
val requestedTargets = targetsProperty
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: listOf("jvm")

val allTargetsEnabled = targetsProperty == "all"
val androidTargetEnabled = allTargetsEnabled || "android" in requestedTargets
val iosTargetEnabled = allTargetsEnabled || "iosArm64" in requestedTargets

if (androidTargetEnabled) {
    apply(plugin = "com.android.kotlin.multiplatform.library")
}

kotlin {
    jvm()

    if (iosTargetEnabled) {
        iosArm64()
    }

    if (androidTargetEnabled) {
        val androidTarget = (this as ExtensionAware)
            .extensions
            .getByName("android") as KotlinMultiplatformAndroidLibraryTarget

        androidTarget.apply {
            namespace = "io.github.smyrgeorge.sqlx4k.examples.sqlitekmp"
            compileSdk = 36
            minSdk = 21
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.sqlx4k.sqlite)
                implementation(project(":sqlx4k-sqldelight"))
            }
        }
    }
}

sqldelight {
    databases.register("SampleDatabase") {
        generateAsync = true
        packageName = "io.github.smyrgeorge.sqlx4k.examples.sqlitekmp.db"
    }
}
