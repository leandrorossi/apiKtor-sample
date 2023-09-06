package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(val id: Int? = null, val name: String, val email: String? = null, var password: String? = null)

object Users : Table() {
    val id = integer("_id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 100).nullable()
    val password = varchar("password", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}