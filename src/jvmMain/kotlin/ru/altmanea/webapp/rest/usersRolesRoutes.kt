package ru.altmanea.webapp.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.altmanea.webapp.access.Role
import ru.altmanea.webapp.access.User
import ru.altmanea.webapp.auth.*
import ru.altmanea.webapp.config.Config

fun Route.usersRolesRoutes() {
    route(Config.usersRolesPath) {
        authenticate("auth-jwt") {
            authorization(setOf(roleAdmin, roleUser)) {
                get { // Получить список пользователей и их ролей
                    val userRolesList = userRoles.map {
                        Pair(it.key, it.value)
                    }
                    call.respond(userRolesList)
                    call.respondText(userRolesList.joinToString { "${it.first.fullName()}, ${it.second.joinToString { it.name }}" })
                }
                get("roles") { // Получить список ролей
                    call.respond(roleList)
                }
            }
            authorization(setOf(roleAdmin)) {
                put { // Изменить роли пользователя
                    val (user, receivedRoles) = call.receive<Pair<User, Set<Role>>>()
                    val mappedRoles = receivedRoles.mapNotNull { receivedRole -> // Найти роль по имени
                        roleList.find { it.name == receivedRole.name }
                    }.toSet()

                    var userUpdated = false
                    userRoles.forEach {
                        if (it.key.username + it.key.password == user.username + user.password) {
                            userRoles[it.key] = mappedRoles
                            userUpdated = true
                        }
                    }

                    if (userUpdated) {
                        call.respondText(
                            "User updated (\"${user.formateFullName()}\") with roles \"${mappedRoles.joinToString { it.name }}\"",
                            status = HttpStatusCode.Accepted
                        )
                    } else {
                        call.respondText("User not found", status = HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}