package com.example.model

@kotlinx.serialization.Serializable
data class User(val id: Int = 0, val name: String, val currencyId: Int = 1)