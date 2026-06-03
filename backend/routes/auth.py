from flask import Blueprint, request, jsonify
from werkzeug.security import generate_password_hash, check_password_hash
from flask_jwt_extended import create_access_token
from peewee import IntegrityError
from models import User
from flask_jwt_extended import create_access_token, jwt_required, get_jwt, get_jwt_identity

auth_bp = Blueprint('auth', __name__, url_prefix='/auth')


def user_to_dict(user):
    return {
        'id': user.id,
        'name': user.name,
        'email': user.email,
        'is_admin': user.is_admin,
    }


@auth_bp.post('/register')
def register():
    data = request.get_json()
    name = data.get('name', '').strip()
    email = data.get('email', '').strip().lower()
    password = data.get('password', '')

    if not name or not email or not password:
        return jsonify({'error': 'name, email and password are required'}), 400

    try:
        user = User.create(
            name=name,
            email=email,
            password_hash=generate_password_hash(password),
        )
    except IntegrityError:
        return jsonify({'error': 'email already registered'}), 409

    token = create_access_token(identity=str(user.id), additional_claims={'is_admin': user.is_admin})
    return jsonify({'token': token, 'user': user_to_dict(user)}), 201


@auth_bp.post('/login')
def login():
    data = request.get_json()
    email = data.get('email', '').strip().lower()
    password = data.get('password', '')

    user = User.get_or_none(User.email == email)
    if user is None or not check_password_hash(user.password_hash, password):
        return jsonify({'error': 'invalid credentials'}), 401

    token = create_access_token(identity=str(user.id), additional_claims={'is_admin': user.is_admin})
    return jsonify({'token': token, 'user': user_to_dict(user)}), 200


@auth_bp.put('/users/<int:user_id>')
@jwt_required()
def update_profile(user_id):
    claims = get_jwt()
    if not claims.get('is_admin') and int(get_jwt_identity()) != user_id:
        return jsonify({'error': 'forbidden'}), 403

    user = User.get_or_none(User.id == user_id)
    if user is None:
        return jsonify({'error': 'user not found'}), 404

    data = request.get_json()

    if 'name' in data:
        name = data['name'].strip()
        if not name:
            return jsonify({'error': 'name cannot be empty'}), 400
        user.name = name

    if 'password' in data:
        current_password = data.get('current_password', '')
        if not check_password_hash(user.password_hash, current_password):
            return jsonify({'error': 'current password is incorrect'}), 400
        password = data['password']
        if len(password) < 6:
            return jsonify({'error': 'password must be at least 6 characters'}), 400
        user.password_hash = generate_password_hash(password)

    user.save()
    return jsonify(user_to_dict(user)), 200