package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Tool (val id: Int? = null, val name: String? = null)

object Tools : Table() {
    val id = integer("_id").autoIncrement()
    val name = varchar("name", 100).nullable()

    override val primaryKey = PrimaryKey(id)
}