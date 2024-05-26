package ru.altmanea.webapp.access

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.altmanea.webapp.data.Student

@Serializable
open class User(
    val username: String,
    val password: String
) {
    fun fullName() = "$username, $password"
    fun formateFullName() = "login: $username; password: $password"
}

val User.json
    get() = Json.encodeToString(this)

