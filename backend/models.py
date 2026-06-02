from peewee import (
    MySQLDatabase, Model, AutoField, CharField,
    TextField, IntegerField, BooleanField, ForeignKeyField
)

db = MySQLDatabase(None)  # initialized in app.py with credentials


class BaseModel(Model):
    class Meta:
        database = db


class Category(BaseModel):
    id = AutoField()
    name = CharField(unique=True)


class User(BaseModel):
    id = AutoField()
    name = CharField()
    email = CharField(unique=True)
    password_hash = CharField()
    is_admin = BooleanField(default=False)


class Event(BaseModel):
    id = AutoField()
    title = CharField()
    description = TextField()
    date = CharField()          # "YYYY-MM-DD"
    time = CharField()          # "HH:MM"
    location = CharField()
    category = ForeignKeyField(Category, backref='events')
    organizer = CharField()
    participant_count = IntegerField(default=0)
    max_participants = IntegerField(null=True)
    image_url = CharField(null=True)


class Registration(BaseModel):
    id = AutoField()
    user = ForeignKeyField(User, backref='registrations')
    event = ForeignKeyField(Event, backref='registrations')


ALL_TABLES = [Category, User, Event, Registration]