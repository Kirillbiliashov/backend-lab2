package com.example.model

@kotlinx.serialization.Serializable
data class Category(override val id: Int = 0, val name: String): IdCopyable<Category>() {
    override fun copy(newId: Int) = this.copy(id = newId)

}
