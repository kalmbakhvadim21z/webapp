package component.student

import component.template.ElementInListProps
import react.FC
import react.dom.html.ReactHTML.span
import react.router.dom.Link
import ru.altmanea.webapp.data.Student


val CStudentInList = FC<ElementInListProps<Student>>("StudentInList") { props ->
    Link {
        to = "/students/${props.id}/lessons"
        +props.element.fullnameWithGroup()
    }
}