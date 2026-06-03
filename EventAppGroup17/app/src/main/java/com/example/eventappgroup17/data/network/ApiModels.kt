package com.example.eventappgroup17.data.network

import com.example.eventappgroup17.model.Category
import com.example.eventappgroup17.model.Event
import com.example.eventappgroup17.model.User
import com.google.gson.annotations.SerializedName

// ── Requests ──────────────────────────────────────────────────────────────────

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(val name: String, val email: String, val password: String)

data class CreateEventRequest(
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    @SerializedName("category_id") val categoryId: Int,
    val organizer: String,
    @SerializedName("max_participants") val maxParticipants: Int?,
    @SerializedName("image_url") val imageUrl: String? = null,
)

data class UpdateEventRequest(
    val title: String? = null,
    val description: String? = null,
    val date: String? = null,
    val time: String? = null,
    val location: String? = null,
    @SerializedName("category_id") val categoryId: Int? = null,
    val organizer: String? = null,
    @SerializedName("max_participants") val maxParticipants: Int? = null,
)

data class CreateRegistrationRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("event_id") val eventId: Int,
)

data class CreateCategoryRequest(val name: String)

data class UpdateProfileRequest(
    val name: String? = null,
    val password: String? = null,
    @SerializedName("current_password") val currentPassword: String? = null,
)

// ── Responses ─────────────────────────────────────────────────────────────────

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("is_admin") val isAdmin: Boolean,
) {
    fun toUser() = User(id = id, name = name, email = email, isAdmin = isAdmin)
}

data class AuthResponse(val token: String, val user: UserDto)

data class CategoryDto(val id: Int, val name: String) {
    fun toCategory() = Category(id = id, name = name)
}

data class EventDto(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val category: CategoryDto,
    val organizer: String,
    @SerializedName("participant_count") val participantCount: Int,
    @SerializedName("max_participants") val maxParticipants: Int?,
    @SerializedName("image_url") val imageUrl: String?,
) {
    fun toEvent() = Event(
        id = id, title = title, description = description,
        date = date, time = time, location = location,
        category = category.toCategory(), organizer = organizer,
        participantCount = participantCount, maxParticipants = maxParticipants,
        imageUrl = imageUrl,
    )
}

data class RegistrationEventSummary(val id: Int, val title: String, val date: String, val time: String, val location: String)
data class RegistrationDto(val id: Int, val event: RegistrationEventSummary)

data class RegistrationCreatedDto(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("event_id") val eventId: Int,
)

data class ParticipantDto(val id: Int, val name: String, val email: String) {
    fun toUser() = User(id = id, name = name, email = email)
}

data class MessageResponse(val message: String)