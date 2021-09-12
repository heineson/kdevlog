package io.github.heineson.kdevlog.store

import java.util.concurrent.ConcurrentHashMap

object InputStore : Store<InputEntity> {
    private val store = ConcurrentHashMap<String, InputEntity>()

    override fun saveAll(entities: Collection<InputEntity>) = store.putAll(entities.map { Pair(it.id, it) })

    override fun save(entity: InputEntity) {
        store[entity.id] = entity
    }

    override fun getAll(): List<InputEntity> = store.values.toList()

    override fun get(id: String): InputEntity? = store[id]

    override fun delete(id: String) {
        store.remove(id)
    }
}

enum class InputType { FILE }
data class InputEntity(val id: String, val type: InputType)
