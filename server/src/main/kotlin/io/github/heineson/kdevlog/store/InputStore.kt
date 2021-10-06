package io.github.heineson.kdevlog.store

import io.github.heineson.kdevlog.model.Input
import java.util.concurrent.ConcurrentHashMap

class InputStore : Store<Input> {
    private val store = ConcurrentHashMap<String, Input>()

    override fun saveAll(entities: Collection<Input>) = store.putAll(entities.map { Pair(it.id, it) })

    override fun save(entity: Input): Input {
        store[entity.id] = entity
        return entity
    }

    override fun getAll(): List<Input> = store.values.toList()

    override fun get(id: String): Input? = store[id]

    override fun delete(id: String): Input? = store.remove(id)
}
