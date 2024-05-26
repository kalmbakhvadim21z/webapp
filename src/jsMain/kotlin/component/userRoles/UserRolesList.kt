package component.userRoles

import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import react.router.dom.Link
import react.useState
import ru.altmanea.webapp.access.Role
import ru.altmanea.webapp.access.User

external interface UserRolesListProps : Props {
    var usersRoles: List<Pair<User, Set<Role>>>
    var roles: List<Role>
    var changeUserRoles: (Pair<User, Set<Role>>) -> Unit
}

val CUserRolesList = FC<UserRolesListProps>("UserRolesList") { props ->
    var editedId by useState("")
    ol {
        props.usersRoles.forEach { (user, roles) ->
            li {
                if (editedId == user.username + user.password) {
                    CChangeUserRole {
                        this.roles = props.roles
                        this.saveRole = {
                            props.changeUserRoles(Pair(user, it))
                            editedId = ""
                        }
                    }
                }
            }
            CUserRolesItem {
                this.userRole = Pair(user, roles)
            }
            button {
                +"✎"
                onClick = {
                    editedId = user.username + user.password
                }
            }
        }
    }
    ReactHTML.div {
        Link {
            to = "/"
            +"Back"
        }
    }
}