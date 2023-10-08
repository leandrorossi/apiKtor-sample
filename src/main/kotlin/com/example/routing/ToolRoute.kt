package com.example.routing

import com.example.repository.ToolRepository
import com.example.models.Tool
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.toolRouter() {
    routing {
        authenticate("auth-jwt") {
            rateLimit(RateLimitName("protected")) {
                getAllTools()
                getTool()
                insertTool()
                editTool()
                deleteTool()
            }
        }
    }
}

private val toolRepository = ToolRepository()

private fun Route.getAllTools() {
    get("/tools") {
        val tools = toolRepository.findAll()

        if (tools.isNotEmpty()) {
            call.respond(HttpStatusCode.OK, tools)
        } else {
            call.respondText("No tools found", status = HttpStatusCode.NotFound)
        }
    }
}

private fun Route.getTool() {
    get("/tool/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val tool = toolRepository.findById(id) ?: return@get call.respondText(
            "No found with id $id",
            status = HttpStatusCode.NotFound
        )
        call.respond(tool)
    }
}

private fun Route.insertTool() {
    post("/tool") {
        val request = call.receive<Tool>()
        val tool = toolRepository.insert(request)

        call.respondText("Tool created $tool", status = HttpStatusCode.Created)
    }
}

private fun Route.editTool() {
    put("/tool/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val request = call.receive<Tool>()
        val result = toolRepository.edit(id, request)

        if (result) {
            call.respondText("Tool edited correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}

private fun Route.deleteTool() {
    delete("/tool/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val result = toolRepository.delete(id)

        if (result) {
            call.respondText("Tool removed correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}
