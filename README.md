# Sqlx4k-sqldelight

![Build](https://github.com/smyrgeorge/sqlx4k-sqldelight/actions/workflows/ci.yml/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.github.smyrgeorge/sqlx4k-sqldelight)
![GitHub License](https://img.shields.io/github/license/smyrgeorge/sqlx4k-sqldelight)
![GitHub commit activity](https://img.shields.io/github/commit-activity/w/smyrgeorge/sqlx4k-sqldelight)
![GitHub issues](https://img.shields.io/github/issues/smyrgeorge/sqlx4k-sqldelight)
[![Kotlin](https://img.shields.io/badge/kotlin-2.3.0-blue.svg?logo=kotlin)](http://kotlinlang.org)

A coroutine-first SQL toolkit with compile-time query validations for Kotlin Multiplatform. PostgreSQL, MySQL, and
SQLite supported.

---

This repository only contains the necessary parts for the `sqldelight` integration.
If you are looking the driver implementation, you can find it here: https://github.com/smyrgeorge/sqlx4k

📖 [Documentation](https://smyrgeorge.github.io/sqlx4k-sqldelight/)

🏠 [Homepage](https://smyrgeorge.github.io/) (under construction)

## Usage

`PostgreSQL`, `MySQL`, and `SQLite` are supported.

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.smyrgeorge:sqlx4k-sqldelight:x.y.z")
                implementation("io.github.smyrgeorge:sqlx4k-postgres:x.y.z")
                // Or "io.github.smyrgeorge:sqlx4k-mysql:x.y.z"
                // Or "io.github.smyrgeorge:sqlx4k-sqlite:x.y.z" for JVM/Android SQLite.
            }
        }
    }
}

sqldelight {
    databases.register("Database") {
        generateAsync = true
        packageName = "db.entities"
        dialect("io.github.smyrgeorge:sqlx4k-sqldelight-dialect-postgres:x.y.z")
        // Or "io.github.smyrgeorge:sqlx4k-sqldelight-dialect-mysql:x.y.z" for MySQL.
        // SQLite uses SQLDelight's default SQLite dialect, so no extra dialect dependency is needed.
    }
}
```

For SQLite, the async wrapper is the same on JVM and Android:

```kotlin
import io.github.smyrgeorge.sqlx4k.sqlite.SQLite
import io.github.smyrgeorge.sqlx4k.sqldelight.Sqlx4kSqldelightDriver

val jvmDriver = Sqlx4kSqldelightDriver(SQLite(":memory:"))
val androidDriver = Sqlx4kSqldelightDriver(SQLite(context = appContext, url = "app.db"))
```

Check the examples for more information.

## Supported targets

We support the following targets:

- jvm
- android
- iosArm64
- androidNativeX64
- androidNativeArm64
- macosArm64
- macosX64
- linuxArm64
- linuxX64
- mingwX64
- wasmWasi (potential future candidate)
