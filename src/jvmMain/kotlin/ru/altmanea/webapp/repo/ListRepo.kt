package ru.altmanea.webapp.repo

import ru.altmanea.webapp.common.Item
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class RepoElement<E>(
    val elem: E,
    val version: Long
)

class ListRepo<E> : Repo<E> {
    private val list = ConcurrentHashMap<String, RepoElement<E>>()
    private fun version() = System.currentTimeMillis()

    override fun create(element: E): Boolean =
        true.apply {
            list[UUID.randomUUID().toString()] =
                RepoElement(element, version())
        }

    override fun read(): List<Item<E>> =
        list.map {
            Item(it.value.elem, it.key, it.value.version)
        }

    override fun read(id: String): Item<E>? =
        list[id]?.let {
            Item(it.elem, id, it.version)
        }

    override fun read(ids: List<String>): List<Item<E>> =
        ids.mapNotNull { id ->
            list[id]?.let {
                Item(it.elem, id, it.version)
            }
        }

    override fun update(item: Item<E>): Boolean =
        list[item.id]?.let {
            if (it.version==item.version){
                list[item.id]= RepoElement(item.elem, version())
                true
            } else {
                false
            }
        }?: false

    override fun delete(id: String): Boolean =
        list.remove(id) != null

}