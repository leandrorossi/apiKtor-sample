package com.example.routing

import com.example.repository.UserRepository
import com.example.models.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRouter() {
    routing {
        authenticate("auth-jwt") {
            getAllUsers()
            getUserById()
            getUserByName()
            insertUser()
            editUser()
            deleteUser()
        }
    }
}

private val userRepository = UserRepository()

private fun Route.getAllUsers() {
    get("/users") {
        val users = userRepository.findAll()

        if (users.isNotEmpty()) {
            call.respond(HttpStatusCode.OK, users)
        } else {
            call.respondText("No users found", status = HttpStatusCode.NotFound)
        }
    }
}

private fun Route.getUserById() {
    get("user/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val user = userRepository.findById(id) ?: return@get call.respondText(
            "No found with id $id",
            status = HttpStatusCode.NotFound
        )
        call.respond(user)
    }
}

private fun Route.getUserByName() {
    get("username/{name?}") {
        val name = call.parameters["name"] ?: return@get call.respondText(
            "Missing name",
            status = HttpStatusCode.BadRequest
        )
        val user = userRepository.findByName(name) ?: return@get call.respondText(
            "No found with name $name",
            status = HttpStatusCode.NotFound
        )
        call.respond(user)
    }
}

private fun Route.insertUser() {
    post("/user") {
        val request = call.receive<User>()
        val user = userRepository.insert(request)

        call.respondText("User created $user", status = HttpStatusCode.Created)
    }
}

private fun Route.editUser() {
    put("/user/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val request = call.receive<User>()
        val result = userRepository.edit(id, request)

        if (result) {
            call.respondText("User edited correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}

private fun Route.deleteUser() {
    delete("/user/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val result = userRepository.delete(id)

        if (result) {
            call.respondText("User removed correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}