package com.example.db.orm

import org.jetbrains.exposed.sql.Table


object UsersCredentials : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50)
    val password = varchar("password", 100)
    override val primaryKey = PrimaryKey(id)
}