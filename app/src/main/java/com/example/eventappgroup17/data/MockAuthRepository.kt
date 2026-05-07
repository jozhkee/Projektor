package com.example.eventappgroup17.data

import com.example.eventappgroup17.model.User

object MockAuthRepository {
    private val accounts = mutableListOf(
        sampleUser to "password123",
        sampleAdmin to "admin123"
    )

    fun login(email: String, password: String): User? =
        accounts.find { it.first.email.equals(email, ignoreCase = true) && it.second == password }?.first

    fun register(name: String, email: String, password: String): User? {
        if (accounts.any { it.first.email.equals(email, ignoreCase = true) }) return null
        val newUser = User(id = accounts.size + 1, name = name, email = email)
        accounts.add(newUser to password)
        return newUser
    }

    fun getUsersByIds(ids: List<Int>): List<User> =
        accounts.filter { it.first.id in ids }.map { it.first }
}
