package com.example.db

import com.example.db.orm.Categories
import com.example.db.orm.Currencies
import com.example.db.orm.Expenses
import com.example.db.orm.Users
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DbSingleton {
    fun init() {
        val driver = "org.postgresql.Driver"
        val database = Database.connect(System.getenv("dbUrl"), driver)
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