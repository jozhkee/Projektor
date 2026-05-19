package com.example.eventappgroup17.data.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // Events
    @GET("events/")
    suspend fun getEvents(
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Int? = null,
    ): Response<List<EventDto>>

    @GET("events/{id}")
    suspend fun getEvent(@Path("id") id: Int): Response<EventDto>

    @POST("events/")
    suspend fun createEvent(
        @Header("Authorization") token: String,
        @Body request: CreateEventRequest,
    ): Response<EventDto>

    @PUT("events/{id}")
    suspend fun updateEvent(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateEventRequest,
    ): Response<EventDto>

    @DELETE("events/{id}")
    suspend fun deleteEvent(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Response<MessageResponse>

    @GET("events/{id}/participants")
    suspend fun getParticipants(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Response<List<ParticipantDto>>

    // Categories
    @GET("categories/")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @POST("categories/")
    suspend fun createCategory(
        @Header("Authorization") token: String,
        @Body request: CreateCategoryRequest,
    ): Response<CategoryDto>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Response<MessageResponse>

    // Registrations
    @GET("users/{userId}/registrations")
    suspend fun getUserRegistrations(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
    ): Response<List<RegistrationDto>>

    @POST("registrations")
    suspend fun createRegistration(
        @Header("Authorization") token: String,
        @Body request: CreateRegistrationRequest,
    ): Response<RegistrationCreatedDto>

    @DELETE("registrations/{id}")
    suspend fun deleteRegistration(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Response<MessageResponse>
}