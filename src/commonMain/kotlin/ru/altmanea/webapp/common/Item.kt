package ru.altmanea.webapp.common

import kotlinx.serialization.Serializable

typealias ItemId = String

@Serializable
class Item<E>(
    val elem: E,
    val id: ItemId,
    val version: Long
)