package com.example.eventappgroup17.model

data class Category(
    val id: Int,
    val name: String
)

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val category: Category,
    val organizer: String,
    val participantCount: Int,
    val maxParticipants: Int? = null,
    val imageUrl: String? = null
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val isAdmin: Boolean = false
)

data class Registration(
    val id: Int,
    val userId: Int,
    val eventId: Int
)
