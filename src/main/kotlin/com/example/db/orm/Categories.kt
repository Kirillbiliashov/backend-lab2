package com.example.db.orm

import org.jetbrains.exposed.sql.Table

object Categories: Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    override val primaryKey = PrimaryKey(id)
}