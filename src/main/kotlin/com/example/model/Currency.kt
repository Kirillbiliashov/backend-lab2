package com.example.model

@kotlinx.serialization.Serializable
data class Currency(val id: Int = 0, val name: String, val country: String)

