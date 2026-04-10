# SQLite KMP Sample

This sample shows one shared SQLDelight database backed by `sqlx4k-sqlite` on:

- JVM
- Android
- iOS

## What it demonstrates

- A shared SQLDelight schema in `commonMain`
- A shared `SampleApp` that seeds and reads data asynchronously
- Platform-specific SQLite driver creation for JVM, Android, and iOS

## Key files

- `src/commonMain/sqldelight/.../notes.sq`
- `src/commonMain/kotlin/.../SampleApp.kt`
- `src/jvmMain/kotlin/.../JvmSample.kt`
- `src/androidMain/kotlin/.../AndroidSample.kt`
- `src/iosMain/kotlin/.../IosSample.kt`

## JVM usage

```kotlin
import io.github.smyrgeorge.sqlx4k.examples.sqlitekmp.runJvmSample

val summary = runJvmSample()
println(summary)
```

Build it with:

```bash
./gradlew :examples:sqlite-kmp:compileKotlinJvm
```

## Android usage

```kotlin
import io.github.smyrgeorge.sqlx4k.examples.sqlitekmp.runAndroidSample

val summary = runAndroidSample(applicationContext)
println(summary)
```

Configure Android sources with:

```bash
./gradlew :examples:sqlite-kmp:help -Ptargets=android
```

## iOS usage

```kotlin
import io.github.smyrgeorge.sqlx4k.examples.sqlitekmp.runIosSample

let summary = runIosSample()
print(summary)
```

Configure iOS sources with:

```bash
./gradlew :examples:sqlite-kmp:help -Ptargets=iosArm64
```
