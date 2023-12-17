package com.example.model

@kotlinx.serialization.Serializable
data class Currency(override val id: Int = 0, val name: String, val country: String): IdCopyable<Currency>() {
    override fun copy(newId: Int) = this.copy(id = newId)

}

