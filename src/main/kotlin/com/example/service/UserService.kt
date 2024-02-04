package com.example.service

import com.example.models.User
import com.example.repository.UserRepository

class UserService(private val userRepository: UserRepository)  {

    suspend fun findAll(): List<User> = userRepository.findAll()

    suspend fun findById(id: Int): User? = userRepository.findById(id)

    suspend fun findByName(name: String): User? = userRepository.findByName(name)

    suspend fun insert(user: User): User? = userRepository.insert(user)

    suspend fun edit(id: Int, user: User): Boolean = userRepository.edit(id, user)

    suspend fun delete(id: Int) = userRepository.delete(id)
}