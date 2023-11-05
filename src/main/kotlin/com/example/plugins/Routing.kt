package com.example.plugins

import com.example.model.Category
import com.example.model.Expense
import com.example.model.ModelStorage
import com.example.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val userStorage = ModelStorage<User>()
private val expenseStorage = ModelStorage<Expense>()
private val categoryStorage = ModelStorage<Category>()

fun Application.configureRouting() {
    routing {
        userRouting()
        categoryRouting()
        recordRouting()
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
        userStorage.add(user)
        call.respond(HttpStatusCode.OK, "user successfully added")
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
        categoryStorage.add(category)
        call.respond(HttpStatusCode.OK, "category successfully added")
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
        expenseStorage.add(record)
        call.respond(HttpStatusCode.OK, "expense successfully added")
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