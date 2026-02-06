# 🏫 Kita Casa Azul - Dienstplan REST API

Complete Spring Boot REST API with MySQL database, JWT authentication, and audit tracking for Kita Casa Azul work schedule management.

## 📦 What's Included

This project provides a **production-ready** REST API with:

- ✅ **Spring Boot 3.2.2** with Java 17
- ✅ **MySQL 8.0+** database integration
- ✅ **Spring Data JPA** entities and repositories
- ✅ **JWT Authentication** (only admins can access)
- ✅ **Audit Tracking** (created_by/updated_by on all records)
- ✅ **Auto-calculation** of working hours and breaks
- ✅ **Complete documentation**

## 🚀 Quick Start (10 minutes)

### Prerequisites

- ☑️ Java 17+
- ☑️ Maven 3.6+
- ☑️ MySQL 8.0+

### Step 1: Setup Database

```bash
# Create database and user
sudo mysql -u root -p

CREATE DATABASE kita_casa_azul CHARACTER SET utf8mb4;
CREATE USER 'kita_admin'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON kita_casa_azul.* TO 'kita_admin'@'localhost';
EXIT;

# Run schema
mysql -u kita_admin -p kita_casa_azul < mysql-schema.sql

# Load sample data
mysql -u kita_admin -p kita_casa_azul < mysql-sample-data.sql

# Add security (admins table + audit columns)
mysql -u kita_admin -p kita_casa_azul < security-migration.sql
```

### Step 2: Configure Application

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.password=your_password
```

**Optional:** Change JWT secret (for production):
```properties
jwt.secret=your-very-long-secret-key-minimum-256-bits
```

### Step 3: Build & Run

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

Application starts on **http://localhost:8080**

### Step 4: Test Authentication

```bash
# Login as Alexandre
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alexandre",
    "password": "password123"
  }'

# Save the returned token and use it:
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/staff
```

✅ **You're done!** API is running and secured!

## 📁 Project Structure

```
kita-spring-api/
├── pom.xml                              # Maven dependencies
├── README.md                            # This file (overview)
├── QUICK-START.md                       # Quick setup guide
├── SECURITY-GUIDE.md                    # Complete security documentation
├── SECURITY-QUICK-REF.md                # Security quick reference
├── mysql-schema.sql                     # Database schema
├── mysql-sample-data.sql                # Sample data (week 6)
├── security-migration.sql               # Security tables + audit columns
│
└── src/main/
    ├── java/com/kita/dienstplan/
    │   ├── DienstplanApplication.java   # Main application
    │   │
    │   ├── entity/                      # JPA Entities (5 files)
    │   │   ├── Admin.java               # Admin users
    │   │   ├── WeeklySchedule.java      # Week definitions
    │   │   ├── Group.java               # Kindergarten groups
    │   │   ├── Staff.java               # Employees
    │   │   └── ScheduleEntry.java       # Shifts (auto-calc + audit)
    │   │
    │   ├── repository/                  # Spring Data JPA (5 files)
    │   │   ├── AdminRepository.java
    │   │   ├── WeeklyScheduleRepository.java
    │   │   ├── GroupRepository.java
    │   │   ├── StaffRepository.java
    │   │   └── ScheduleEntryRepository.java
    │   │
    │   ├── service/                     # Business Logic
    │   │   └── ScheduleService.java
    │   │
    │   ├── controller/                  # REST Controllers (3 files)
    │   │   ├── AuthenticationController.java
    │   │   ├── ScheduleController.java
    │   │   └── CRUDControllers.java
    │   │
    │   ├── dto/                         # Data Transfer Objects
    │   │   ├── ScheduleEntryDTO.java
    │   │   └── TotalDTOs.java
    │   │
    │   └── security/                    # Security Configuration (4 files)
    │       ├── JwtService.java
    │       ├── JwtAuthenticationFilter.java
    │       ├── SecurityConfiguration.java
    │       └── AuditingConfiguration.java
    │
    └── resources/
        └── application.properties       # Configuration
```

## 🔒 Security Features

### Authentication

**Two admins are pre-configured:**
- Username: `uwe` | Password: `password123`
- Username: `alexandre` | Password: `password123`

⚠️ **IMPORTANT:** Change default passwords in production!

**All API endpoints require authentication** (except `/api/auth/login`)

### Audit Tracking

Every database modification is automatically tracked:
- `created_by` - Admin who created the record
- `updated_by` - Admin who last modified the record
- `created_at` - When created
- `updated_at` - When last updated

**Example:**
```sql
SELECT id, staff_id, created_by, updated_by 
FROM schedule_entries 
WHERE created_by = 'alexandre';
```

## 📡 API Endpoints

### Authentication
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/me` - Get current admin info

### Weekly Schedules
- `GET /api/weekly-schedules` - Get all schedules
- `GET /api/weekly-schedules/week/{year}/{week}` - Get specific week
- `POST /api/weekly-schedules` - Create schedule
- `PUT /api/weekly-schedules/{id}` - Update schedule
- `DELETE /api/weekly-schedules/{id}` - Delete schedule

### Groups
- `GET /api/groups` - Get all groups
- `GET /api/groups/active` - Get active groups
- `POST /api/groups` - Create group
- `PUT /api/groups/{id}` - Update group
- `DELETE /api/groups/{id}` - Delete group

### Staff
- `GET /api/staff` - Get all staff
- `GET /api/staff/active` - Get active staff
- `GET /api/staff/group/{groupId}` - Get staff by group
- `POST /api/staff` - Create staff member
- `PUT /api/staff/{id}` - Update staff
- `DELETE /api/staff/{id}` - Delete staff

### Schedule Entries
- `GET /api/schedules/week/{year}/{week}` - Get week schedule
- `GET /api/schedules/staff/{id}/week/{year}/{week}` - Get staff schedule
- `GET /api/schedules/date/{date}` - Get entries for date
- `GET /api/schedules/on-duty?date=X&time=Y` - Who's working
- `GET /api/schedules/daily-totals/{year}/{week}` - Daily totals
- `POST /api/schedules/entries` - Create entry
- `PUT /api/schedules/entries/{id}` - Update entry
- `DELETE /api/schedules/entries/{id}` - Delete entry

## 🔥 Key Features

### 1. Auto-Calculation
Working hours and breaks are automatically calculated when creating/updating schedule entries:

```java
@PrePersist
@PreUpdate
public void calculateWorkingHours() {
    // Automatically calculates:
    // - Total minutes from start/end time
    // - Break time (30 min if > 6 hours)
    // - Working minutes (total - break)
}
```

### 2. Spring Data JPA Queries
Custom repository methods without writing SQL:

```java
List<Staff> findByGroupIdAndActive(Long groupId);
List<ScheduleEntry> findWhoIsWorkingAt(LocalDate date, LocalTime time);
List<Object[]> getDailyTotals(Integer weekNumber, Integer year);
```

### 3. JWT Authentication
Stateless authentication with JWT tokens:
- Login → Get token
- Include token in `Authorization: Bearer TOKEN` header
- Token expires after 24 hours

### 4. Automatic Audit Tracking
All changes tracked automatically via JPA:
- `@CreatedBy` annotation
- `@LastModifiedBy` annotation
- No manual code needed!

## 🛠️ Configuration

### Database Connection
Edit `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/kita_casa_azul
spring.datasource.username=kita_admin
spring.datasource.password=your_password
```

### JWT Settings
```properties
jwt.secret=your-secret-key-minimum-256-bits
jwt.expiration=86400000  # 24 hours in milliseconds
```

### Server Port
```properties
server.port=8080  # Change if needed
```

## 📚 Documentation

- **README.md** (this file) - Project overview
- **QUICK-START.md** - Quick setup guide
- **SECURITY-GUIDE.md** - Complete security documentation
- **SECURITY-QUICK-REF.md** - Security quick reference

## 🧪 Testing

### Test Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alexandre","password":"password123"}'
```

### Test Protected Endpoint
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/staff
```

### Create Schedule Entry
```bash
curl -X POST http://localhost:8080/api/schedules/entries \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "weeklyScheduleId": 1,
    "staffId": 6,
    "dayOfWeek": 0,
    "workDate": "2026-02-02",
    "startTime": "09:00",
    "endTime": "17:00",
    "status": "normal"
  }'
```

## 🌐 Frontend Integration

### JavaScript Example
```javascript
// Login
const login = async (username, password) => {
  const res = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({username, password})
  });
  const data = await res.json();
  localStorage.setItem('token', data.token);
  return data;
};

// API call with authentication
const getStaff = async () => {
  const token = localStorage.getItem('token');
  const res = await fetch('http://localhost:8080/api/staff', {
    headers: {'Authorization': `Bearer ${token}`}
  });
  return await res.json();
};
```

## 🚨 Troubleshooting

### Build fails
```bash
mvn clean install -U
```

### Port 8080 already in use
Change port in `application.properties`:
```properties
server.port=9090
```

### Database connection error
1. Verify MySQL is running: `sudo systemctl status mysql`
2. Check credentials in `application.properties`
3. Verify database exists: `SHOW DATABASES;`

### 401 Unauthorized
Token expired or missing. Login again to get new token.

## 📦 Build for Production

```bash
# Build JAR file
mvn clean package

# Run JAR
java -jar target/dienstplan-1.0.0.jar
```

## 🔐 Production Checklist

- [ ] Change default admin passwords
- [ ] Change JWT secret key
- [ ] Configure CORS for specific domains
- [ ] Use HTTPS/SSL
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate`
- [ ] Disable SQL logging: `spring.jpa.show-sql=false`
- [ ] Set up database backups
- [ ] Configure connection pooling
- [ ] Add API rate limiting
- [ ] Set up monitoring/logging

## 🎯 Technologies Used

- **Spring Boot** 3.2.2
- **Spring Security** with JWT
- **Spring Data JPA**
- **MySQL Connector/J**
- **Hibernate** (JPA implementation)
- **Lombok** (code generation)
- **Jackson** (JSON processing)
- **jjwt** (JWT library)
- **Maven** (build tool)

## 📖 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)
- [Spring Security](https://docs.spring.io/spring-security/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/)
- [JWT Introduction](https://jwt.io/)

## ✨ Features Summary

✅ Complete CRUD operations for all entities  
✅ JWT authentication with 24-hour tokens  
✅ Auto-calculation of working hours and breaks  
✅ Automatic audit tracking on all changes  
✅ Pre-configured admins (Uwe and Alexandre)  
✅ Sample data included (Week 6)  
✅ Production-ready architecture  
✅ Comprehensive documentation  
✅ RESTful API design  
✅ MySQL database integration  

## 🎓 Next Steps

1. ✅ Follow QUICK-START.md for setup
2. ✅ Test authentication
3. ✅ Change default passwords
4. ✅ Build your frontend
5. ✅ Deploy to production

---

**Version:** 1.0.0  
**Java:** 17+  
**Spring Boot:** 3.2.2  
**Database:** MySQL 8.0+  
**Created:** February 2026  

🎉 **Happy Coding!**
