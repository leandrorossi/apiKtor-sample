package com.example.routing

import com.example.models.User
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.userRouter() {

    val service: UserService by inject()

    routing {
        authenticate("auth-jwt") {
            rateLimit (RateLimitName("protected")) {
                getAllUsers(service)
                getUserById(service)
                getUserByName(service)
                insertUser(service)
                editUser(service)
                deleteUser(service)
            }
        }
    }
}

private fun Route.getAllUsers(service: UserService) {
    get("/users") {
        val users = service.findAll()

        if (users.isNotEmpty()) {
            call.respond(HttpStatusCode.OK, users)
        } else {
            call.respondText("No users found", status = HttpStatusCode.NotFound)
        }
    }
}

private fun Route.getUserById(service: UserService) {
    get("user/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val user = service.findById(id) ?: return@get call.respondText(
            "No found with id $id",
            status = HttpStatusCode.NotFound
        )
        call.respond(user)
    }
}

private fun Route.getUserByName(service: UserService) {
    get("username/{name?}") {
        val name = call.parameters["name"] ?: return@get call.respondText(
            "Missing name",
            status = HttpStatusCode.BadRequest
        )
        val user = service.findByName(name) ?: return@get call.respondText(
            "No found with name $name",
            status = HttpStatusCode.NotFound
        )
        call.respond(user)
    }
}

private fun Route.insertUser(service: UserService) {
    post("/user") {
        val request = call.receive<User>()
        val user = service.insert(request)

        call.respondText("User created $user", status = HttpStatusCode.Created)
    }
}

private fun Route.editUser(service: UserService) {
    put("/user/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val request = call.receive<User>()
        val result = service.edit(id, request)

        if (result) {
            call.respondText("User edited correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}

private fun Route.deleteUser(service: UserService) {
    delete("/user/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val result = service.delete(id)

        if (result) {
            call.respondText("User removed correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}