import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.plugins.ExtensionAware

plugins {
    id("io.github.smyrgeorge.sqlx4k.multiplatform.simple")
    id("io.github.smyrgeorge.sqlx4k.publish")
    id("io.github.smyrgeorge.sqlx4k.dokka")
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
}

val androidTargetEnabled = providers.gradleProperty("targets").orNull
    ?.let { targets ->
        targets == "all" || targets.split(",").any { target -> target.trim() == "android" }
    } == true

if (androidTargetEnabled) {
    apply(plugin = "com.android.kotlin.multiplatform.library")
}

kotlin {
    if (androidTargetEnabled) {
        val androidTarget = (this as ExtensionAware)
            .extensions
            .getByName("android") as KotlinMultiplatformAndroidLibraryTarget

        androidTarget.apply {
            namespace = "io.github.smyrgeorge.sqlx4k.sqldelight"
            compileSdk = 36
            minSdk = 21
        }
    }

    sourceSets {
        configureEach {
            languageSettings.progressiveMode = true
        }
        commonMain {
            dependencies {
                api(libs.sqlx4k)
                api(libs.sqldeligh)
                api(libs.kotlinx.datetime)
                api(libs.stately.concurrency)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        jvmTest {
            dependencies {
                implementation(libs.sqlx4k.postgres)
                implementation(libs.sqlx4k.mysql)
                implementation(libs.sqlx4k.sqlite)
                implementation(libs.testcontainers)
                implementation(libs.testcontainers.postgresql)
                implementation(libs.testcontainers.mysql)
            }
        }
    }
}

// Configure test tasks to pass Docker environment to TestContainers
tasks.withType<Test> {
    environment("DOCKER_HOST", System.getenv("DOCKER_HOST") ?: "unix:///var/run/docker.sock")
    environment("TESTCONTAINERS_RYUK_DISABLED", "true")
    // Set Docker API version to 1.44 (minimum level for modern Docker versions)
    environment("DOCKER_API_VERSION", "1.44")
    systemProperty("api.version", "1.44")
}
