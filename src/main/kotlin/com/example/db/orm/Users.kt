package com.example.db.orm

import org.jetbrains.exposed.sql.Table

object Users: Table() {

    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val categoryId = integer("categoryId").references(Currencies.id)

    override val primaryKey = PrimaryKey(id)
}