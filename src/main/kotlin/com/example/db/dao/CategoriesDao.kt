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

interface CategoriesDao {
    suspend fun insert(category: Category): Int

    suspend fun delete(id: Int)

    suspend fun get(id: Int): Category?

    suspend fun getAll(): List<Category>
}

class CategoriesDaoImpl : CategoriesDao {

    private fun resultRowToCategory(row: ResultRow): Category = Category(
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
            .mapNotNull(::resultRowToCategory)
            .singleOrNull()
    }

    override suspend fun getAll() = dbQuery {
        Categories.selectAll().map(::resultRowToCategory)
    }

}