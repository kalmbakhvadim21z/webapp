package component.group

import csstype.px
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.span
import react.useRef
import ru.altmanea.webapp.data.Student
import web.html.HTMLInputElement

external interface ChangeGroupProps : Props {
    var oldGroup: String
    var saveGroup: (String) -> Unit
}

val CChangeGroup = FC<ChangeGroupProps>("ChangeGroup") { props ->
    val newGroup = useRef<HTMLInputElement>()
    span {
        input {
            defaultValue = props.oldGroup
            ref = newGroup
        }
        button {
            +"✓"
            onClick = {
                newGroup.current?.value?.let { group ->
                    props.saveGroup(group)
                }
            }
        }
    }
}