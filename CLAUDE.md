# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a full-stack kindergarten (Kita Casa Azul) staff scheduling system built with:
- **Backend**: Spring Boot 3.2.2 + Java 17 + MySQL 8.0
- **Frontend**: Angular 21 + TypeScript
- **Authentication**: JWT-based with Spring Security

The system manages staff schedules, age groups, and weekly planning for a kindergarten facility.

## Development Commands

### Database Setup

**Flyway Migrations (Recommended)**:
```bash
# Start backend - Flyway will handle database setup automatically
cd backend
mvn spring-boot:run
```

Flyway will automatically:
- Create/update database schema
- Apply all pending migrations
- Track migration history in `flyway_schema_history` table

See `backend/FLYWAY_SETUP.md` for complete migration guide and `backend/src/main/resources/db/migration/README.md` for detailed documentation.

**Manual Setup (Legacy)**:
```bash
cd backend
./quick-setup.sh  # Creates database, user, and loads test data
```

**Note**: The setup script creates database `kita_casa_azul` with user `kita_admin` and password from `application.properties`.

### Backend (Spring Boot)

**Start the backend** (runs on http://localhost:8080):
```bash
cd backend
mvn spring-boot:run
```

**Build**:
```bash
mvn clean package
```

**Run tests**:
```bash
mvn test
```

**Database Configuration**: `backend/src/main/resources/application.properties`
- Connection: `jdbc:mysql://localhost:3306/kita_casa_azul`
- JPA DDL: Set to `none` (schema managed via SQL scripts)
- JWT secret and expiration configured here

### Frontend (Angular)

**Start the frontend** (runs on http://localhost:4200):
```bash
cd frontend
npm install      # First time only
ng serve
```

**Build for production**:
```bash
ng build
```

**Run tests**:
```bash
ng test
```

**Development server with custom port**:
```bash
ng serve --port 4300
```

## Architecture

### Backend Architecture

**Package structure** (`com.kita.dienstplan`):
- `controller/` - REST endpoints (Staff, Schedule, Authentication)
- `service/` - Business logic layer
- `repository/` - JPA repositories for data access
- `entity/` - JPA entities (Staff, Group, WeeklySchedule, ScheduleEntry, Admin)
- `dto/` - Data transfer objects
- `security/` - JWT and Spring Security configuration

**Key Components**:
- **SecurityConfiguration**: Configures JWT auth, CORS (allows all origins in dev), session management (stateless)
- **JwtService**: Handles token generation and validation
- **JwtAuthenticationFilter**: Intercepts requests to validate JWT tokens
- **AuditingConfiguration**: Automatic `createdBy`/`updatedBy` tracking

**REST API Base**: `http://localhost:8080/api`
- `/api/auth/**` - Public authentication endpoints
- `/api/staff/**` - Staff management (requires JWT)
- `/api/age-groups/**` - Age group management (requires JWT)
- `/api/schedules/**` - Schedule entries and weekly schedules (requires JWT)
- `/api/weekly-schedules/**` - Weekly schedule management (requires JWT)

**Database Schema**:
- Managed entirely via SQL scripts in `backend/` directory
- `init-database.sql` - Complete schema + test data
- DDL mode set to `none` - Hibernate does NOT auto-create/update schema
- When modifying schema, update SQL scripts and re-run `./quick-setup.sh`

### Frontend Architecture

**Structure** (`frontend/src/app`):
- `components/` - UI components (dashboard, login)
  - `dashboard/` - Main app with 4 tabs: Overview, Schedule, Staff, Age Groups
  - `login/` - Authentication form
- `services/` - HTTP clients and business logic
  - `api.service.ts` - All backend API calls
  - `auth.service.ts` - Authentication state and token management
- `models/` - TypeScript interfaces matching backend DTOs
- `guards/` - Route guards for authentication
- `app.routes.ts` - Route configuration

**Key Patterns**:
- Services use RxJS Observables for async operations
- JWT token stored in localStorage, sent via HTTP interceptor
- API service centralizes all backend communication
- Dashboard uses tab-based navigation for different views

**API Service**: Single service (`ApiService`) with methods for all backend endpoints
- Staff operations: `getAllStaff()`, `getActiveStaff()`, `createStaff()`, etc.
- Group operations: `getAllGroups()`, `getActiveGroups()`, etc.
- Schedule operations: `getWeekSchedule()`, `getDailyTotals()`, etc.
- Base URL: `http://localhost:8080/api`

### Data Flow

1. **Authentication**: User logs in → `AuthService` → Backend `/api/auth/login` → JWT token stored → HTTP interceptor adds token to all requests
2. **Schedule Loading**: Dashboard → `ApiService.getWeekSchedule(year, week)` → Backend fetches from DB → Returns `ScheduleEntry[]` with staff details
3. **Data Models**: Backend entities map to TypeScript interfaces in `models.ts`

## Development Credentials

**Default Admin Users** (for development only):
- Username: `alexandre` or `uwe`
- Password: `password123`

**Database**:
- Database: `kita_casa_azul`
- User: `kita_admin`
- Password: See `backend/src/main/resources/application.properties`

## Important Notes

- **CORS**: Currently allows all origins (`*`) in `SecurityConfiguration.corsConfigurationSource()` - restrict in production
- **JWT Secret**: Change `jwt.secret` in `application.properties` before production deployment (minimum 256 bits)
- **Password Encoding**: Uses BCrypt for admin passwords
- **Session Management**: Stateless (no server-side sessions)
- **SQL Logging**: Enabled in development (`spring.jpa.show-sql=true`)
- **Database Timezone**: Set to UTC with `serverTimezone=UTC` in connection string
- **Jackson Date Format**: `yyyy-MM-dd` with Europe/Berlin timezone

## Common Workflows

**Adding a new API endpoint**:
1. Create/update entity in `backend/.../entity/`
2. Create repository in `backend/.../repository/`
3. Add service method in `backend/.../service/`
4. Add controller endpoint in `backend/.../controller/`
5. Update DTO if needed in `backend/.../dto/`
6. Add corresponding interface in `frontend/src/app/models/models.ts`
7. Add method in `frontend/src/app/services/api.service.ts`

**Modifying database schema**:
1. Update SQL scripts in `backend/` (not JPA entities)
2. Run `./quick-setup.sh` to recreate database
3. Update JPA entities to match if needed
4. Restart backend

**Adding a new frontend component**:
1. Generate with `ng generate component components/component-name`
2. Update routing in `app.routes.ts` if needed
3. Add to dashboard tabs if applicable
