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

_BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = os.environ.get('DB_PATH', os.path.join(_BASE_DIR, 'eventapp.db'))

JWT_SECRET = os.environ.get('JWT_SECRET_KEY')
if not JWT_SECRET:
    raise RuntimeError('JWT_SECRET_KEY is not set. Copy .env.example to .env and fill it in.')


def create_app():
    app = Flask(__name__)
    app.config['JWT_SECRET_KEY'] = JWT_SECRET
    app.config['JWT_ACCESS_TOKEN_EXPIRES'] = timedelta(days=7)

    CORS(app)
    JWTManager(app)

    db.init(DB_PATH)
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
    app.run(debug=True, port=5000)
