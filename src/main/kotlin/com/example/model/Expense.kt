package com.example.model

import kotlinx.serialization.SerialName
import java.time.LocalDateTime

@kotlinx.serialization.Serializable
data class Expense(
    override val id: Int = 0,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("category_id")
    val categoryId: Int,
    val timestamp: String = LocalDateTime.now().toString(),
    val sum: Double,
    val currencyId: Int = 1
): IdCopyable<Expense>() {
    override fun copy(newId: Int) = copy(id = newId)
}
