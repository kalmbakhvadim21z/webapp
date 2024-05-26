package component.student

import component.template.EditAddProps
import react.FC
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.span
import react.useState
import ru.altmanea.webapp.data.Student
import web.html.InputType

val CStudentAdd = FC<EditAddProps<Student>>("StudentAdd") { props ->
    var firstname by useState("")
    var surname by useState("")
    var group by useState("")
    div {
        label {
            +"Firstname"
        }
        input {
            type = InputType.text
            value = firstname
            onChange = { firstname = it.target.value }
        }
    }
    div {
        label {
            +"Surname"
        }
        input {
            type = InputType.text
            value = surname
            onChange = { surname = it.target.value }
        }
    }
    div {
        label {
            +"Group"
        }
        input {
            type = InputType.text
            value = group
            onChange = { group = it.target.value }
        }
        button {
            +"✓"
            onClick = {
                props.saveElement(Student(firstname, surname, group))
            }
        }
    }
}
