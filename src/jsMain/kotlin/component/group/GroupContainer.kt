package component.group

import js.core.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import query.QueryError
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useContext
import ru.altmanea.webapp.common.Item
import ru.altmanea.webapp.common.ItemId
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

val containerGroupList = FC<Props>("QueryGroupList") {
    val queryClient = useQueryClient()
    val userInfo = useContext(userInfoContext)
    val groupListQueryKey = arrayOf("groupList").unsafeCast<QueryKey>()
    val queryGroups = useQuery<String, QueryError, String, QueryKey>(
        queryKey = groupListQueryKey,
        queryFn = {
            fetchText(Config.groupsPath,
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        },
    )
    val addGroupMutation = useMutation<HTTPResult, Any, String, Any>(
        mutationFn = { group: String ->
            fetch(
                Config.groupsPath,
                jso {
                    method = "POST"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(group)
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(groupListQueryKey)
            }
        }
    )
    val deleteGroupMutation = useMutation<HTTPResult, Any, ItemId, Any>(
        mutationFn = { groupId: ItemId ->
            fetch(
                "${Config.groupsPath}$groupId",
                jso {
                    method = "DELETE"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(groupListQueryKey)
            }
        }
    )
    val updateGroupMutation = useMutation<HTTPResult, Any, Item<String>, Any>(
        mutationFn = { groupItem: Item<String> ->
            fetch(
                "${Config.groupsPath}${groupItem.id}",
                jso {
                    method = "PUT"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(groupItem.elem)
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(groupListQueryKey)
            }
        }
    )
    if (queryGroups.isLoading) div { +"Loading .." }
    else if (queryGroups.isError) div { +"Error!" }
    else {
        val items = Json.decodeFromString<Array<Item<String>>>(queryGroups.data ?: "")
        CAddGroup {
            this.addGroup = {
                addGroupMutation.mutateAsync(it, null)
            }
        }
        CGroupList {
            this.groups = items
            this.changeGroup = {
                updateGroupMutation.mutateAsync(it, null)
            }
            this.deleteGroup = {
                deleteGroupMutation.mutateAsync(it, null)
            }
        }
    }
}