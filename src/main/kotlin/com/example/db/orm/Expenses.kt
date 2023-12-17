package com.example.db.orm

import org.jetbrains.exposed.sql.Table

object Expenses: Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("userId").references(Users.id)
    val categoryId = integer("categoryId").references(Categories.id)
    val timestamp = long("timestamp")
    val sum = double("sum")
    override val primaryKey = PrimaryKey(id)
}
//"jdbc:mysql://localhost:3306/your_database"