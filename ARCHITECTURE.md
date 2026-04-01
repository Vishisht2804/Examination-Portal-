# OEMS MVC Architecture

This project follows MVC with a REST backend and SPA frontend.

## Backend MVC Mapping

- Model:
  - `backend/src/main/java/com/oems/model`
  - JPA entities and enums represent domain state.

- Controller:
  - `backend/src/main/java/com/oems/controller`
  - HTTP endpoints only. Controllers validate input and delegate to services.

- Service (application/business layer):
  - `backend/src/main/java/com/oems/service`
  - Business rules, orchestration, authorization checks, exam timer logic, result evaluation.

- Repository (data access):
  - `backend/src/main/java/com/oems/repository`
  - Persistence operations only.

- View models for API:
  - `backend/src/main/java/com/oems/dto`
  - Request/response DTOs used as API contract so entities are not exposed.

## Request Flow

1. Controller receives request and validates DTO.
2. Service applies business rules.
3. Repository loads/saves models.
4. Service maps model to DTO response.
5. Controller returns JSON response.

## Security Placement

- `backend/src/main/java/com/oems/security`
- `backend/src/main/java/com/oems/config`

Security is cross-cutting and sits outside MVC layers while protecting controller routes.

## Frontend

The frontend is a SPA that consumes backend MVC endpoints:

- UI pages/components: `frontend/src/pages`, `frontend/src/components`
- State/context: `frontend/src/context`
- API client layer: `frontend/src/api`

This keeps presentation concerns in frontend and domain/business concerns in backend services.
