package ru.altmanea.webapp.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.altmanea.webapp.auth.authorization
import ru.altmanea.webapp.auth.roleAdmin
import ru.altmanea.webapp.auth.roleUser
import ru.altmanea.webapp.common.Item
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Student
import ru.altmanea.webapp.repo.groupsRepo
import ru.altmanea.webapp.repo.studentsRepo

fun Route.groupRoutes() {
    route(Config.groupsPath) {
        authenticate("auth-jwt") {
            authorization(setOf(roleAdmin, roleUser)) {
                get {
                    val groups = groupsRepo.read()
                    if (groups.isEmpty()) {
                        call.respondText("No group found", status = HttpStatusCode.NotFound)
                    } else {
                        call.respond(groups)
                    }
                }
            }
            authorization(setOf(roleAdmin)) {
                post { // создать группу
                    var group = call.receive<String>()
                    group = group.trim('\"')
                    groupsRepo.create(group)
                    call.respondText("Group stored correctly ($group)", status = HttpStatusCode.Created)
                }
                put("/{idG}") { // изменить группу
                    val idG = call.parameters["idG"]
                        ?: return@put call.respondText(
                            "Missing or malformed groupOld", status = HttpStatusCode.BadRequest
                        )
                    val newGroup = call.receive<String>().trim('\"')
                    val oldGroup = groupsRepo.read(idG)
                    studentsRepo.read().find { it.elem.group == oldGroup?.elem }?.let {
                        studentsRepo.update(
                            Item(
                                Student(it.elem.firstname, it.elem.surname, newGroup),
                                it.id,
                                it.version
                            )
                        )
                    }
                    groupsRepo.update(Item(newGroup, idG, oldGroup?.version ?: 0))
                    call.respondText("Group updated  (${oldGroup?.elem} -> $newGroup)", status = HttpStatusCode.OK)
                }
                delete("/{idG}") { // удалить группу
                    val idG = call.parameters["idG"]
                        ?: return@delete call.respondText(
                            "Missing or malformed group", status = HttpStatusCode.BadRequest
                        )
                    val deletedGroup = groupsRepo.read(idG)
                        ?: return@delete call.respondText(
                            "No group with id $idG", status = HttpStatusCode.NotFound
                        )
                    studentsRepo.read().forEach {
                        if (it.elem.group == deletedGroup.elem) {
                            studentsRepo.update(
                                Item(
                                    Student(it.elem.firstname, it.elem.surname, ""),
                                    it.id,
                                    it.version
                                )
                            )
                        }
                    }
                    studentsRepo.read().find { it.elem.group == deletedGroup.elem }
                        ?.let {
                            studentsRepo.update(
                                Item(
                                    Student(it.elem.firstname, it.elem.surname, ""),
                                    it.id,
                                    it.version
                                )
                            )
                        }
                    groupsRepo.delete(idG)
                    call.respondText("Group deleted correctly (${deletedGroup.elem})", status = HttpStatusCode.Accepted)
                }
            }
        }
    }
}