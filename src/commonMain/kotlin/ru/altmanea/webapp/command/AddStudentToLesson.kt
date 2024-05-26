package ru.altmanea.webapp.command

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.altmanea.webapp.access.User
import ru.altmanea.webapp.data.LessonId
import ru.altmanea.webapp.data.StudentId

@Serializable
class AddStudentToLesson(
    val lessonId: LessonId,
    val studentId: StudentId,
    val version: Long
){
    companion object {
        const val path="addStudent"
    }
}

val AddStudentToLesson.json
    get() = Json.encodeToString(this)