package component.group

import react.FC
import react.Props
import react.dom.html.ReactHTML

external interface GroupItemProps : Props {
    var group: String
}

val CGroupItem = FC<GroupItemProps>("GroupItem") { props ->
    ReactHTML.div {
        props.group.let {
            +it
        }
    }
}