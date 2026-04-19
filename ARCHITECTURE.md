# OEMS Architecture

Online Examination Management System (OEMS) is built as a layered Spring Boot REST backend with a React SPA frontend. The backend owns authentication, business rules, persistence, and response shaping. The frontend owns the user interface, client state, routing, and API consumption.

## 1. Architectural Style

The project uses a pragmatic MVC-style architecture:

- Model: domain objects in `backend/src/main/java/com/oems/model`
- Controller: HTTP endpoints in `backend/src/main/java/com/oems/controller`
- Service: application and business logic in `backend/src/main/java/com/oems/service`
- Repository: MongoDB persistence interfaces in `backend/src/main/java/com/oems/repository`
- View/API contract: request and response DTOs in `backend/src/main/java/com/oems/dto`
- Presentation layer: React pages and reusable components in `frontend/src/pages` and `frontend/src/components`

The backend is not an MVC web page renderer. It is a REST API that returns JSON to the frontend SPA, so the "view" is implemented in the React client and in the backend DTO contract.

## 2. Backend Layering

### Controllers

Controllers are thin request handlers. They validate input, resolve the current user when needed, and delegate to services.

- `AuthController` handles login and profile lookup.
- `AdminController` handles user, course, and result administration.
- `TeacherController` handles exam authoring, question management, and teacher result views.
- `StudentController` handles exam listing, attempt lifecycle, answer saving, submission, and result history.
- `ExamController` is currently reserved for future cross-role exam endpoints.

### Services

Services contain the actual application rules.

- `AuthService` authenticates a user and generates a JWT.
- `UserService` creates, updates, lists, and deactivates users.
- `CourseService` manages courses, enrollment, teacher assignment, and student membership.
- `ExamService` manages exam creation, editing, publication, question ordering, and exam visibility.
- `AttemptService` manages starting an attempt, saving answers, submitting an attempt, and calculating score.
- `ResultService` exposes teacher/admin result views and publishing flows.
- `CurrentUserService` resolves the authenticated user from the security context.
- `MapperService` converts domain objects into response DTOs.

### Repositories

Repositories are Spring Data MongoDB interfaces and should remain persistence-focused.

- `UserRepository`
- `CourseRepository`
- `ExamRepository`
- `QuestionRepository`
- `ExamAttemptRepository`
- `StudentAnswerRepository`

### Domain Models

Domain objects are MongoDB documents under `backend/src/main/java/com/oems/model`.

- `User`
- `Course`
- `Exam`
- `Question`
- `ExamAttempt`
- `StudentAnswer`
- `Role`
- `OptionChoice`
- `AttemptStatus`

### DTO Layer

DTOs are used as the API boundary so the frontend never depends on persistence classes directly. This keeps the REST contract stable and avoids exposing internal entity structure.

## 3. Core Request Flow

Typical backend flow:

1. React page or component calls the API through `frontend/src/api/*`.
2. Spring Security authenticates the request with JWT, if required.
3. Controller validates the incoming request body or path variables.
4. Service applies business rules and authorization checks.
5. Repository loads or saves MongoDB documents.
6. Service maps the result into a DTO.
7. Controller returns JSON to the frontend.

## 4. Security Architecture

Security is intentionally separated from business logic.

- `JwtTokenProvider` creates and validates JWT tokens.
- `JwtAuthFilter` reads the `Authorization` header and sets authentication in the security context.
- `SecurityConfig` configures stateless sessions, CORS, authorization rules, and password encoding.
- `CurrentUserService` resolves the currently authenticated user when controller or service logic needs identity-specific behavior.

Authentication is stateless. The frontend stores the token in local storage and the Axios client injects it on each request.

## 5. Frontend Architecture

The frontend is a single-page application with role-based navigation.

- `frontend/src/pages` contains the main route-level screens for login, admin, teacher, and student workflows.
- `frontend/src/components` contains reusable UI pieces such as loading, navigation, protected routing, cards, and timers.
- `frontend/src/context/AuthContext.jsx` stores authentication state and exposes `login`, `logout`, and `isAuthenticated`.
- `frontend/src/api` holds the API client and role-specific request modules.
- `frontend/src/routes/AppRoutes.jsx` defines the public and protected routes.

Client-side authorization is enforced by `ProtectedRoute`, which blocks unauthenticated users and role mismatches before the page renders.

## 6. Data Model

The main domain relationships are:

- A `Course` has one teacher and many students.
- An `Exam` belongs to one `Course` and one teacher.
- A `Question` belongs to one `Exam`.
- An `ExamAttempt` belongs to one student and one exam.
- A `StudentAnswer` belongs to one attempt and one question.

Enum usage:

- `Role` defines `STUDENT`, `TEACHER`, and `ADMIN`.
- `OptionChoice` defines multiple-choice answers `A` through `D`.
- `AttemptStatus` defines the attempt lifecycle: `IN_PROGRESS`, `SUBMITTED`, and `TIMED_OUT`.

## 7. Data Seeding

`DataSeeder` populates demo users, courses, exams, and questions on first run. It also supports a reset mode controlled by `app.seed.reset` or `SEED_RESET=true`.

This is useful for demos because the project can start with a working set of roles and exam data without manual setup.

## 8. Design Decisions Visible In Code

The implementation shows a few important OO design choices:

- Controllers are thin and delegate to services.
- Services are split by domain responsibility rather than one large application service.
- Repositories isolate MongoDB access from business logic.
- DTOs separate API contracts from persistence models.
- Security concerns are kept in filter/config classes rather than mixed into controllers.
- Frontend auth and routing logic are centralized instead of duplicated across pages.

## 9. Summary

The architecture is a layered MVC-style system with a clear separation between controller, service, repository, domain, and frontend presentation responsibilities. The design supports the OOAD rubric by keeping the backend modular, the frontend role-aware, and the security flow stateless and centralized.
