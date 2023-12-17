package com.example.db.orm

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DbSingleton {
    fun init() {
        val driver = "com.mysql.cj.jdbc.Driver"
        val url = "jdbc:mysql://localhost:3306/BackendLabs"
        val database = Database.connect(url, driver, "root", "root")
        transaction(database) {
            SchemaUtils.create(Currencies)
            SchemaUtils.create(Users)
            SchemaUtils.create(Categories)
            SchemaUtils.create(Expenses)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}