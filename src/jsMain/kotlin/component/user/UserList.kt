package component.user

import component.template.EditAddProps
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.router.dom.Link
import react.useState
import ru.altmanea.webapp.access.User

external interface UserListProps : Props {
    var users: List<User>
    var changeUser: (Pair<User, User>) -> Unit
    var deleteUser: (User) -> Unit
}

val CUserList = FC<UserListProps>("UserList") { props ->
    var editedId by useState("")
    ReactHTML.ol {
        props.users.forEach { user ->
            ReactHTML.li {
                if (editedId == user.username + user.password) {
                    CChangeUser {
                        oldUser = user
                        saveUser = {
                            props.changeUser(Pair(user, it))
                            editedId = ""
                        }
                    }
                }
                CUserItem {
                    this.user = user
                }
                ReactHTML.button {
                    +"✕"
                    onClick = {
                        props.deleteUser(user)
                    }
                }
                ReactHTML.button {
                    +"✎"
                    onClick = {
                        editedId = user.username + user.password
                    }
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