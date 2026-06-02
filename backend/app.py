import os
from datetime import timedelta
from dotenv import load_dotenv
from flask import Flask

load_dotenv()

from flask_cors import CORS
from flask_jwt_extended import JWTManager
from models import db, ALL_TABLES
from routes.auth import auth_bp
from routes.events import events_bp
from routes.categories import categories_bp
from routes.registrations import registrations_bp

DB_HOST = os.environ.get('DB_HOST', 'localhost')
DB_PORT = int(os.environ.get('DB_PORT', 3306))
DB_USER = os.environ.get('DB_USER', 'root')
DB_PASSWORD = os.environ.get('DB_PASSWORD', '')
DB_NAME = os.environ.get('DB_NAME', 'eventapp')
JWT_SECRET = os.environ.get('JWT_SECRET_KEY')

if not JWT_SECRET:
    raise RuntimeError('JWT_SECRET_KEY is not set. Copy .env.example to .env and fill it in.')


def create_app():
    app = Flask(__name__)
    app.config['JWT_SECRET_KEY'] = JWT_SECRET
    app.config['JWT_ACCESS_TOKEN_EXPIRES'] = timedelta(days=7)

    CORS(app)
    JWTManager(app)

    db.init(DB_NAME, host=DB_HOST, port=DB_PORT, user=DB_USER, password=DB_PASSWORD)
    with db:
        db.create_tables(ALL_TABLES, safe=True)

    app.register_blueprint(auth_bp)
    app.register_blueprint(events_bp)
    app.register_blueprint(categories_bp)
    app.register_blueprint(registrations_bp)

    @app.teardown_appcontext
    def close_db(exc):
        if not db.is_closed():
            db.close()

    @app.before_request
    def open_db():
        if db.is_closed():
            db.connect()

    return app


if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', debug=True, port=5000)
