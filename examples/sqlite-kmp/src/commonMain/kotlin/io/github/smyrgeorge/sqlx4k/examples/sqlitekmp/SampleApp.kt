package io.github.smyrgeorge.sqlx4k.examples.sqlitekmp

import app.cash.sqldelight.async.coroutines.awaitAsList
import io.github.smyrgeorge.sqlx4k.Driver
import io.github.smyrgeorge.sqlx4k.examples.sqlitekmp.db.SampleDatabase
import io.github.smyrgeorge.sqlx4k.sqldelight.Sqlx4kSqldelightDriver

data class SampleNote(
    val id: Long,
    val title: String,
    val platform: String,
)

class SampleApp internal constructor(
    private val driver: Driver,
    private val database: SampleDatabase,
) {
    suspend fun resetAndSeed(platform: String) {
        database.notesQueries.deleteAll()
        database.notesQueries.insertNote(
            id = 1,
            title = "Ship SQLite support",
            platform = platform,
        )
        database.notesQueries.insertNote(
            id = 2,
            title = "Query data from shared code",
            platform = platform,
        )
    }

    suspend fun notes(): List<SampleNote> =
        database.notesQueries.selectAll()
            .awaitAsList()
            .map { row ->
                SampleNote(
                    id = row.id,
                    title = row.title,
                    platform = row.platform,
                )
            }

    suspend fun summary(platform: String): String {
        resetAndSeed(platform)
        val notes = notes()
        return buildString {
            append(platform)
            append(" sample loaded ")
            append(notes.size)
            append(" notes: ")
            append(notes.joinToString { "${it.id}:${it.title}" })
        }
    }

    suspend fun close() {
        driver.close().getOrThrow()
    }
}

suspend fun createSampleApp(driver: Driver): SampleApp {
    val sqlDriver = Sqlx4kSqldelightDriver(driver)
    SampleDatabase.Schema.create(sqlDriver).await()
    return SampleApp(
        driver = driver,
        database = SampleDatabase(sqlDriver),
    )
}
