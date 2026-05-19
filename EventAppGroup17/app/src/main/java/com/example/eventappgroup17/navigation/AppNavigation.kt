package com.example.eventappgroup17.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eventappgroup17.data.network.ApiClient
import com.example.eventappgroup17.data.network.CreateCategoryRequest
import com.example.eventappgroup17.data.network.CreateEventRequest
import com.example.eventappgroup17.data.network.CreateRegistrationRequest
import com.example.eventappgroup17.data.network.LoginRequest
import com.example.eventappgroup17.data.network.RegisterRequest
import com.example.eventappgroup17.data.network.UpdateEventRequest
import com.example.eventappgroup17.model.Category
import com.example.eventappgroup17.model.Event
import com.example.eventappgroup17.model.User
import com.example.eventappgroup17.screens.EventDetailScreen
import com.example.eventappgroup17.screens.EventListScreen
import com.example.eventappgroup17.screens.LoginScreen
import com.example.eventappgroup17.screens.RegisterScreen
import com.example.eventappgroup17.screens.UserAreaScreen
import com.example.eventappgroup17.screens.admin.AdminCategoriesScreen
import com.example.eventappgroup17.screens.admin.AdminDashboardScreen
import com.example.eventappgroup17.screens.admin.AdminEventFormScreen
import com.example.eventappgroup17.screens.admin.AdminParticipantsScreen
import kotlinx.coroutines.launch

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val EVENT_LIST = "event_list"
    const val EVENT_DETAIL = "event_detail/{eventId}"
    const val USER_AREA = "user_area"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_EVENT_FORM = "admin_event_form?eventId={eventId}"
    const val ADMIN_PARTICIPANTS = "admin_participants/{eventId}"
    const val ADMIN_CATEGORIES = "admin_categories"

    fun eventDetail(id: Int) = "event_detail/$id"
    fun adminEventForm(id: Int? = null) = if (id != null) "admin_event_form?eventId=$id" else "admin_event_form"
    fun adminParticipants(id: Int) = "admin_participants/$id"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    var currentUser by remember { mutableStateOf<User?>(null) }
    val events = remember { mutableStateListOf<Event>() }
    val categories = remember { mutableStateListOf<Category>() }
    // Pair<registrationId, eventId> — need registration ID to cancel
    val registrations = remember { mutableStateListOf<Pair<Int, Int>>() }

    suspend fun loadSharedData(userId: Int) {
        val eventsResp = ApiClient.api.getEvents()
        if (eventsResp.isSuccessful) {
            events.clear()
            events.addAll(eventsResp.body()!!.map { it.toEvent() })
        }
        val catsResp = ApiClient.api.getCategories()
        if (catsResp.isSuccessful) {
            categories.clear()
            categories.addAll(catsResp.body()!!.map { it.toCategory() })
        }
        val regsResp = ApiClient.api.getUserRegistrations(ApiClient.bearerToken, userId)
        if (regsResp.isSuccessful) {
            registrations.clear()
            registrations.addAll(regsResp.body()!!.map { it.id to it.event.id })
        }
    }

    fun clearSession() {
        currentUser = null
        ApiClient.token = null
        events.clear()
        categories.clear()
        registrations.clear()
    }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLogin = { email, password ->
                    try {
                        val response = ApiClient.api.login(LoginRequest(email, password))
                        if (response.isSuccessful) {
                            val body = response.body()!!
                            ApiClient.token = body.token
                            val user = body.user.toUser()
                            currentUser = user
                            loadSharedData(user.id)
                            val dest = if (user.isAdmin) Routes.ADMIN_DASHBOARD else Routes.EVENT_LIST
                            navController.navigate(dest) { popUpTo(Routes.LOGIN) { inclusive = true } }
                            null
                        } else {
                            "Invalid email or password"
                        }
                    } catch (e: Exception) {
                        "Network error — is the server running?"
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegister = { name, email, password ->
                    try {
                        val response = ApiClient.api.register(RegisterRequest(name, email, password))
                        if (response.isSuccessful) {
                            val body = response.body()!!
                            ApiClient.token = body.token
                            val user = body.user.toUser()
                            currentUser = user
                            loadSharedData(user.id)
                            navController.navigate(Routes.EVENT_LIST) { popUpTo(Routes.LOGIN) { inclusive = true } }
                            null
                        } else {
                            "An account with this email already exists"
                        }
                    } catch (e: Exception) {
                        "Network error — is the server running?"
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.EVENT_LIST) {
            EventListScreen(
                events = events,
                currentUser = currentUser,
                onEventClick = { navController.navigate(Routes.eventDetail(it.id)) },
                onUserAreaClick = { navController.navigate(Routes.USER_AREA) },
                onLogout = {
                    clearSession()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(
            route = Routes.EVENT_DETAIL,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: return@composable
            val event = events.find { it.id == eventId } ?: return@composable
            val isRegistered = registrations.any { it.second == eventId }
            val isFull = event.maxParticipants != null && event.participantCount >= event.maxParticipants
            EventDetailScreen(
                event = event,
                isLoggedIn = currentUser != null,
                isRegistered = isRegistered,
                isFull = isFull,
                onRegister = {
                    val user = currentUser
                    if (user != null && !isRegistered && !isFull) {
                        try {
                            val resp = ApiClient.api.createRegistration(
                                ApiClient.bearerToken,
                                CreateRegistrationRequest(userId = user.id, eventId = eventId)
                            )
                            if (resp.isSuccessful) {
                                val body = resp.body()!!
                                registrations.add(body.id to eventId)
                                val idx = events.indexOfFirst { it.id == eventId }
                                if (idx >= 0) events[idx] = events[idx].copy(participantCount = events[idx].participantCount + 1)
                            }
                        } catch (_: Exception) {}
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.USER_AREA) {
            val user = currentUser ?: return@composable
            val registeredEvents = events.filter { event -> registrations.any { it.second == event.id } }
            UserAreaScreen(
                user = user,
                registeredEvents = registeredEvents,
                onCancelRegistration = { eventId ->
                    val reg = registrations.find { it.second == eventId }
                    if (reg != null) {
                        scope.launch {
                            try {
                                val resp = ApiClient.api.deleteRegistration(ApiClient.bearerToken, reg.first)
                                if (resp.isSuccessful) {
                                    registrations.remove(reg)
                                    val idx = events.indexOfFirst { it.id == eventId }
                                    if (idx >= 0 && events[idx].participantCount > 0) {
                                        events[idx] = events[idx].copy(participantCount = events[idx].participantCount - 1)
                                    }
                                }
                            } catch (_: Exception) {}
                        }
                    }
                },
                onEventClick = { navController.navigate(Routes.eventDetail(it.id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_DASHBOARD) {
            LaunchedEffect(Unit) {
                try {
                    val resp = ApiClient.api.getEvents()
                    if (resp.isSuccessful) {
                        val fresh = resp.body()!!.map { it.toEvent() }
                        events.clear()
                        events.addAll(fresh)
                    }
                } catch (_: Exception) {}
            }
            AdminDashboardScreen(
                events = events,
                onCreateEvent = { navController.navigate(Routes.adminEventForm()) },
                onEditEvent = { navController.navigate(Routes.adminEventForm(it.id)) },
                onViewParticipants = { navController.navigate(Routes.adminParticipants(it.id)) },
                onDeleteEvent = { event ->
                    scope.launch {
                        try {
                            val resp = ApiClient.api.deleteEvent(ApiClient.bearerToken, event.id)
                            if (resp.isSuccessful) events.remove(event)
                        } catch (_: Exception) {}
                    }
                },
                onManageCategories = { navController.navigate(Routes.ADMIN_CATEGORIES) },
                onLogout = {
                    clearSession()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(
            route = Routes.ADMIN_EVENT_FORM,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId")?.takeIf { it != -1 }
            val event = eventId?.let { id -> events.find { it.id == id } }
            AdminEventFormScreen(
                existingEvent = event,
                categories = categories,
                onSave = { savedEvent ->
                    try {
                        if (event != null) {
                            val req = UpdateEventRequest(
                                title = savedEvent.title,
                                description = savedEvent.description,
                                date = savedEvent.date,
                                time = savedEvent.time,
                                location = savedEvent.location,
                                categoryId = savedEvent.category.id,
                                organizer = savedEvent.organizer,
                                maxParticipants = savedEvent.maxParticipants,
                            )
                            val resp = ApiClient.api.updateEvent(ApiClient.bearerToken, savedEvent.id, req)
                            if (resp.isSuccessful) {
                                val idx = events.indexOfFirst { it.id == savedEvent.id }
                                if (idx >= 0) events[idx] = resp.body()!!.toEvent()
                                navController.popBackStack()
                                null
                            } else if (resp.code() == 409) {
                                "Max participants cannot be less than the number of already registered participants"
                            } else {
                                "Failed to save event"
                            }
                        } else {
                            val req = CreateEventRequest(
                                title = savedEvent.title,
                                description = savedEvent.description,
                                date = savedEvent.date,
                                time = savedEvent.time,
                                location = savedEvent.location,
                                categoryId = savedEvent.category.id,
                                organizer = savedEvent.organizer,
                                maxParticipants = savedEvent.maxParticipants,
                            )
                            val resp = ApiClient.api.createEvent(ApiClient.bearerToken, req)
                            if (resp.isSuccessful) {
                                events.add(resp.body()!!.toEvent())
                                navController.popBackStack()
                                null
                            } else {
                                "Failed to create event"
                            }
                        }
                    } catch (_: Exception) {
                        "Network error — is the server running?"
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.ADMIN_PARTICIPANTS,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: return@composable
            val event = events.find { it.id == eventId } ?: return@composable
            var participants by remember { mutableStateOf<List<User>>(emptyList()) }
            LaunchedEffect(eventId) {
                try {
                    val resp = ApiClient.api.getParticipants(ApiClient.bearerToken, eventId)
                    if (resp.isSuccessful) participants = resp.body()!!.map { it.toUser() }
                } catch (_: Exception) {}
            }
            AdminParticipantsScreen(
                event = event,
                participants = participants,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_CATEGORIES) {
            AdminCategoriesScreen(
                categories = categories,
                onAddCategory = { name ->
                    scope.launch {
                        try {
                            val resp = ApiClient.api.createCategory(ApiClient.bearerToken, CreateCategoryRequest(name))
                            if (resp.isSuccessful) categories.add(resp.body()!!.toCategory())
                        } catch (_: Exception) {}
                    }
                },
                onDeleteCategory = { category ->
                    scope.launch {
                        try {
                            val resp = ApiClient.api.deleteCategory(ApiClient.bearerToken, category.id)
                            if (resp.isSuccessful) categories.remove(category)
                        } catch (_: Exception) {}
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}