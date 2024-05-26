package ru.altmanea.webapp.repo

import ru.altmanea.webapp.common.Item
import ru.altmanea.webapp.data.Grade
import ru.altmanea.webapp.data.GradeInfo
import ru.altmanea.webapp.data.Lesson
import ru.altmanea.webapp.data.Student

val studentsRepo = ListRepo<Student>()
val lessonsRepo = ListRepo<Lesson>()
val groupsRepo = ListRepo<String>()

fun createTestData() {
    val studentsData = listOf(
        Student("Sheldon", "Cooper", "21z"),
        Student("Leonard", "Hofstadter", "22z"),
        Student("Howard", "Wolowitz", "23z"),
        Student("Penny", "Hofstadter", "21z"),
    ).apply {
        map {
            studentsRepo.create(it)
        }
    }
    val groups = studentsData.map { it.group }.toSet().sorted()
    groups.apply { map { groupsRepo.create(it) } }
    val lessonsData = listOf(
        Lesson("Math"),
        Lesson("Phys"),
        Lesson("Story"),
    ).apply {
        map {
            lessonsRepo.create(it)
        }
    }

    val students = studentsRepo.read()
    val lessons = lessonsRepo.read()
    val sheldon = students.findLast { it.elem.firstname == "Sheldon" }
    check(sheldon != null)
    val leonard = students.findLast { it.elem.firstname == "Leonard" }
    check(leonard != null)
    val math = lessons.findLast { it.elem.name == "Math" }
    check(math != null)
    val newMath = Lesson(
        math.elem.name,
        arrayOf(
            GradeInfo(sheldon.id, Grade.A),
            GradeInfo(leonard.id, Grade.B)
        )
    )
    lessonsRepo.update(Item(newMath, math.id, math.version))
}
