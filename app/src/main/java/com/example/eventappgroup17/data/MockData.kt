package com.example.eventappgroup17.data

import com.example.eventappgroup17.model.Category
import com.example.eventappgroup17.model.Event
import com.example.eventappgroup17.model.Registration
import com.example.eventappgroup17.model.User

// Sample categories to test the UI
val sampleCategories = listOf(
    Category(1, "Workshop"),
    Category(2, "Talk"),
    Category(3, "Sports"),
    Category(4, "Study Session"),
    Category(5, "Cultural"),
    Category(6, "Meetup")
)

// Sample events to test the UI
val sampleEvents = listOf(
    Event(
        id = 1,
        title = "Kotlin for Android Beginners",
        description = "A hands-on workshop covering the basics of Kotlin and Jetpack Compose. Perfect for students new to Android development.",
        date = "2026-05-20",
        time = "14:00",
        location = "Room A101, NOVA IMS",
        category = sampleCategories[0],
        organizer = "Prof. João Silva",
        participantCount = 18,
        maxParticipants = 30
    ),
    Event(
        id = 2,
        title = "Data Science Career Talk",
        description = "Industry professionals share their journey and tips for breaking into data science. Q&A session included.",
        date = "2026-05-22",
        time = "18:00",
        location = "Auditorium, NOVA IMS",
        category = sampleCategories[1],
        organizer = "Career Services",
        participantCount = 45,
        maxParticipants = 100
    ),
    Event(
        id = 3,
        title = "5-a-side Football Tournament",
        description = "Inter-faculty football tournament. Form your team and sign up. Prizes for the top 3 teams!",
        date = "2026-05-25",
        time = "10:00",
        location = "University Sports Ground",
        category = sampleCategories[2],
        organizer = "Sports Committee",
        participantCount = 32,
        maxParticipants = 50
    ),
    Event(
        id = 4,
        title = "Machine Learning Study Group",
        description = "Weekly study group to work through the ML course material together. Bring your laptop.",
        date = "2026-05-19",
        time = "16:00",
        location = "Library Room 3, NOVA IMS",
        category = sampleCategories[3],
        organizer = "Student Association",
        participantCount = 12,
        maxParticipants = 20
    ),
    Event(
        id = 5,
        title = "Fado Night",
        description = "An evening celebrating Portuguese Fado music with live performances and traditional food.",
        date = "2026-05-30",
        time = "20:00",
        location = "Campus Cafeteria",
        category = sampleCategories[4],
        organizer = "Cultural Club",
        participantCount = 60,
        maxParticipants = 80
    ),
    Event(
        id = 6,
        title = "Tech Startup Meetup",
        description = "Network with fellow students interested in entrepreneurship and startups. Lightning pitch session.",
        date = "2026-06-03",
        time = "19:00",
        location = "Innovation Hub, Lisbon",
        category = sampleCategories[5],
        organizer = "Entrepreneurship Club",
        participantCount = 27,
        maxParticipants = null
    )
)


// Sample users for testing the UI
val sampleUser = User(id = 1, name = "Maria Costa", email = "maria.costa@novaims.pt")
val sampleAdmin = User(id = 2, name = "Admin", email = "admin@novaims.pt", isAdmin = true)

// Sample registrations to events for testing the UI
val sampleRegistrations = mutableListOf(
    Registration(id = 1, userId = 1, eventId = 1),
    Registration(id = 2, userId = 1, eventId = 4)
)
