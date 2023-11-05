package com.example.model

class ModelStorage<T> where T: IdCopyable<T> {

    private var inserts = 0
    private var _values = mutableListOf<T>()

    fun values() = _values.toList()

    fun add(entry: T): Int {
        _values.add(entry.copy(++inserts))
        return inserts
    }


    fun delete(id: Int) {
        val record = _values.find { it.id == id }
        _values.remove(record)
    }

    fun get(id: Int) = _values.first { it.id == id }


}

@kotlinx.serialization.Serializable
sealed class IdCopyable<T> {

    abstract val id: Int
    abstract fun copy(newId: Int): T
}


