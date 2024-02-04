package com.example.routing

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.fileRoute() {
    routing {
        authenticate("auth-jwt") {
            rateLimit(RateLimitName("protected")) {
                upload()
                download()
            }
        }
    }

}

private fun Route.upload() {

    var fileDescription = ""
    var fileName = ""

    post("/upload") {

        val contentLength = call.request.header(HttpHeaders.ContentLength)

        if (Integer.parseInt(contentLength) > 2097152) {
            call.respond(HttpStatusCode.BadRequest, "File must be smaller than 2mb")
        }

        val multipartData = call.receiveMultipart()

        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    fileDescription = part.value
                }

                is PartData.FileItem -> {
                    fileName = part.originalFileName as String
                    val fileBytes = part.streamProvider().readBytes()
                    File("uploads/$fileName").writeBytes(fileBytes)
                }

                else -> {}
            }
            part.dispose()
        }

        call.respondText("$fileDescription is uploaded to 'uploads/$fileName'")
    }
}

private fun Route.download() {
    get("/download") {
        val file = File("uploads/ktor_logo.png")
        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "ktor_logo.png")
                .toString()
        )
        call.respondFile(file)
    }
}