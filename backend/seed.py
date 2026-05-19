"""
Run once to populate the database with the same sample data used in MockData.kt.
    python seed.py
"""
from werkzeug.security import generate_password_hash
from models import db, ALL_TABLES, Category, User, Event, Registration

DB_PATH = 'eventapp.db'

CATEGORIES = [
    'Workshop', 'Talk', 'Sports', 'Study Session', 'Cultural', 'Meetup'
]

EVENTS = [
    {
        'title': 'Kotlin for Android Beginners',
        'description': 'A hands-on workshop covering the basics of Kotlin and Jetpack Compose. Perfect for students new to Android development.',
        'date': '2026-05-20', 'time': '14:00',
        'location': 'Room A101, NOVA IMS',
        'category': 'Workshop', 'organizer': 'Prof. João Silva',
        'max_participants': 30,
    },
    {
        'title': 'Data Science Career Talk',
        'description': 'Industry professionals share their journey and tips for breaking into data science. Q&A session included.',
        'date': '2026-05-22', 'time': '18:00',
        'location': 'Auditorium, NOVA IMS',
        'category': 'Talk', 'organizer': 'Career Services',
        'max_participants': 100,
    },
    {
        'title': '5-a-side Football Tournament',
        'description': 'Inter-faculty football tournament. Form your team and sign up. Prizes for the top 3 teams!',
        'date': '2026-05-25', 'time': '10:00',
        'location': 'University Sports Ground',
        'category': 'Sports', 'organizer': 'Sports Committee',
        'max_participants': 50,
    },
    {
        'title': 'Machine Learning Study Group',
        'description': 'Weekly study group to work through the ML course material together. Bring your laptop.',
        'date': '2026-05-19', 'time': '16:00',
        'location': 'Library Room 3, NOVA IMS',
        'category': 'Study Session', 'organizer': 'Student Association',
        'max_participants': 20,
    },
    {
        'title': 'Fado Night',
        'description': 'An evening celebrating Portuguese Fado music with live performances and traditional food.',
        'date': '2026-05-30', 'time': '20:00',
        'location': 'Campus Cafeteria',
        'category': 'Cultural', 'organizer': 'Cultural Club',
        'max_participants': 80,
    },
    {
        'title': 'Tech Startup Meetup',
        'description': 'Network with fellow students interested in entrepreneurship and startups. Lightning pitch session.',
        'date': '2026-06-03', 'time': '19:00',
        'location': 'Innovation Hub, Lisbon',
        'category': 'Meetup', 'organizer': 'Entrepreneurship Club',
        'max_participants': None,
    },
]

USERS = [
    {'name': 'Maria Costa', 'email': 'maria.costa@novaims.pt', 'password': 'password123', 'is_admin': False},
    {'name': 'Admin', 'email': 'admin@novaims.pt', 'password': 'admin123', 'is_admin': True},
]

# user index 0 → event indices 0 and 3 (mirrors sampleRegistrations in MockData.kt)
REGISTRATIONS = [(0, 0), (0, 3)]


def seed():
    db.init(DB_PATH)
    db.connect()
    db.create_tables(ALL_TABLES, safe=True)

    cat_map = {}
    for name in CATEGORIES:
        cat, _ = Category.get_or_create(name=name)
        cat_map[name] = cat

    user_list = []
    for u in USERS:
        user, _ = User.get_or_create(
            email=u['email'],
            defaults={
                'name': u['name'],
                'password_hash': generate_password_hash(u['password']),
                'is_admin': u['is_admin'],
            }
        )
        user_list.append(user)

    event_list = []
    for e in EVENTS:
        event, _ = Event.get_or_create(
            title=e['title'],
            defaults={
                'description': e['description'],
                'date': e['date'],
                'time': e['time'],
                'location': e['location'],
                'category': cat_map[e['category']],
                'organizer': e['organizer'],
                'participant_count': 0,
                'max_participants': e['max_participants'],
            }
        )
        event_list.append(event)

    for user_idx, event_idx in REGISTRATIONS:
        _, created = Registration.get_or_create(
            user=user_list[user_idx],
            event=event_list[event_idx],
        )
        if created:
            Event.update(participant_count=Event.participant_count + 1).where(
                Event.id == event_list[event_idx].id
            ).execute()

    db.close()
    print('Database seeded successfully.')


if __name__ == '__main__':
    seed()