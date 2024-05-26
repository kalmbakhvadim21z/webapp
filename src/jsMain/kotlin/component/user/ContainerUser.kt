package component.user

import csstype.px
import emotion.react.css
import js.core.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import query.QueryError
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.useContext
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

val containerUserList = FC<Props>("QueryUserList") {
    val queryClient = useQueryClient()
    val userInfo = useContext(userInfoContext)
    val userListQueryKey = arrayOf("userList").unsafeCast<QueryKey>()
    val queryUsers = useQuery<String, QueryError, String, QueryKey>(
        queryKey = userListQueryKey,
        queryFn = {
            fetchText(
                Config.usersPath,
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        },
    )
    val addUserMutation = useMutation<HTTPResult, Any, User, Any>(
        mutationFn = { user: User ->
            fetch(
                Config.usersPath,
                jso {
                    method = "POST"
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
                queryClient.invalidateQueries<Any>(userListQueryKey)
            }
        }
    )
    val deleteUserMutation = useMutation<HTTPResult, Any, User, Any>(
        mutationFn = { user: User ->
            fetch(
                Config.usersPath,
                jso {
                    method = "DELETE"
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
                queryClient.invalidateQueries<Any>(userListQueryKey)
            }
        }
    )
    val updateUserMutation = useMutation<HTTPResult, Any, Pair<User, User>, Any>(
        mutationFn = { users: Pair<User, User> ->
            fetch(
                Config.usersPath,
                jso {
                    method = "PUT"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(users)
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(userListQueryKey)
            }
        }
    )
    if (queryUsers.isLoading) div { +"Loading .." }
    else if (queryUsers.isError) div { +"Error!" }
    else {
        val items = Json.decodeFromString<List<User>>(queryUsers.data ?: "")
        CAddUser {
            this.addUser = {
                addUserMutation.mutateAsync(it, null)
            }
        }
        CUserList {
            this.users = items
            this.changeUser = {
                updateUserMutation.mutateAsync(it, null)
            }
            this.deleteUser = {
                deleteUserMutation.mutateAsync(it, null)
            }
        }
        div {
            css {
               fontSize = 16.px
            }
            h3 { +"Примечание" }
            +"Нельзя удалять текущего пользователя, "
            +"Нельзя изменять логин текущего пользователя, "
            +"При добавлении нового пользователя - он будет создан с ролью user."
        }
    }
}