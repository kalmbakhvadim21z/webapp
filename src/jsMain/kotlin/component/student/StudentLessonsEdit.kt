package component.student

import component.CGrade
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import ru.altmanea.webapp.common.Item
import ru.altmanea.webapp.common.ItemId
import ru.altmanea.webapp.data.Grade
import ru.altmanea.webapp.data.Lesson

external interface StudentLessonsEditProps : Props {
    var lessons: List<Item<Lesson>>
    var deleteLesson: (ItemId) -> Unit
    var changeGrade: (Pair<ItemId, Int>) -> Unit
    var studentId: String
}

val CStudentLessonsEdit = FC<StudentLessonsEditProps>("StudentLessonsEdit") { props ->
    ol {
        props.lessons.forEach { lesson ->
            li {
                +lesson.elem.name
                CGrade {
                    this.init = lesson.elem.students.find { it.studentId == props.studentId }?.grade
                    change = {
                        val newGrade = Grade.valueOf(it!!.name).mark
                        props.changeGrade(Pair(lesson.id, newGrade))
                    }
                }
                button {
                    +"x"
                    onClick = {
                        props.deleteLesson(lesson.id)
                    }
                }
            }
        }
    }
}