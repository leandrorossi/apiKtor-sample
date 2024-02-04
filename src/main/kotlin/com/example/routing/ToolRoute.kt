package com.example.routing

import com.example.models.Tool
import com.example.service.ToolService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.toolRouter() {

    val service: ToolService by inject()

    routing {
        authenticate("auth-jwt") {
            rateLimit(RateLimitName("protected")) {
                getAllTools(service)
                getTool(service)
                insertTool(service)
                editTool(service)
                deleteTool(service)
            }
        }
    }
}

private fun Route.getAllTools(service: ToolService) {
    get("/tools") {
        val tools = service.findAll()

        if (tools.isNotEmpty()) {
            call.respond(HttpStatusCode.OK, tools)
        } else {
            call.respondText("No tools found", status = HttpStatusCode.NotFound)
        }
    }
}

private fun Route.getTool(service: ToolService) {
    get("/tool/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val tool = service.findById(id) ?: return@get call.respondText(
            "No found with id $id",
            status = HttpStatusCode.NotFound
        )
        call.respond(tool)
    }
}

private fun Route.insertTool(service: ToolService) {
    post("/tool") {
        val request = call.receive<Tool>()
        val tool = service.insert(request)

        call.respondText("Tool created $tool", status = HttpStatusCode.Created)
    }
}

private fun Route.editTool(service: ToolService) {
    put("/tool/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val request = call.receive<Tool>()
        val result = service.edit(id, request)

        if (result) {
            call.respondText("Tool edited correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}

private fun Route.deleteTool(service: ToolService) {
    delete("/tool/{id?}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val result = service.delete(id)

        if (result) {
            call.respondText("Tool removed correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}
