package component.userRoles

import csstype.px
import emotion.react.css
import js.core.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import query.QueryError
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.useContext
import ru.altmanea.webapp.access.Role
import ru.altmanea.webapp.access.User
import ru.altmanea.webapp.config.Config
import tanstack.query.core.QueryKey
import tanstack.react.query.useMutation
import tanstack.react.query.useQuery
import tanstack.react.query.useQueryClient
import tools.HTTPResult
import tools.fetch
import tools.fetchText
import userInfoContext
import kotlin.js.json

val containerUserRolesList = FC<Props>("QueryUserRolesList") {
    val queryClient = useQueryClient()
    val userInfo = useContext(userInfoContext)
    val userRolesListQueryKey = arrayOf("userRolesList").unsafeCast<QueryKey>()
    val rolesListQueryKey = arrayOf("rolesList").unsafeCast<QueryKey>()
    val queryUsersRoles = useQuery<String, QueryError, String, QueryKey>(
        queryKey = userRolesListQueryKey,
        queryFn = {
            fetchText(
                Config.usersRolesPath,
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        },

    )
    val queryRoles = useQuery<String, QueryError, String, QueryKey>(
        queryKey = rolesListQueryKey,
        queryFn = {
            fetchText(
                "${Config.usersRolesPath}roles",
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    val updateUserRolesMutation = useMutation<HTTPResult, Any, Pair<User, Set<Role>>, Any>(
        mutationFn = { user: Pair<User, Set<Role>> ->
            fetch(
                Config.usersRolesPath,
                jso {
                    method = "PUT"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(user)
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(userRolesListQueryKey)
            }
        }
    )
    if (queryUsersRoles.isLoading || queryRoles.isLoading) div { +"Loading .." }
    else if (queryUsersRoles.isError || queryRoles.isError) div { +"Error!" }
    else {
        val itemsUserRoles = Json.decodeFromString<List<Pair<User, Set<Role>>>>(queryUsersRoles.data ?: "")
        val itemsRoles = Json.decodeFromString<List<Role>>(queryRoles.data ?: "")
        CUserRolesList {
            this.usersRoles = itemsUserRoles
            this.changeUserRoles = {
                updateUserRolesMutation.mutateAsync(it, null)
            }
            this.roles = itemsRoles
        }
        div {
            css {
                fontSize = 16.px
            }
            h3 { +"Примечание" }
            +"Доступны роли: ${itemsRoles.joinToString(separator = ", ") { it.name }}; "
            +"user - доступно только чтение; admin - доступно все. "
        }
    }
}