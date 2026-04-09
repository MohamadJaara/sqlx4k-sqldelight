package io.github.smyrgeorge.sqlx4k.sqldelight

import app.cash.sqldelight.db.QueryResult
import io.github.smyrgeorge.sqlx4k.ConnectionPool
import io.github.smyrgeorge.sqlx4k.sqlite.ISQLite
import io.github.smyrgeorge.sqlx4k.sqlite.SQLite
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class Sqlx4kSqldelightDriverSqliteTest {

    private var sqlx4kDriver: ISQLite? = null
    private var sqldelightDriver: Sqlx4kSqldelightDriver<ISQLite>? = null

    @BeforeTest
    fun setup() {
        runBlocking {
            val options = ConnectionPool.Options.builder()
                .minConnections(1)
                .maxConnections(1)
                .build()

            sqlx4kDriver = SQLite(
                url = ":memory:",
                options = options
            )
            sqldelightDriver = Sqlx4kSqldelightDriver(sqlx4kDriver!!)

            sqlx4kDriver!!.execute(
                """
                CREATE TABLE IF NOT EXISTS test_users (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    email TEXT,
                    active INTEGER DEFAULT 1
                )
                """.trimIndent()
            ).getOrThrow()
        }
    }

    @AfterTest
    fun teardown() {
        sqldelightDriver?.close()
    }

    @Test
    fun shouldExecuteInsertStatement() = runBlocking {
        val driver = sqldelightDriver ?: return@runBlocking

        val result = driver.execute(
            identifier = null,
            sql = "INSERT INTO test_users (id, name, email) VALUES (?, ?, ?)",
            parameters = 3
        ) {
            bindString(0, "user-1")
            bindString(1, "John Doe")
            bindString(2, "john@example.com")
        }

        assertTrue(result is QueryResult.AsyncValue)
        assertEquals(1L, result.await())
    }

    @Test
    fun shouldExecuteSelectQuery() = runBlocking {
        val driver = sqldelightDriver ?: return@runBlocking

        driver.execute(
            identifier = null,
            sql = "INSERT INTO test_users (id, name, email) VALUES (?, ?, ?)",
            parameters = 3
        ) {
            bindString(0, "user-2")
            bindString(1, "Jane Smith")
            bindString(2, "jane@example.com")
        }.await()

        var foundName: String? = null
        driver.executeQuery(
            identifier = null,
            sql = "SELECT name FROM test_users WHERE id = ?",
            mapper = { cursor ->
                QueryResult.AsyncValue {
                    if (cursor.next().await()) {
                        foundName = cursor.getString(0)
                    }
                    Unit
                }
            },
            parameters = 1
        ) {
            bindString(0, "user-2")
        }.await()

        assertEquals("Jane Smith", foundName)
    }

    @Test
    fun shouldHandleNullValues() = runBlocking {
        val driver = sqldelightDriver ?: return@runBlocking

        driver.execute(
            identifier = null,
            sql = "INSERT INTO test_users (id, name, email) VALUES (?, ?, ?)",
            parameters = 3
        ) {
            bindString(0, "user-3")
            bindString(1, "No Email User")
            bindString(2, null)
        }.await()

        var email: String? = "not_null"
        driver.executeQuery(
            identifier = null,
            sql = "SELECT email FROM test_users WHERE id = ?",
            mapper = { cursor ->
                QueryResult.AsyncValue {
                    if (cursor.next().await()) {
                        email = cursor.getString(0)
                    }
                    Unit
                }
            },
            parameters = 1
        ) {
            bindString(0, "user-3")
        }.await()

        assertEquals(null, email)
    }

    @Test
    fun shouldSupportNewTransaction() = runBlocking {
        val driver = sqldelightDriver ?: return@runBlocking

        val transaction = driver.newTransaction().await()
        assertNotNull(transaction)
        assertEquals(transaction, driver.currentTransaction())
    }
}
