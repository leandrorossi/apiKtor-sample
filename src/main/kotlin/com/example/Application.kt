package com.example

import com.example.dao.DatabaseFactory
import com.example.plugins.*
import com.example.routing.loginRouter
import com.example.routing.toolRouter
import com.example.routing.userRouter
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(environment.config)
    configureJWT()
    configureSerialization()
    configureCompression()
    configureRateLimit()
    configureStatusPage()

    userRouter()
    loginRouter()
    toolRouter()
}
