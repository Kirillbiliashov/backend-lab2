package com.example.plugins

import com.example.model.Category
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
        ModelStorage.deleteUser(id)
        call.respond(HttpStatusCode.OK, "category $id successfully deleted")
    }
    post("/category") {
        val category = call.receive<Category>()
        ModelStorage.addCategory(category)
        call.respond(HttpStatusCode.OK, "category successfully added")
    }

}
