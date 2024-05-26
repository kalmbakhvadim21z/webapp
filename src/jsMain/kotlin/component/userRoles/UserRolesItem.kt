package component.userRoles

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import ru.altmanea.webapp.access.Role
import ru.altmanea.webapp.access.User

external interface UserRolesItemProps : Props {
    var userRole: Pair<User, Set<Role>>
}

val CUserRolesItem = FC<UserRolesItemProps>("UserItem") { props ->
    div {
        props.userRole.let {
            +"User: \"${it.first.formateFullName()}\", Role: ${it.second.joinToString { it.name }}"
        }
    }
}