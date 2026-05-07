package com.example.eventappgroup17.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eventappgroup17.data.MockAuthRepository
import com.example.eventappgroup17.data.sampleCategories
import com.example.eventappgroup17.data.sampleEvents
import com.example.eventappgroup17.data.sampleRegistrations
import com.example.eventappgroup17.model.Category
import com.example.eventappgroup17.model.Registration
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
    var currentUser by remember { mutableStateOf<User?>(null) }

    val events = remember { sampleEvents.toMutableStateList() }
    val registrations = remember { sampleRegistrations.toMutableStateList() }
    val categories = remember { sampleCategories.toMutableStateList() }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLogin = { email, password ->
                    val user = MockAuthRepository.login(email, password)
                    if (user != null) {
                        currentUser = user
                        val dest = if (user.isAdmin) Routes.ADMIN_DASHBOARD else Routes.EVENT_LIST
                        navController.navigate(dest) { popUpTo(Routes.LOGIN) { inclusive = true } }
                        null
                    } else {
                        "Invalid email or password"
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegister = { name, email, password ->
                    val user = MockAuthRepository.register(name, email, password)
                    if (user != null) {
                        currentUser = user
                        navController.navigate(Routes.EVENT_LIST) { popUpTo(Routes.LOGIN) { inclusive = true } }
                        null
                    } else {
                        "An account with this email already exists"
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
                    currentUser = null
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
            val isRegistered = registrations.any { it.userId == (currentUser?.id ?: -1) && it.eventId == eventId }
            val isFull = event.maxParticipants != null && event.participantCount >= event.maxParticipants
            EventDetailScreen(
                event = event,
                isLoggedIn = currentUser != null,
                isRegistered = isRegistered,
                isFull = isFull,
                onRegister = {
                    if (currentUser != null && !isRegistered && !isFull) {
                        registrations.add(
                            Registration(
                                id = (registrations.maxOfOrNull { it.id } ?: 0) + 1,
                                userId = currentUser!!.id,
                                eventId = eventId
                            )
                        )
                        val idx = events.indexOfFirst { it.id == eventId }
                        if (idx >= 0) events[idx] = events[idx].copy(participantCount = events[idx].participantCount + 1)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.USER_AREA) {
            val user = currentUser ?: return@composable
            val registeredEvents = events.filter { event ->
                registrations.any { it.userId == user.id && it.eventId == event.id }
            }
            UserAreaScreen(
                user = user,
                registeredEvents = registeredEvents,
                onCancelRegistration = { eventId ->
                    registrations.removeIf { it.userId == user.id && it.eventId == eventId }
                    val idx = events.indexOfFirst { it.id == eventId }
                    if (idx >= 0 && events[idx].participantCount > 0) {
                        events[idx] = events[idx].copy(participantCount = events[idx].participantCount - 1)
                    }
                },
                onEventClick = { navController.navigate(Routes.eventDetail(it.id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                events = events,
                onCreateEvent = { navController.navigate(Routes.adminEventForm()) },
                onEditEvent = { navController.navigate(Routes.adminEventForm(it.id)) },
                onViewParticipants = { navController.navigate(Routes.adminParticipants(it.id)) },
                onDeleteEvent = { event -> events.remove(event) },
                onManageCategories = { navController.navigate(Routes.ADMIN_CATEGORIES) },
                onLogout = {
                    currentUser = null
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
                    if (event != null) {
                        val idx = events.indexOfFirst { it.id == savedEvent.id }
                        if (idx >= 0) events[idx] = savedEvent
                    } else {
                        val newId = (events.maxOfOrNull { it.id } ?: 0) + 1
                        events.add(savedEvent.copy(id = newId))
                    }
                    navController.popBackStack()
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
            val participantIds = registrations.filter { it.eventId == eventId }.map { it.userId }
            AdminParticipantsScreen(
                event = event,
                participants = MockAuthRepository.getUsersByIds(participantIds),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_CATEGORIES) {
            AdminCategoriesScreen(
                categories = categories,
                onAddCategory = { name ->
                    val newId = (categories.maxOfOrNull { it.id } ?: 0) + 1
                    categories.add(Category(newId, name))
                },
                onDeleteCategory = { category -> categories.remove(category) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
