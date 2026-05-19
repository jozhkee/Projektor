from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt
from models import Event, Category, Registration, User

events_bp = Blueprint('events', __name__, url_prefix='/events')


def event_to_dict(event):
    return {
        'id': event.id,
        'title': event.title,
        'description': event.description,
        'date': event.date,
        'time': event.time,
        'location': event.location,
        'category': {'id': event.category.id, 'name': event.category.name},
        'organizer': event.organizer,
        'participant_count': event.participant_count,
        'max_participants': event.max_participants,
        'image_url': event.image_url,
    }


@events_bp.get('/')
def list_events():
    query = Event.select(Event, Category).join(Category)

    search = request.args.get('search', '').strip()
    if search:
        query = query.where(
            (Event.title.contains(search)) | (Event.description.contains(search))
        )

    category_id = request.args.get('category_id')
    if category_id:
        query = query.where(Event.category == int(category_id))

    return jsonify([event_to_dict(e) for e in query.order_by(Event.date)]), 200


@events_bp.get('/<int:event_id>')
def get_event(event_id):
    event = Event.select(Event, Category).join(Category).where(Event.id == event_id).first()
    if event is None:
        return jsonify({'error': 'event not found'}), 404
    return jsonify(event_to_dict(event)), 200


@events_bp.post('/')
@jwt_required()
def create_event():
    if not get_jwt().get('is_admin'):
        return jsonify({'error': 'admin only'}), 403

    data = request.get_json()
    category = Category.get_or_none(Category.id == data.get('category_id'))
    if category is None:
        return jsonify({'error': 'category not found'}), 404

    event = Event.create(
        title=data['title'],
        description=data.get('description', ''),
        date=data['date'],
        time=data['time'],
        location=data['location'],
        category=category,
        organizer=data['organizer'],
        participant_count=0,
        max_participants=data.get('max_participants'),
        image_url=data.get('image_url'),
    )
    return jsonify(event_to_dict(event)), 201


@events_bp.put('/<int:event_id>')
@jwt_required()
def update_event(event_id):
    if not get_jwt().get('is_admin'):
        return jsonify({'error': 'admin only'}), 403

    event = Event.get_or_none(Event.id == event_id)
    if event is None:
        return jsonify({'error': 'event not found'}), 404

    data = request.get_json()
    if 'category_id' in data:
        category = Category.get_or_none(Category.id == data['category_id'])
        if category is None:
            return jsonify({'error': 'category not found'}), 404
        event.category = category

    if 'max_participants' in data:
        new_max = data['max_participants']
        if new_max is not None and new_max < event.participant_count:
            return jsonify({
                'error': f'Cannot set max participants to {new_max}: '
                         f'{event.participant_count} users are already registered'
            }), 409
        event.max_participants = new_max

    for field in ('title', 'description', 'date', 'time', 'location', 'organizer', 'image_url'):
        if field in data:
            setattr(event, field, data[field])

    event.save()
    return jsonify(event_to_dict(event)), 200


@events_bp.delete('/<int:event_id>')
@jwt_required()
def delete_event(event_id):
    if not get_jwt().get('is_admin'):
        return jsonify({'error': 'admin only'}), 403

    event = Event.get_or_none(Event.id == event_id)
    if event is None:
        return jsonify({'error': 'event not found'}), 404

    Registration.delete().where(Registration.event == event).execute()
    event.delete_instance()
    return jsonify({'message': 'deleted'}), 200


@events_bp.get('/<int:event_id>/participants')
@jwt_required()
def list_participants(event_id):
    if not get_jwt().get('is_admin'):
        return jsonify({'error': 'admin only'}), 403

    event = Event.get_or_none(Event.id == event_id)
    if event is None:
        return jsonify({'error': 'event not found'}), 404

    registrations = (
        Registration.select(Registration, User)
        .join(User)
        .where(Registration.event == event)
    )
    participants = [
        {'id': r.user.id, 'name': r.user.name, 'email': r.user.email}
        for r in registrations
    ]
    return jsonify(participants), 200