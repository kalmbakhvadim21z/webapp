package ru.altmanea.webapp.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.altmanea.webapp.access.User
import ru.altmanea.webapp.auth.*
import ru.altmanea.webapp.config.Config

fun Route.userRoutes() {
    route(Config.usersPath) {
        authenticate("auth-jwt") {
            authorization(setOf(roleAdmin, roleUser)) {
                get { // Получить список пользователей
                    call.respond(userList)
                }
            }
            authorization(setOf(roleAdmin)) {
                post { // Добавить пользователя с правами user
                    val newUser = call.receive<User>()
                    if (newUser in userList) { // Пользователь уже существует
                        call.respondText("User already exists", status = HttpStatusCode.Conflict)
                        return@post
                    }
                    userList.add(newUser)
                    userRoles[newUser] = setOf(roleUser)
                    call.respondText("User \"${newUser.formateFullName()}\" added with roleUser", status = HttpStatusCode.Created)
                }
                delete { // Удалить пользователя
                    val currentUser = call.principal<UserPrincipal>()!!.user
                    val user = call.receive<User>()
                    if (currentUser.username + currentUser.password == user.username + user.password) { // Пытаемся удалить самого себя
                        call.respondText("You can't delete yourself", status = HttpStatusCode.Forbidden)
                        return@delete
                    }
                    userList.find { it.username + it.password == user.username + user.password }?.let {
                        userList.remove(it)
                        userRoles.remove(it)
                        call.respondText("User deleted (\"${user.formateFullName()}\")", status = HttpStatusCode.Accepted)
                    }
                }
                put { // Изменить пользователя
                    val currentUser = call.principal<UserPrincipal>()!!.user
                    val (oldUser, newUser) = call.receive<Pair<User, User>>()
                    if (currentUser.username == oldUser.username && currentUser.username != newUser.username) {
                        call.respondText("You can't change your login", status = HttpStatusCode.Forbidden)
                        return@put
                    }
                    val userToUpdate = userList.find { it.username == oldUser.username && it.password == oldUser.password }

                    if (userToUpdate != null) { // Пользователь найден
                        userList[userList.indexOf(userToUpdate)] = newUser
                        val roles = userRoles[userToUpdate]
                        if (roles != null) {
                            userRoles.remove(userToUpdate)
                            userRoles[newUser] = roles
                            call.respondText(
                                "User updated (\"${oldUser.formateFullName()}\" -> \"${newUser.formateFullName()}\") with roles ${roles.joinToString { it.name }}\"",
                                status = HttpStatusCode.Accepted
                            )
                        } else {
                            userRoles.remove(userToUpdate)
                            userRoles[newUser] = setOf(roleUser)
                            call.respondText(
                                "User updated (\"${oldUser.formateFullName()}\" -> \"${newUser.formateFullName()}\") with roleUser",
                                status = HttpStatusCode.Accepted
                            )
                        }
                    } else {
                        call.respondText("User not found", status = HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}