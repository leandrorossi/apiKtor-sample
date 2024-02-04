package com.example.service

import com.example.models.Tool
import com.example.repository.ToolRepository

class ToolService(private val toolRepository: ToolRepository)  {

    suspend fun findAll(): List<Tool> = toolRepository.findAll()

    suspend fun findById(id: Int): Tool? = toolRepository.findById(id)

    suspend fun insert(tool: Tool): Tool? = toolRepository.insert(tool)

    suspend fun edit(id: Int, tool: Tool): Boolean = toolRepository.edit(id, tool)

    suspend fun delete(id: Int) = toolRepository.delete(id)

}