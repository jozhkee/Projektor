# EventAppGroup17 — Event & Meetup Platform

A mobile app for discovering and managing events, built for the NOVA IMS Mobile Apps Development course (Group 17).

## Tech Stack

- **Android:** Kotlin + Jetpack Compose
- **Navigation:** Navigation Compose
- **Networking:** Retrofit 2 + OkHttp + Gson
- **Backend:** Python / Flask + Peewee ORM + MySQL

## Project Structure

```
Projektor/
├── EventAppGroup17/          # Android app (Kotlin + Jetpack Compose)
│   └── app/src/main/java/com/example/eventappgroup17/
│       ├── data/network/     # Retrofit client, API service, DTOs
│       ├── model/            # Domain models (User, Event, Category, Registration)
│       ├── navigation/       # AppNavigation (all routes + API calls)
│       ├── screens/          # Compose screens
│       │   └── admin/        # Admin-only screens
│       └── ui/theme/
└── backend/                  # Flask REST API
    ├── app.py                # Flask app factory
    ├── models.py             # Peewee models
    ├── seed.py               # Populate DB with sample data
    ├── requirements.txt
    └── routes/
        ├── auth.py           # POST /auth/login  POST /auth/register
        ├── events.py         # CRUD /events  GET /events/{id}/participants
        ├── categories.py     # CRUD /categories
        └── registrations.py  # /registrations  /users/{id}/registrations
```

## Screens

| Screen | Description |
|---|---|
| Login / Register | JWT-authenticated via Flask API |
| Event List | Searchable event cards loaded from API |
| Event Detail | Full event info + register/unregister via API |
| User Area | View and cancel registered events |
| Admin Dashboard | Manage all events (create, edit, delete) |
| Admin Event Form | Create / edit events via API |
| Admin Participants | View participants fetched from API |
| Admin Categories | Manage event categories via API |

## API Endpoints

| Method | Path | Auth |
|---|---|---|
| POST | `/auth/login` | — |
| POST | `/auth/register` | — |
| GET | `/events/` | — |
| GET | `/events/{id}` | — |
| POST | `/events/` | Admin |
| PUT | `/events/{id}` | Admin |
| DELETE | `/events/{id}` | Admin |
| GET | `/events/{id}/participants` | Admin |
| GET | `/categories/` | — |
| POST | `/categories/` | Admin |
| DELETE | `/categories/{id}` | Admin |
| GET | `/users/{id}/registrations` | User |
| POST | `/registrations` | User |
| DELETE | `/registrations/{id}` | User |

## Getting Started

### Backend

```bash
cd backend
pip install -r requirements.txt
python seed.py        # populate DB with sample data (run once)
python app.py         # starts on http://localhost:5000
```

Sample accounts created by `seed.py`:
- **User:** `maria.costa@novaims.pt` / `password123`
- **Admin:** `admin@novaims.pt` / `admin123`

### Android App

1. Open `EventAppGroup17/` in Android Studio
2. The emulator connects to the backend at `https://projektor.fog.pt/` by default
3. For a physical device, change `BASE_URL` in `data/network/ApiClient.kt` to your machine's LAN IP
4. Run on an emulator or physical device (minSdk 24)
