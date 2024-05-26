package component.lesson

import invalidateRepoKey
import js.core.jso
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import query.QueryError
import react.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import ru.altmanea.webapp.command.AddStudentToLesson
import ru.altmanea.webapp.common.Item
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Lesson
import ru.altmanea.webapp.data.Student
import ru.altmanea.webapp.data.StudentId
import tanstack.query.core.QueryKey
import tanstack.react.query.useMutation
import tanstack.react.query.useQuery
import tanstack.react.query.useQueryClient
import tools.HTTPResult
import tools.fetch
import tools.fetchText
import userInfoContext
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import kotlin.js.json

external interface StudentSelectProps : Props {
    var startName: String
    var onPick: (StudentId) -> Unit
}

val CStudentSelect = FC<StudentSelectProps>("StudentSelect") { props ->
    val selectQueryKey = arrayOf("StudentSelect", props.startName).unsafeCast<QueryKey>()
    val userInfo = useContext(userInfoContext)

    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = selectQueryKey,
        queryFn = {
            fetchText(
                "${Config.studentsPath}ByStartName/${props.startName}",
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    val selectRef = useRef<HTMLSelectElement>()
    val students: List<Item<Student>> =
        try {
            Json.decodeFromString(query.data ?: "")
        } catch (e: Throwable) {
            emptyList()
        }
    select {
        ref = selectRef
        students.map {
            option {
                +it.elem.fullname()
                value = it.id
            }
        }
    }
    button {
        +"Add"
        onClick = {
            selectRef.current?.value?.let {
                props.onPick(it)
            }
        }
    }
}

external interface AddStudentProps : Props {
    var lesson: Item<Lesson>
}

val CAddStudentToLesson = FC<AddStudentProps>("AddStudent") { props ->
    val queryClient = useQueryClient()
    val invalidateRepoKey = useContext(invalidateRepoKey)
    var input by useState("")
    val inputRef = useRef<HTMLInputElement>()
    val userInfo = useContext(userInfoContext)

    val addStudentMutation = useMutation<HTTPResult, Any, StudentId, Any>(
        mutationFn = { studentId ->
            fetch(
                "${Config.lessonsPath}/${AddStudentToLesson.path}",
                jso {
                    method = "POST"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(AddStudentToLesson(
                        props.lesson.id,
                        studentId,
                        props.lesson.version
                    ))
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(invalidateRepoKey)
            }
        }
    )

    div {
        input {
            ref = inputRef
            list = "StudentsHint"
            onChange = { input = it.target.value }
        }
        CStudentSelect {
            startName = input
            onPick = {
                addStudentMutation.mutateAsync(it, null)
            }
        }
    }
}
