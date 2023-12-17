package com.example.db.orm

import org.jetbrains.exposed.sql.Table

object Currencies: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 20)
    val country = varchar("country", 50)
    override val primaryKey = PrimaryKey(id)
}