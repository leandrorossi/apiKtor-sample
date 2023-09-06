package com.example.dao

interface DAOFacade<T> {

    suspend fun findAll() : List<T>

    suspend fun findById(id: Int): T?

    suspend fun findByName(name: String): T?

    suspend fun insert(model: T): T?

    suspend fun edit(id: Int, model: T): Boolean

    suspend fun delete(id: Int): Boolean

}