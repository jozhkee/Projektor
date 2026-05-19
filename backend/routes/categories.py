from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt
from peewee import IntegrityError
from models import Category

categories_bp = Blueprint('categories', __name__, url_prefix='/categories')


def category_to_dict(cat):
    return {'id': cat.id, 'name': cat.name}


@categories_bp.get('/')
def list_categories():
    return jsonify([category_to_dict(c) for c in Category.select().order_by(Category.name)]), 200


@categories_bp.post('/')
@jwt_required()
def create_category():
    if not get_jwt().get('is_admin'):
        return jsonify({'error': 'admin only'}), 403

    name = request.get_json().get('name', '').strip()
    if not name:
        return jsonify({'error': 'name is required'}), 400

    try:
        cat = Category.create(name=name)
    except IntegrityError:
        return jsonify({'error': 'category already exists'}), 409

    return jsonify(category_to_dict(cat)), 201


@categories_bp.delete('/<int:category_id>')
@jwt_required()
def delete_category(category_id):
    if not get_jwt().get('is_admin'):
        return jsonify({'error': 'admin only'}), 403

    cat = Category.get_or_none(Category.id == category_id)
    if cat is None:
        return jsonify({'error': 'category not found'}), 404

    cat.delete_instance()
    return jsonify({'message': 'deleted'}), 200