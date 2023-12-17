package com.example.db.dao

import com.example.db.DbSingleton
import com.example.db.DbSingleton.dbQuery
import com.example.db.orm.Categories
import com.example.db.orm.Currencies
import com.example.db.orm.Users
import com.example.model.Category
import com.example.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class CategoriesDaoImpl : Dao<Category> {

    override fun resultRowToEntity(row: ResultRow): Category = Category(
        id = row[Categories.id],
        name = row[Categories.name]
    )

    override suspend fun insert(category: Category) = dbQuery {
        Categories.insert {
            it[name] = category.name
        }[Categories.id]
    }

    override suspend fun delete(id: Int) {
        dbQuery {
            Categories.deleteWhere { Categories.id eq id }
        }
    }

    override suspend fun get(id: Int) = dbQuery {
        Categories.select { Categories.id eq id }
            .mapNotNull(::resultRowToEntity)
            .singleOrNull()
    }

    override suspend fun getAll() = dbQuery {
        Categories.selectAll().map(::resultRowToEntity)
    }

}