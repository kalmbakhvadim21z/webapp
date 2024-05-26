package component.userRoles

import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useState
import ru.altmanea.webapp.access.Role
import web.html.InputType

external interface ChangeUserRoleProps : Props {
    var roles: List<Role>
    var saveRole: (Set<Role>) -> Unit
}

val CChangeUserRole = FC<ChangeUserRoleProps>("ChangeUserRole") { props ->
    val (selectedRoles, setSelectedRoles) = useState<Set<Role>>(emptySet())
    div {
        props.roles.forEach { role ->
            label {
                input {
                    type = InputType.checkbox
                    checked = selectedRoles.contains(role)
                    onChange = {
                        val newSelectedRoles = if (selectedRoles.contains(role)) {
                            selectedRoles - role
                        } else {
                            selectedRoles + role
                        }
                        setSelectedRoles(newSelectedRoles)
                    }
                }
                +role.name
            }
        }
        button {
            +"✓"
            onClick = {
                props.saveRole(selectedRoles)
            }
        }
    }
}