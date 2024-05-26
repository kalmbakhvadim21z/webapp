package component.user

import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.details
import react.dom.html.ReactHTML.summary
import react.useRef
import ru.altmanea.webapp.access.User
import web.html.HTMLInputElement

external interface AddUserProps : Props {
    var addUser: (User) -> Unit
}

val CAddUser = FC<AddUserProps>("AddUser") { props ->
    details {
        summary { +"Add new user" }
        val loginRef = useRef<HTMLInputElement>()
        val passwordRef = useRef<HTMLInputElement>()
        ReactHTML.div {
            ReactHTML.input {
                ref = loginRef
            }
            ReactHTML.input {
                ref = passwordRef
            }
            ReactHTML.button {
                +"Add user"
                onClick = {
                    loginRef.current?.value?.let { login ->
                        passwordRef.current?.value?.let { password ->
                            props.addUser(User(login, password))
                        }
                    }
                }
            }
        }
    }
}