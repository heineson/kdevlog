package io.github.heineson.kdevlog.store

interface Store<T> {
    fun saveAll(entities: Collection<T>)
    fun save(entity: T)
    fun getAll(): List<T>
    fun get(id: String): T?
    fun delete(id: String)
}
