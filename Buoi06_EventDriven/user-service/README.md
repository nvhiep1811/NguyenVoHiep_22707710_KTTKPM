# User Service

## Features
- User registration with password hashing (BCrypt)
- MongoDB integration for user storage
- Kafka integration to publish `USER_REGISTERED` event
- Input validation

## API Endpoints
### Register a new user
- **URL:** `/api/users/register`
- **Method:** `POST`
- **Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepassword",
  "role": "USER"
}
```

## Configuration
The service uses environment variables from the root `.env` file.
- `USER_SERVER_PORT`: Port for the service (default: 8081)
- `USER_MONGODB_URI`: MongoDB connection string
- `TOPIC_USER_REGISTERED`: Kafka topic name
