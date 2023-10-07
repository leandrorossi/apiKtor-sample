package com.example

import com.example.dao.DatabaseFactory
import com.example.plugins.configureCompression
import com.example.plugins.configureJWT
import com.example.plugins.configureSerialization
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

    userRouter()
    loginRouter()
    toolRouter()
}
