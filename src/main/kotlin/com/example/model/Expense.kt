package com.example.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Expense(
    val id: Int,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("category_id")
    val categoryId: Int,
    val timestamp: String,
    val sum: Double
)
