package com.example

import com.example.dao.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import com.example.routing.toolRouter
import com.example.routing.loginRouter
import com.example.routing.userRouter

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureCompression()
    configureJWT()

    userRouter()
    loginRouter()
    toolRouter()
}
