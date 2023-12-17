package com.example.db.dao

import com.example.db.DbSingleton
import com.example.db.DbSingleton.dbQuery
import com.example.db.orm.Currencies
import com.example.db.orm.Users
import com.example.model.Currency
import com.example.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class UsersDaoImpl : Dao<User> {

    override fun resultRowToEntity(row: ResultRow): User = User(
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
            .mapNotNull(::resultRowToEntity)
            .singleOrNull()
    }

    override suspend fun getAll() = dbQuery {
        Users.selectAll().map(::resultRowToEntity)
    }

}