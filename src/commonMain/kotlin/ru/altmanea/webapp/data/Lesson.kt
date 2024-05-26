package ru.altmanea.webapp.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.altmanea.webapp.common.ItemId


@Serializable
class Lesson(
    val name: String,
    val students: Array<GradeInfo> = emptyArray()
) {
    fun addStudent(studentId: StudentId) =
        Lesson(
            name,
            students + GradeInfo (studentId, null)
        )

    fun removeStudent(studentId: StudentId) =
        Lesson(
            name,
            students.filter { it.studentId != studentId }.toTypedArray()
        )
}

@Serializable
class GradeInfo(
    val studentId: StudentId,
    var grade: Grade?
)

typealias LessonId = ItemId

val Lesson.json
    get() = Json.encodeToString(this)

