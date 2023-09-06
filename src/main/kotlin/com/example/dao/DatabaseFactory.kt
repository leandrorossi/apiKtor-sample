package com.example.dao

import com.example.models.Tools
import com.example.models.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val jdbcURL = "jdbc:sqlserver://localhost:1433;databaseName=ApiKtor;integratedSecurity=false;encrypt=true;trustServerCertificate=true"
        val user = "sa"
        val password = "adm"
        val database = Database.connect(url = jdbcURL, user = user, password =  password)

        transaction(database) {
            SchemaUtils.create(Tools)
            SchemaUtils.create(Users)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

}