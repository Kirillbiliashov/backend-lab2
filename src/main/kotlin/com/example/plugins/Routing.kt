package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.example.db.dao.*
import com.example.model.*
import com.example.model.Currency
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*


private val currenciesDao = CurrenciesDaoImpl()
private val usersDao = UsersDaoImpl()
private val categoriesDao = CategoriesDaoImpl()
private val expensesDao = ExpensesDaoImpl()
private val usersCredentialsDao = UsersCredentialsDaoImpl()

fun Application.configureRouting() {
    install(Authentication) {
        jwt("app-validation") {
            challenge { defaultScheme, realm ->
                val message = if (call.request.headers.contains("Authorization")) {
                    "Missing auth token"
                } else {
                    "Auth token is not valid or has expired"
                }
                call.respond(HttpStatusCode.Unauthorized, message)
            }
            verifier(
                JWT
                    .require(Algorithm.HMAC256(System.getenv("secretKey")))
                    .withIssuer("backend-lab4")
                    .build()
            )
            validate {
                val username = it.payload.getClaim("username").asString()
                val password = it.payload.getClaim("password").asString()
                if (username != null && password != null &&
                    usersCredentialsDao.exists(UserCredentials(username, password))
                ) {
                    JWTPrincipal(it.payload)
                } else {
                    null
                }
            }
        }
    }
    routing {
        userRouting()
        categoryRouting()
        recordRouting()
        currencyRouting()
    }
    install(RequestValidation) {
        validate<User> { user ->
            val reasons = mutableListOf<String>();
            if (user.name.length < 2) reasons.add("User should have valid username")
            if (user.currencyId < 1) reasons.add("User should have valid currency id")
            if (reasons.isNotEmpty()) ValidationResult.Invalid(reasons)
            else ValidationResult.Valid
        }
        validate<Expense> { expense ->
            val reasons = mutableListOf<String>();
            if (expense.userId < 1) reasons.add("Expense should have valid user id")
            if (expense.categoryId < 1) reasons.add("Expense should have valid category id")
            if (expense.sum <= 0.0) reasons.add("Expense sum should be positive")
            if (expense.currencyId < 1) reasons.add("Expense should have valid currency id")
            if (reasons.isNotEmpty()) ValidationResult.Invalid(reasons)
            else ValidationResult.Valid
        }
        validate<Category> { category ->
            if (category.name.length < 3) ValidationResult.Invalid("Category should have valid name")
            else ValidationResult.Valid
        }
        validate<Currency> { currency ->
            val reasons = mutableListOf<String>()
            if (currency.country.length <= 2) reasons.add("Currency country should be valid")
            if (currency.name.length < 3) reasons.add("Currency name should be valid")
            if (reasons.isNotEmpty()) ValidationResult.Invalid(reasons)
            else ValidationResult.Valid
        }
        validate<UserCredentials> { credentials ->
            val reasons = mutableListOf<String>()
            if (credentials.username.length < 6) reasons.add("Username should be at least 6 symbols long")
            if (credentials.password.length < 6) reasons.add("Password should be at least 6 symbols long")
            if (reasons.isNotEmpty()) ValidationResult.Invalid(reasons)
            else ValidationResult.Valid
        }
    }
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
        }
        exception<java.lang.NumberFormatException> { call, cause ->
            call.respond(HttpStatusCode.UnprocessableEntity, "id should be valid int")
        }

    }
}

fun Routing.userRouting() {
    authenticate("app-validation") {
        get("/user/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val user = usersDao.get(id)
            if (user == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, user)
        }

        delete("/user/{id}") {
            val id = call.parameters["id"]!!.toInt()
            usersDao.delete(id)
            call.respond(HttpStatusCode.NoContent, "user $id successfully deleted")
        }
        post("/user") {
            val user = call.receive<User>()
            val id = usersDao.insert(user)
            call.respond(IdBody(id))
        }
        get("/users") {
            call.respond(usersDao.getAll())
        }
    }

    post("/user/signup") {
        val creds = call.receive<UserCredentials>()
        val token = creds.generateToken()
        usersCredentialsDao.insert(creds)
        call.respond(hashMapOf("token" to token))
    }
    post("/user/login") {
        val creds = call.receive<UserCredentials>()
        if (usersCredentialsDao.exists(creds)) {
            val token = creds.generateToken()
            call.respond(hashMapOf("token" to token))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid username or password")
        }
    }
}

fun Routing.categoryRouting() {
    authenticate("app-validation") {
        get("/category/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val category = categoriesDao.get(id)
            if (category == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, category)
        }
        delete("/category/{id}") {
            val id = call.parameters["id"]!!.toInt()
            categoriesDao.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
        post("/category") {
            val category = call.receive<Category>()
            val id = categoriesDao.insert(category)
            call.respond(IdBody(id))
        }
    }
}

fun Routing.currencyRouting() {
    authenticate("app-validation") {
        get("/currency/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val currency = currenciesDao.get(id)
            if (currency == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, currency)
        }
        delete("/currency/{id}") {
            val id = call.parameters["id"]!!.toInt()
            if (id <= 2) call.respond(HttpStatusCode.BadRequest, "Can't delete default currency")
            else {
                currenciesDao.delete(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
        post("/currency") {
            val currency = call.receive<Currency>()
            val id = currenciesDao.insert(currency)
            call.respond(IdBody(id))
        }
    }

}

fun Routing.recordRouting() {
    authenticate("app-validation") {
        get("/record/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val expense = expensesDao.get(id)
            if (expense == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.OK, expense)
        }

        delete("/record/{id}") {
            val id = call.parameters["id"]!!.toInt()
            expensesDao.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }

        post("/record") {
            val record = call.receive<Expense>()
            val id = expensesDao.insert(record)
            call.respond(IdBody(id))
        }

        get("/records") {
            val userId = call.parameters["user_id"]?.toInt()
            val categoryId = call.parameters["category_id"]?.toInt()
            if (userId == null && categoryId == null) {
                call.respond(HttpStatusCode.BadRequest, "At least user id or category id needs to be passed")
            } else {
                call.respond(expensesDao.getQuery(userId, categoryId))
            }
        }
    }

}