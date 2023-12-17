package com.example.db.dao

import com.example.db.DbSingleton.dbQuery
import com.example.db.orm.Currencies
import com.example.model.Currency
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

interface CurrenciesDao {

    suspend fun insert(currency: Currency): Int

    suspend fun delete(id: Int)

    suspend fun get(id: Int): Currency?
}

class CurrenciesDaoImpl: CurrenciesDao {

    private fun resultRowToCurrency(row: ResultRow): Currency = Currency(
        id = row[Currencies.id],
        name = row[Currencies.name],
        country = row[Currencies.country]
    )

    override suspend fun insert(currency: Currency): Int  = dbQuery {
           Currencies.insert {
               it[Currencies.name] = currency.name
               it[country] = currency.country
           }[Currencies.id]
       }


    override suspend fun delete(id: Int) {
        dbQuery {
            Currencies.deleteWhere { Currencies.id eq id }
        }
    }

    override suspend fun get(id: Int) = dbQuery {
        Currencies.select { Currencies.id eq id }
            .mapNotNull(::resultRowToCurrency)
            .singleOrNull()
    }

}