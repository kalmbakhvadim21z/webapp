package component.user

import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.span
import react.useRef
import ru.altmanea.webapp.access.User
import web.html.HTMLInputElement

external interface ChangeUserProps : Props {
    var oldUser: User
    var saveUser: (User) -> Unit
}

val CChangeUser = FC<ChangeUserProps>("ChangeUser") { props ->
    val newLoginRef = useRef<HTMLInputElement>()
    val newPasswordRef = useRef<HTMLInputElement>()
    span {
        input {
            defaultValue = props.oldUser.username
            ref = newLoginRef
        }
        input {
            defaultValue = props.oldUser.password
            ref = newPasswordRef
        }
        button {
            +"✓"
            onClick = {
                newLoginRef.current?.value?.let { newLogin ->
                    newPasswordRef.current?.value?.let { newPassword ->
                        props.saveUser(User(newLogin, newPassword))
                    }
                }
            }
        }
    }
}