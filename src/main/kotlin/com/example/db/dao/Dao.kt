package com.example.db.dao

import com.example.model.Category
import com.example.model.Currency
import org.jetbrains.exposed.sql.ResultRow

interface Dao<T> {

    fun resultRowToEntity(row: ResultRow): T

    suspend fun insert(entity: T): Int

    suspend fun delete(id: Int)

    suspend fun get(id: Int): T?

    suspend fun getAll(): List<T>
}