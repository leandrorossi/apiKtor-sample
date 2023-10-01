package com.example.routing

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.repository.UserRepository
import com.example.models.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*

fun Application.loginRouter() {
    routing {
        login()
        register()
        token()
    }
}

private val userRepository = UserRepository()

private const val SECRET = "secret"
private const val ISSUER = "http://0.0.0.0:8080/"
private const val AUDIENCE = "http://0.0.0.0:8080/login"
private val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]+\$")

private fun Route.register() {
    post("/register") {
        val user = call.receive<User>()

        if (user.password.toString().length < 6) {
            call.respondText("Password must be greater than 6 characters",  status = HttpStatusCode.BadRequest)
        }
        if (!regex.matches(user.password.toString())) {
            call.respondText("Password does not meet character requirements",  status = HttpStatusCode.BadRequest)
        }

        user.password = BCrypt.hashpw(user.password, BCrypt.gensalt())
        userRepository.insert(user)

        val token = JWT.create()
            .withAudience(AUDIENCE)
            .withIssuer(ISSUER)
            .withClaim("claim", user.name)
            .withExpiresAt(Date(System.currentTimeMillis() + 600000))
            .sign(Algorithm.HMAC256(SECRET))

        call.respond(hashMapOf("token" to token))
    }
}

private fun Route.login() {
    post("/login") {
        val request = call.receive<User>()

        if (request.password.toString().length < 6) {
            call.respondText("Password must be greater than 6 characters",  status = HttpStatusCode.BadRequest)
        }
        if (!regex.matches(request.password.toString())) {
            call.respondText("Password does not meet character requirements",  status = HttpStatusCode.BadRequest)
        }

        val user = userRepository.findByName(request.name) ?: return@post call.respondText(
            "No found with name ${request.name}",
            status = HttpStatusCode.NotFound
        )

        if(!BCrypt.checkpw(request.password, user.password)) {
            call.respondText("Password Invalid",  status = HttpStatusCode.BadRequest)
        }

        val token = JWT.create()
            .withAudience(AUDIENCE)
            .withIssuer(ISSUER)
            .withClaim("claim", request.name)
            .withExpiresAt(Date(System.currentTimeMillis() + 600000))
            .sign(Algorithm.HMAC256(SECRET))

        call.respond(hashMapOf("token" to token))
    }
}

private fun Route.token() {
    authenticate("auth-jwt") {
        get("/token") {
            val principal = call.principal<JWTPrincipal>()
            val user = principal!!.payload.getClaim("claim").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())

            call.respondText("Token, $user! Token is expired at $expiresAt ms.")
        }
    }
}