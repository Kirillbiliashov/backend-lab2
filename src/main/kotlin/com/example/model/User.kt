package com.example.model

@kotlinx.serialization.Serializable
data class User(override val id: Int = 0, val name: String) : IdCopyable<User>() {
    override fun copy(newId: Int) = copy(id = newId)
}
