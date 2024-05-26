package ru.altmanea.webapp.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.altmanea.webapp.access.User
import ru.altmanea.webapp.auth.AuthConfig.Companion.audience
import ru.altmanea.webapp.auth.AuthConfig.Companion.issuer
import ru.altmanea.webapp.auth.AuthConfig.Companion.secret
import ru.altmanea.webapp.config.Config
import java.util.*

fun Route.authRoutes() {
    post(Config.loginPath) {
        val user = call.receive<User>()
        val localUser = userList.find { it.username == user.username }
        if (localUser?.password != user.password)
            return@post call.respondText("Wrong user name password", status = HttpStatusCode.Unauthorized)
        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000))
            .sign(Algorithm.HMAC256(secret))
        call.respond(hashMapOf("token" to token))
    }
    route("hello") {
        authenticate("auth-jwt") {
            // authenticate test
            get("all") {
                val principal = call.principal<UserPrincipal>()
                call.respondText("Hello, ${principal?.user?.username}! ")
            }
            // authorize test
            authorization(setOf(roleAdmin)) {
                get("admin") {
                    call.respond("Hello, Admin")
                }
            }
        }
    }
}
