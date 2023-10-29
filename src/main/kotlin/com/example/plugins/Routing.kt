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
        call.respond(ModelStorage.users.first { it.id == id })
    }
    delete("/user/{id}") {
        val id = call.parameters["id"]!!.toInt()
        ModelStorage.deleteUser(id)
        call.respond(HttpStatusCode.OK, "user $id successfully deleted")
    }
    post("/user") {
        val user = call.receive<User>()
        ModelStorage.addUser(user)
        call.respond(HttpStatusCode.OK, "user successfully added")
    }
    get("/users") {
        call.respond(ModelStorage.users)
    }
}

fun Routing.categoryRouting() {
    get("/category/{id}") {
        val id = call.parameters["id"]!!.toInt()
        call.respond(ModelStorage.categories.first { it.id == id })
    }
    delete("/category/{id}") {
        val id = call.parameters["id"]!!.toInt()
        ModelStorage.deleteCategory(id)
        call.respond(HttpStatusCode.OK, "category $id successfully deleted")
    }
    post("/category") {
        val category = call.receive<Category>()
        ModelStorage.addCategory(category)
        call.respond(HttpStatusCode.OK, "category successfully added")
    }

}

fun Routing.recordRouting() {
    get("/record/{id}") {
        val id = call.parameters["id"]!!.toInt()
        call.respond(ModelStorage.expenses.first { it.id == id })
    }

    delete("/record/{id}") {
        val id = call.parameters["id"]!!.toInt()
        ModelStorage.deleteRecord(id)
        call.respond(HttpStatusCode.OK, "expense $id successfully deleted")
    }

    post("/record") {
        val record = call.receive<Expense>()
        ModelStorage.addRecord(record)
        call.respond(HttpStatusCode.OK, "expense successfully added")
    }

    get("/records") {
        val userId = call.parameters["user_id"]?.toInt()
        val categoryId = call.parameters["category_id"]?.toInt()
        if (userId == null && categoryId == null) {
            call.respond(HttpStatusCode.BadRequest, "At least user id or category id needs to be passed")
        } else {
            val filteredExpenses = ModelStorage
                .expenses
                .filter { it.userId == userId || userId == null }
                .filter { it.categoryId == categoryId || categoryId == null }
            call.respond(filteredExpenses)
        }

    }

}