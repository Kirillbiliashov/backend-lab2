package com.example.db

import com.example.db.orm.*
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DbSingleton {
    fun init() {
        val driver = "org.postgresql.Driver"
        val database = Database.connect(
            System.getenv("dbUrl"),
            driver,
            System.getenv("dbUser"),
            System.getenv("dbPass")
        )
        transaction(database) {
            SchemaUtils.create(Currencies)
            SchemaUtils.create(Users)
            SchemaUtils.create(Categories)
            SchemaUtils.create(Expenses)
            SchemaUtils.create(UsersCredentials)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}