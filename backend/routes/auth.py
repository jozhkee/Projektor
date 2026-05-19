from flask import Blueprint, request, jsonify
from werkzeug.security import generate_password_hash, check_password_hash
from flask_jwt_extended import create_access_token
from peewee import IntegrityError
from models import User

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