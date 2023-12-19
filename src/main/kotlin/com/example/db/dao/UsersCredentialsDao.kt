package com.example.db.dao

import com.example.db.DbSingleton.dbQuery
import com.example.db.orm.UsersCredentials
import com.example.model.UserCredentials
import com.example.model.hashedPassword
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import javax.management.Query.and


interface UsersCredentialsDao {

    suspend fun exists(creds: UserCredentials): Boolean

    suspend fun insert(creds: UserCredentials)
}

class UsersCredentialsDaoImpl : UsersCredentialsDao {
    override suspend fun exists(creds: UserCredentials) = dbQuery {
        val hashedPwd = creds.hashedPassword()
        UsersCredentials.select {
            (UsersCredentials.username eq creds.username) and (UsersCredentials.password eq hashedPwd)
        }.any()
    }

    override suspend fun insert(creds: UserCredentials) {
        dbQuery {
            UsersCredentials.insert {
                it[username] = creds.username
                it[password] = creds.hashedPassword()
            }
        }
    }

}
