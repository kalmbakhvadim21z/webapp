package component.student

import js.core.get
import js.core.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import query.QueryError
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import react.router.Params
import react.router.dom.Link
import react.router.useParams
import react.useContext
import ru.altmanea.webapp.common.Item
import ru.altmanea.webapp.common.ItemId
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Lesson
import tanstack.query.core.QueryKey
import tanstack.react.query.useMutation
import tanstack.react.query.useQuery
import tanstack.react.query.useQueryClient
import tools.HTTPResult
import tools.fetch
import tools.fetchText
import userInfoContext
import kotlin.js.json

val CStudentLessons = FC<Props>("StudentLessons") {
    val params: Params = useParams()
    val userInfo = useContext(userInfoContext)
    val studentId = params["studentId"]
    val myQueryKey = arrayOf("lessons").unsafeCast<QueryKey>()
    val queryClient = useQueryClient()
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = arrayOf("lessons").unsafeCast<QueryKey>(),
        queryFn = {
            fetchText("${Config.studentsPath}students/${studentId}/lessons",
                jso {
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                }
            )
        }
    )
    val updateMutation = useMutation<HTTPResult, Any, Pair<ItemId, Int>, Any>(
        { (id, grade): Pair<ItemId, Int> ->
            fetch(
                "${Config.studentsPath}grade/${studentId}/lessons/${id}",
                jso {
                    method = "PUT"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(Pair(id, grade))
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(myQueryKey)
            }
        }
    )
    val deleteMutation = useMutation<HTTPResult, Any, ItemId, Any>(
        { id: ItemId ->
            fetch(
                "${Config.studentsPath}delete/${studentId}/lessons/${id}",
                jso {
                    method = "DELETE"
                    headers = json(
                        "Authorization" to userInfo?.second?.authHeader
                    )
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(myQueryKey)
            }
        }
    )
    if (query.isLoading) {
        div { +"Loading..." }
    } else if (query.isError) {
        div { +"Error!" }
    } else {
        h1 { +"Уроки студента" }
        val lessons = Json.decodeFromString<List<Item<Lesson>>>(query.data ?: "")
        div {
            if (lessons.isEmpty()) {
                +"Нет уроков у студента"
            } else {
                CStudentLessonsEdit {
                    this.lessons = lessons
                    this.deleteLesson = {
                        deleteMutation.mutateAsync(it, null)
                    }
                    this.changeGrade = {
                        updateMutation.mutateAsync(it, null)
                    }
                    this.studentId = studentId ?: ""
                }
            }
        }
    }
    div {
        Link {
            to = "/students"
            +"Back"
            return@Link
        }
    }
}