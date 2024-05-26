package component.lesson

import component.CGrade
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.tr
import ru.altmanea.webapp.common.Item
import ru.altmanea.webapp.common.ItemId
import ru.altmanea.webapp.data.Lesson


external interface GradeEditProps : Props {
    var students: Array<GradeInfoFull>
    var changeStudents: (Array<GradeInfoFull>) -> Unit
    var deleteStudentFromLesson: (Pair<ItemId, ItemId>) -> Unit
    var item: Item<Lesson>
}

val CGradeEdit = FC<GradeEditProps>("GradeEdit") { props ->
    table {
        tbody {
            props.students.map { itemGradePair ->
                tr {
                    td {
                        +itemGradePair.itemStudent.elem.fullname()
                    }
                    td {
                        CGrade {
                            init = itemGradePair.grade
                            change = { newGrade ->
                                val newStudents = props.students.map {
                                    if (it.itemStudent.id == itemGradePair.itemStudent.id)
                                        it.newGrade(newGrade)
                                    else
                                        it
                                }.toTypedArray()
                                props.changeStudents(newStudents)
                            }
                        }
                        button {
                            +"x"
                            onClick = {
                                props.deleteStudentFromLesson(Pair(props.item.id, itemGradePair.itemStudent.id))
                            }
                        }
                    }
                }
            }
        }
    }
}