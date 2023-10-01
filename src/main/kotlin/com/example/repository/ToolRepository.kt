package com.example.repository

import com.example.dao.DAOFacade
import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.Tool
import com.example.models.Tools
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ToolRepository : DAOFacade<Tool> {

    private fun resultRowToTool(row: ResultRow) = Tool(
        id = row[Tools.id],
        name = row[Tools.name]
    )

    override suspend fun findAll(): List<Tool> = dbQuery {
        Tools.selectAll().map(::resultRowToTool)
    }

    override suspend fun findById(id: Int): Tool? = dbQuery {
        Tools
            .select { Tools.id eq id }
            .map(::resultRowToTool)
            .singleOrNull()
    }

    override suspend fun findByName(name: String): Tool = dbQuery {
        TODO("Not yet implemented")
    }

    override suspend fun insert(model: Tool): Tool? = dbQuery {
        val insertStatement = Tools.insert {
            it[Tools.name] = model.name
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToTool)
    }

    override suspend fun edit(id: Int, model: Tool): Boolean = dbQuery {
        Tools.update({ Tools.id eq id }) {
            it[Tools.name] = model.name
        } > 0
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        Tools.deleteWhere { this.id eq id } > 0
    }

}