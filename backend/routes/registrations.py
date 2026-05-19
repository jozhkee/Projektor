from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from peewee import IntegrityError
from models import Registration, Event, User

registrations_bp = Blueprint('registrations', __name__)


def registration_to_dict(reg):
    e = reg.event
    return {
        'id': reg.id,
        'event': {
            'id': e.id,
            'title': e.title,
            'date': e.date,
            'time': e.time,
            'location': e.location,
        },
    }


@registrations_bp.get('/users/<int:user_id>/registrations')
@jwt_required()
def user_registrations(user_id):
    # users can only see their own; admins can see anyone's
    claims = get_jwt()
    if not claims.get('is_admin') and int(get_jwt_identity()) != user_id:
        return jsonify({'error': 'forbidden'}), 403

    regs = (
        Registration.select(Registration, Event)
        .join(Event)
        .where(Registration.user == user_id)
    )
    return jsonify([registration_to_dict(r) for r in regs]), 200


@registrations_bp.post('/registrations')
@jwt_required()
def create_registration():
    data = request.get_json()
    user_id = data.get('user_id')
    event_id = data.get('event_id')

    # users can only register themselves
    claims = get_jwt()
    if not claims.get('is_admin') and int(get_jwt_identity()) != user_id:
        return jsonify({'error': 'forbidden'}), 403

    event = Event.get_or_none(Event.id == event_id)
    if event is None:
        return jsonify({'error': 'event not found'}), 404

    if event.max_participants and event.participant_count >= event.max_participants:
        return jsonify({'error': 'event is full'}), 409

    existing = Registration.get_or_none(
        (Registration.user == user_id) & (Registration.event == event_id)
    )
    if existing:
        return jsonify({'error': 'already registered'}), 409

    reg = Registration.create(user=user_id, event=event_id)
    Event.update(participant_count=Event.participant_count + 1).where(Event.id == event_id).execute()

    return jsonify({'id': reg.id, 'user_id': user_id, 'event_id': event_id}), 201


@registrations_bp.delete('/registrations/<int:reg_id>')
@jwt_required()
def delete_registration(reg_id):
    reg = Registration.get_or_none(Registration.id == reg_id)
    if reg is None:
        return jsonify({'error': 'registration not found'}), 404

    claims = get_jwt()
    if not claims.get('is_admin') and int(get_jwt_identity()) != reg.user_id:
        return jsonify({'error': 'forbidden'}), 403

    event_id = reg.event_id
    reg.delete_instance()
    Event.update(participant_count=Event.participant_count - 1).where(Event.id == event_id).execute()

    return jsonify({'message': 'cancelled'}), 200