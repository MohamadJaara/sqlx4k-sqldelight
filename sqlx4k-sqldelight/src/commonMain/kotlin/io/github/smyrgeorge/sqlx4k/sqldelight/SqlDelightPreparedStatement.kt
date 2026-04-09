@file:OptIn(ExperimentalUuidApi::class)
@file:Suppress("unused")

package io.github.smyrgeorge.sqlx4k.sqldelight

import app.cash.sqldelight.db.SqlPreparedStatement
import io.github.smyrgeorge.sqlx4k.impl.statement.ExtendedStatement
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.time.Instant
import kotlin.math.max
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SqlDelightPreparedStatement(sql: String) : SqlPreparedStatement {
    var statement = ExtendedStatement(sql.normalizePositionalParameters())

    override fun bindBoolean(index: Int, boolean: Boolean?) {
        statement = statement.bind(index, boolean)
    }

    override fun bindBytes(index: Int, bytes: ByteArray?) {
        statement.bind(index, bytes?.toHexString()?.let { "\\x$it" })
    }

    override fun bindDouble(index: Int, double: Double?) {
        statement = statement.bind(index, double)
    }

    fun bindShort(index: Int, short: Short?) {
        statement = statement.bind(index, short)
    }

    fun bindInt(index: Int, int: Int?) {
        statement = statement.bind(index, int)
    }

    override fun bindLong(index: Int, long: Long?) {
        statement = statement.bind(index, long)
    }

    override fun bindString(index: Int, string: String?) {
        statement = statement.bind(index, string)
    }

    fun bindDate(index: Int, value: LocalDate?) {
        statement = statement.bind(index, value?.toString())
    }

    fun bindTime(index: Int, value: LocalTime?) {
        statement = statement.bind(index, value?.toString())
    }

    fun bindLocalTimestamp(index: Int, value: LocalDateTime?) {
        statement = statement.bind(index, value?.toString())
    }

    fun bindTimestamp(index: Int, value: Instant?) {
        statement = statement.bind(index, value?.toString())
    }

    fun bindInterval(index: Int, value: DateTimePeriod?) {
        statement = statement.bind(index, value?.toString())
    }

    fun bindUuid(index: Int, value: Uuid?) {
        statement = statement.bind(index, value?.toString())
    }
}

private fun String.normalizePositionalParameters(): String {
    if ('?' !in this) return this

    val normalized = StringBuilder(length)
    var nextImplicitIndex = 1
    var maxExplicitIndex = 0
    var index = 0

    while (index < length) {
        when {
            startsWith("--", index) -> {
                val end = indexOf('\n', index + 2).let { if (it == -1) length else it }
                normalized.append(this, index, end)
                index = end
            }

            startsWith("/*", index) -> {
                val end = indexOf("*/", index + 2).let { if (it == -1) length else it + 2 }
                normalized.append(this, index, end)
                index = end
            }

            this[index] == '\'' || this[index] == '"' -> {
                val quote = this[index]
                normalized.append(quote)
                index++
                while (index < length) {
                    val current = this[index]
                    normalized.append(current)
                    index++
                    if (current == quote) {
                        if (index < length && this[index] == quote) {
                            normalized.append(this[index])
                            index++
                        } else {
                            break
                        }
                    }
                }
            }

            this[index] == '?' -> {
                var digitsEnd = index + 1
                while (digitsEnd < length && this[digitsEnd].isDigit()) {
                    digitsEnd++
                }

                if (digitsEnd > index + 1) {
                    val explicitIndex = substring(index + 1, digitsEnd).toInt()
                    maxExplicitIndex = max(maxExplicitIndex, explicitIndex)
                    nextImplicitIndex = max(nextImplicitIndex, maxExplicitIndex + 1)
                    normalized.append('$').append(explicitIndex)
                    index = digitsEnd
                } else {
                    while (nextImplicitIndex <= maxExplicitIndex) {
                        nextImplicitIndex++
                    }
                    normalized.append('$').append(nextImplicitIndex++)
                    index++
                }
            }

            else -> {
                normalized.append(this[index])
                index++
            }
        }
    }

    return normalized.toString()
}
