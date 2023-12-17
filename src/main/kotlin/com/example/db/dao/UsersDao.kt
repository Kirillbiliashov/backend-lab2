package com.example.db.dao

import com.example.db.DbSingleton
import com.example.db.DbSingleton.dbQuery
import com.example.db.orm.Currencies
import com.example.db.orm.Users
import com.example.model.Currency
import com.example.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface UsersDao {
    suspend fun insert(user: User): Int

    suspend fun delete(id: Int)

    suspend fun get(id: Int): User?

    suspend fun  getAll(): List<User>
}

class UsersDaoImpl : UsersDao {

    private fun resultRowToUser(row: ResultRow): User = User(
        id = row[Users.id],
        name = row[Users.name],
        currencyId = row[Users.currencyId]
    )

    override suspend fun insert(user: User): Int = dbQuery {
        Users.insert {
            it[Users.name] = user.name
            it[Users.currencyId] = user.currencyId
        }[Users.id]
    }

    override suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id eq id }
        }
    }

    override suspend fun get(id: Int) = dbQuery {
        Users.select { Users.id eq id }
            .mapNotNull(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun getAll() = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

}