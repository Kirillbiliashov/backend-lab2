package com.example.model

import kotlinx.serialization.SerialName
import java.time.LocalDateTime

@kotlinx.serialization.Serializable
data class Expense(
    val id: Int = 0,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("category_id")
    val categoryId: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val sum: Double,
    val currencyId: Int = 1
)