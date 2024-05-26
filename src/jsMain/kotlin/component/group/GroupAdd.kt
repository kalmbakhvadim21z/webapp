package component.group

import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.details
import react.dom.html.ReactHTML.summary
import react.useRef
import web.html.HTMLInputElement

external interface AddGroupProps : Props {
    var addGroup: (String) -> Unit
}

val CAddGroup = FC<AddGroupProps>("AddGroup") { props ->
    details {
        summary { +"Add new group" }
        val groupRef = useRef<HTMLInputElement>()
        ReactHTML.div {
            ReactHTML.input {
                ref = groupRef
            }
            ReactHTML.button {
                +"Add"
                onClick = {
                    groupRef.current?.value?.let { group ->
                        props.addGroup(group)
                    }
                }
            }
        }
    }
}