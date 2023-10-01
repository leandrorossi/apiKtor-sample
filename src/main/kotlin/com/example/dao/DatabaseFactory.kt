package com.example.dao

import com.example.models.Tools
import com.example.models.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(config: ApplicationConfig) {

        //sqlserver
        //val jdbcURL = "jdbc:sqlserver://localhost:1433;databaseName=ApiKtor;integratedSecurity=false;encrypt=true;trustServerCertificate=true"

        val driver = config.property("ktor.database.driverClassName").getString()
        val jdbcURL = config.property("ktor.database.jdbcURL").getString()
        val databaseName = config.property("ktor.database.databaseName").getString()
        val username = config.property("ktor.database.username").getString()
        val password = config.property("ktor.database.password").getString()
        val maxPoolSize = config.property("ktor.database.maxPoolSize").getString()
        val autoCommit = config.property("ktor.database.autoCommit").getString()

        val connectionPool = createHikariDataSource(
            url = "$jdbcURL/$databaseName?user=$username&password=$password",
            driver = driver,
            maxPoolSize = maxPoolSize.toInt(),
            autoCommit = autoCommit.toBoolean()
        )

        val database = Database.connect(connectionPool)

        transaction(database) {
            SchemaUtils.create(Tools)
            SchemaUtils.create(Users)
        }

    }

    private fun createHikariDataSource(
        url: String,
        driver: String,
        maxPoolSize: Int,
        autoCommit: Boolean
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = maxPoolSize
        isAutoCommit = autoCommit
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

}