package ru.altmanea.webapp.rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import ru.altmanea.webapp.auth.*
import ru.altmanea.webapp.common.Item
import ru.altmanea.webapp.common.ItemId
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Grade
import ru.altmanea.webapp.repo.lessonsRepo
import ru.altmanea.webapp.repo.studentsRepo

fun Route.studentRoutes() {
    route(Config.studentsPath) {
        repoRoutes(
            studentsRepo,
            listOf(
                ApiPoint.read to { roleList.toSet() },
                ApiPoint.write to { setOf(roleAdmin) }
            )
        )
        authenticate("auth-jwt") {
            authorization(setOf(roleAdmin)) {
                get("ByStartName/{startName}") {
                    val startName =
                        call.parameters["startName"] ?: return@get call.respondText(
                            "Missing or malformed startName",
                            status = HttpStatusCode.BadRequest
                        )
                    val students = studentsRepo.read().filter {
                        it.elem.firstname.startsWith(startName)
                    }
                    if (students.isEmpty()) {
                        call.respondText("No students found", status = HttpStatusCode.NotFound)
                    } else {
                        call.respond(students)
                    }
                }
                get("students/{idS}/lessons") { // получить уроки студента
                    val studentId = call.parameters["idS"] ?: return@get call.respondText(
                        "Missing or malformed student ID",
                        status = HttpStatusCode.BadRequest
                    )
                    studentsRepo.read().find { it.id == studentId }
                        ?: return@get call.respondText(
                            "No student found with ID $studentId",
                            status = HttpStatusCode.NotFound
                        )
                    val lessons = lessonsRepo.read().filter { lesson ->
                        lesson.elem.students.any { it.studentId == studentId }
                    } ?: emptyList()
                    call.respond(lessons)
                }
                delete("delete/{idS}/lessons/{idL}") { // Удалить урок у студента
                    val idS = call.parameters["idS"] ?: return@delete call.respondText(
                        "Missing or malformed student ID",
                        status = HttpStatusCode.BadRequest
                    )
                    val idL = call.parameters["idL"] ?: return@delete call.respondText(
                        "Missing or malformed lesson ID",
                        status = HttpStatusCode.BadRequest
                    )
                    val itemLesson = lessonsRepo.read().find { it.id == idL }
                        ?: return@delete call.respondText(
                            "No lesson found with ID $idL",
                            status = HttpStatusCode.NotFound
                        )
                    val lesson = itemLesson.elem.removeStudent(idS)
                    lessonsRepo.update(Item(lesson, idL, itemLesson.version))
                    call.respondText("Lesson \"${lesson.name}\" deleted from student " +
                            "\"${studentsRepo.read(idS)?.elem?.fullname()}\"", status = HttpStatusCode.OK)
                }
                put("grade/{idS}/lessons/{idL}") { // Изменить оценку у студента
                    val (idL, grade) = call.receive<Pair<ItemId, Int>>()
                    val idS = call.parameters["idS"] ?: return@put call.respondText(
                        "Missing or malformed student ID",
                        status = HttpStatusCode.BadRequest
                    )
                    val itemLesson = lessonsRepo.read().find { it.id == idL }
                        ?: return@put call.respondText(
                            "No lesson found with ID $idL",
                            status = HttpStatusCode.NotFound
                        )
                    val studentInLesson = itemLesson.elem.students.find { it.studentId == idS }
                    if (studentInLesson != null) {
                        studentInLesson.grade = Grade.values().find { it.mark == grade }
                            ?: return@put call.respondText(
                                "Invalid grade value",
                                status = HttpStatusCode.BadRequest
                            )
                        lessonsRepo.update(Item(itemLesson.elem, idL, itemLesson.version))
                        call.respondText("Grade updated for student ${studentsRepo.read(idS)?.elem?.fullname()} in lesson ${itemLesson.elem.name} to ${studentInLesson.grade}", status = HttpStatusCode.OK)
                    } else {
                        call.respondText("Student not found in lesson", status = HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}