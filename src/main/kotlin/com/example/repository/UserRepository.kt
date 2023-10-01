package com.example.repository

import com.example.dao.DAOFacade
import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.User
import com.example.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserRepository : DAOFacade<User> {

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        name = row[Users.name],
        email = row[Users.email],
        password = row[Users.password]
    )

    override suspend fun findAll(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun findById(id: Int): User? = dbQuery {
        Users
            .select {Users.id eq id}
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun findByName(name: String): User? = dbQuery {
        Users
            .select {Users.name eq name}
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun insert(model: User): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.name] = model.name
            it[Users.email] = model.email
            it[Users.password] = model.password
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun edit(id: Int, model: User): Boolean = dbQuery {
        Users.update({ Users.id eq id}) {
            it[Users.name] = model.name
            it[Users.email] = model.email
        } > 0
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        Users.deleteWhere { this.id eq id } > 0
    }

}