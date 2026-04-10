package io.github.smyrgeorge.sqlx4k.examples.sqlitekmp

import io.github.smyrgeorge.sqlx4k.ConnectionPool
import io.github.smyrgeorge.sqlx4k.sqlite.sqlite
import kotlinx.coroutines.runBlocking

fun createIosSampleApp(databasePath: String = "sqlite-kmp-sample.db"): SampleApp = runBlocking {
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

fun runIosSample(databasePath: String = "sqlite-kmp-sample.db"): String = runBlocking {
    val app = createIosSampleApp(databasePath)
    try {
        app.summary("iOS")
    } finally {
        app.close()
    }
}
