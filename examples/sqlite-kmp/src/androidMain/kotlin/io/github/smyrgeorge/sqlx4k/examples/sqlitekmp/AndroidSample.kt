package io.github.smyrgeorge.sqlx4k.examples.sqlitekmp

import android.content.Context
import io.github.smyrgeorge.sqlx4k.ConnectionPool
import io.github.smyrgeorge.sqlx4k.sqlite.sqlite
import kotlinx.coroutines.runBlocking

fun createAndroidSampleApp(
    context: Context,
    databaseName: String = "sqlite-kmp-sample.db",
): SampleApp = runBlocking {
    createSampleApp(
        driver = sqlite(
            context = context,
            url = databaseName,
            options = ConnectionPool.Options.builder()
                .minConnections(1)
                .maxConnections(1)
                .build()
        )
    )
}

fun runAndroidSample(
    context: Context,
    databaseName: String = "sqlite-kmp-sample.db",
): String = runBlocking {
    val app = createAndroidSampleApp(context, databaseName)
    try {
        app.summary("Android")
    } finally {
        app.close()
    }
}
