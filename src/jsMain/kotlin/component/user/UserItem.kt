package component.user

import react.FC
import react.Props
import react.dom.html.ReactHTML
import ru.altmanea.webapp.access.User

external interface UserItemProps : Props {
    var user: User
}

val CUserItem = FC<UserItemProps>("UserItem") { props ->
    ReactHTML.div {
        props.user.let { user ->
            +user.formateFullName()
        }
    }
}