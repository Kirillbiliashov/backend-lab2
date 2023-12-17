package com.example.plugins

import com.example.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val currencyStorage = ModelStorage<Currency>(
    listOf(
        Currency(name = "USD", country = "USA"),
        Currency(name = "EUR", country = "EU")
    )
)
private val userStorage = ModelStorage<User>()
private val expenseStorage = ModelStorage<Expense>()
private val categoryStorage = ModelStorage<Category>()

fun Application.configureRouting() {
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
            if (currency.name.length <= 3) reasons.add("Currency name should be valid")
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
    get("/user/{id}") {
        val id = call.parameters["id"]!!.toInt()
        call.respond(userStorage.get(id))
    }

    delete("/user/{id}") {
        val id = call.parameters["id"]!!.toInt()
        userStorage.delete(id)
        call.respond(HttpStatusCode.OK, "user $id successfully deleted")
    }
    post("/user") {
        val user = call.receive<User>()
        val id = userStorage.add(user)
        call.respond(IdBody(id))
    }
    get("/users") {
        call.respond(userStorage.values())
    }
}

fun Routing.categoryRouting() {
    get("/category/{id}") {
        val id = call.parameters["id"]!!.toInt()
        call.respond(categoryStorage.get(id))
    }
    delete("/category/{id}") {
        val id = call.parameters["id"]!!.toInt()
        categoryStorage.delete(id)
        call.respond(HttpStatusCode.OK, "category $id successfully deleted")
    }
    post("/category") {
        val category = call.receive<Category>()
        val id = categoryStorage.add(category)
        call.respond(IdBody(id))
    }

}

fun Routing.currencyRouting() {
    get("/currency/{id}") {
        val id = call.parameters["id"]!!.toInt()
        call.respond(currencyStorage.get(id))
    }
    delete("/currency/{id}") {
        val id = call.parameters["id"]!!.toInt()
        if (id <= 2) call.respond(HttpStatusCode.BadRequest, "Can't delete default currency")
        else {
            currencyStorage.delete(id)
            call.respond(HttpStatusCode.OK, "category $id successfully deleted")
        }
    }
    post("/currency") {
        val currency = call.receive<Currency>()
        val id = currencyStorage.add(currency)
        call.respond(IdBody(id))
    }

}

fun Routing.recordRouting() {
    get("/record/{id}") {
        val id = call.parameters["id"]!!.toInt()
        call.respond(expenseStorage.get(id))
    }

    delete("/record/{id}") {
        val id = call.parameters["id"]!!.toInt()
        expenseStorage.delete(id)
        call.respond(HttpStatusCode.OK, "expense $id successfully deleted")
    }

    post("/record") {
        val record = call.receive<Expense>()
        val id = expenseStorage.add(record)
        call.respond(IdBody(id))
    }

    get("/records") {
        val userId = call.parameters["user_id"]?.toInt()
        val categoryId = call.parameters["category_id"]?.toInt()
        if (userId == null && categoryId == null) {
            call.respond(HttpStatusCode.BadRequest, "At least user id or category id needs to be passed")
        } else {
            val filteredExpenses = expenseStorage.values()
                .filter { it.userId == userId || userId == null }
                .filter { it.categoryId == categoryId || categoryId == null }
            call.respond(filteredExpenses)
        }

    }

}