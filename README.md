# Projektor — Event & Meetup Platform

A mobile app for discovering and managing events, built for the NOVA IMS Mobile Apps Development course (Group 17).

## Tech Stack

- **Android:** Kotlin + Jetpack Compose
- **Navigation:** Navigation Compose
- **Backend (to be implemented):** Python / Flask REST API + database

## Features

- **User flow:** Browse events, view event details, register/cancel registration, user profile area
- **Admin flow:** Dashboard with full CRUD for events, category management, participant overview
- **Auth:** Login and registration screens (currently mock-based)

## Screens

| Screen | Description |
|---|---|
| Login / Register | User authentication |
| Event List | Searchable and filterable event cards |
| Event Detail | Full event info + registration button |
| User Area | View and cancel registered events |
| Admin Dashboard | Manage all events |
| Admin Event Form | Create / edit events |
| Admin Participants | View event participants |
| Admin Categories | Manage event categories |

## Project Structure

```
app/src/main/java/com/example/eventappgroup17/
├── model/          # Data models (User, Event, Category, Registration)
├── data/           # Mock data and mock auth repository
├── screens/        # All Compose screens
│   └── admin/      # Admin-only screens
├── navigation/     # AppNavigation (routing logic)
└── ui/theme/       # Colors, typography, theme
```

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Run on an emulator or physical device (minSdk 24)

## To Be Implemented

- [ ] Python / Flask REST API
- [ ] Database integration (most likely SQLite)
- [ ] Replace mock data with real API calls via Retrofit
- [ ] Proper authentication (JWT or session-based)
