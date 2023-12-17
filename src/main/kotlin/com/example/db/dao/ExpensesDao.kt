package com.example.db.dao

import com.example.db.DbSingleton
import com.example.db.DbSingleton.dbQuery
import com.example.db.orm.Expenses
import com.example.db.orm.Users
import com.example.model.Expense
import com.example.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface ExpensesDao {
    suspend fun insert(expense: Expense): Int

    suspend fun delete(id: Int)

    suspend fun get(id: Int): Expense?

    suspend fun getQuery(userId: Int?, categoryId: Int?): List<Expense>
}

class ExpensesDaoImpl: ExpensesDao {

    private fun resultRowToExpense(row: ResultRow): Expense = Expense(
        id = row[Expenses.id],
        userId = row[Expenses.userId],
        categoryId = row[Expenses.categoryId],
        timestamp = row[Expenses.timestamp],
        sum = row[Expenses.sum],
        currencyId = row[Expenses.currencyId]
    )

    override suspend fun insert(expense: Expense) = DbSingleton.dbQuery {
        Expenses.insert {
            it[Expenses.userId] = expense.userId
            it[Expenses.categoryId] = expense.categoryId
            it[Expenses.timestamp] = expense.timestamp
            it[Expenses.sum] = expense.sum
            it[Expenses.currencyId] = expense.currencyId
        }[Expenses.id]
    }

    override suspend fun delete(id: Int) {
        dbQuery {
            Expenses.deleteWhere { Expenses.id eq id }
        }
    }

    override suspend fun get(id: Int) = dbQuery {
        Expenses.select { Expenses.id eq id }
            .mapNotNull(::resultRowToExpense)
            .singleOrNull()
    }

    override suspend fun getQuery(userId: Int?, categoryId: Int?) = dbQuery {
        if (userId == null) {
            Expenses.select {
                Expenses.categoryId eq categoryId!!
            }.map(::resultRowToExpense)
        }
        if (categoryId == null) {
            Expenses.select {
                Expenses.userId eq userId!!
            }.map(::resultRowToExpense)
        }
        Expenses.select {
            (Expenses.userId eq userId!!) and (Expenses.categoryId eq categoryId!!)
        }.map(::resultRowToExpense)
    }

}