package io.github.smyrgeorge.sqlx4k.examples.sqlitekmp

import io.github.smyrgeorge.sqlx4k.ConnectionPool
import io.github.smyrgeorge.sqlx4k.sqlite.sqlite
import kotlinx.coroutines.runBlocking

fun createJvmSampleApp(databasePath: String = ":memory:"): SampleApp = runBlocking {
    createSampleApp(
        driver = sqlite(
            url = databasePath,
            options = ConnectionPool.Options.builder()
                .minConnections(1)
                .maxConnections(1)
                .build()
        )
    )
}

fun runJvmSample(databasePath: String = ":memory:"): String = runBlocking {
    val app = createJvmSampleApp(databasePath)
    try {
        app.summary("JVM")
    } finally {
        app.close()
    }
}

fun main() {
    println(runJvmSample())
}
