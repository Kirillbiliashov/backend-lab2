package com.example.model

object ModelStorage {

    private var userInserts = 0
    private var categoriesInserts = 0
    private var expensesInserts = 0

    private var _users = mutableListOf<User>()
    private var _expenses = mutableListOf<Expense>()
    private var _categories = mutableListOf<Category>()

    val users: List<User> = _users
    val expenses: List<Expense> = _expenses
    val categories: List<Category> = _categories

    fun addUser(user: User) {
        _users.add(user.copy(id = ++userInserts))
    }

    fun deleteUser(id: Int) {
        val user = _users.find { it.id == id }!!
        _users.remove(user)
    }

    fun addCategory(category: Category) {
        _categories.add(category.copy(id = ++categoriesInserts))
    }

    fun deleteCategory(id: Int) {
        val category = _categories.find { it.id == id }!!
        _categories.remove(category)
    }



}