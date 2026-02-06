# Kita Casa Azul - Spring Boot REST API

Complete Spring Boot REST API with JPA entities and repositories for the Kita Dienstplan system.

## 🚀 Features

- ✅ **Spring Boot 3.2.2** with Java 17
- ✅ **Spring Data JPA** for database operations
- ✅ **MySQL 8.0+** database
- ✅ **Auto-calculation** of working hours and breaks via JPA lifecycle callbacks
- ✅ **RESTful API** with CRUD operations
- ✅ **Lombok** for reducing boilerplate code
- ✅ **Transaction management**
- ✅ **CORS enabled** for frontend integration

## 📁 Project Structure

```
kita-spring-api/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/kita/dienstplan/
│       │   ├── DienstplanApplication.java          # Main application class
│       │   ├── entity/                              # JPA entities
│       │   │   ├── WeeklySchedule.java
│       │   │   ├── Group.java
│       │   │   ├── Staff.java
│       │   │   └── ScheduleEntry.java               # With @PrePersist/@PreUpdate
│       │   ├── repository/                          # Spring Data JPA repositories
│       │   │   ├── WeeklyScheduleRepository.java
│       │   │   ├── GroupRepository.java
│       │   │   ├── StaffRepository.java
│       │   │   └── ScheduleEntryRepository.java
│       │   ├── service/                             # Business logic
│       │   │   └── ScheduleService.java
│       │   ├── controller/                          # REST controllers
│       │   │   ├── ScheduleController.java
│       │   │   └── CRUDControllers.java
│       │   └── dto/                                 # Data Transfer Objects
│       │       ├── ScheduleEntryDTO.java
│       │       └── TotalDTOs.java
│       └── resources/
│           └── application.properties               # Configuration
```

## 🛠️ Setup

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Your MySQL database already set up (from previous steps)

### Step 1: Clone/Extract Project

```bash
cd kita-spring-api
```

### Step 2: Configure Database

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/kita_casa_azul
spring.datasource.username=kita_admin
spring.datasource.password=your_actual_password
```

### Step 3: Build Project

```bash
mvn clean install
```

### Step 4: Run Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/dienstplan-1.0.0.jar
```

Application will start on **http://localhost:8080**

## 📡 API Endpoints

### Weekly Schedules

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/weekly-schedules` | Get all weekly schedules |
| GET | `/api/weekly-schedules/{id}` | Get schedule by ID |
| GET | `/api/weekly-schedules/week/{year}/{week}` | Get schedule by week number |
| POST | `/api/weekly-schedules` | Create new weekly schedule |
| PUT | `/api/weekly-schedules/{id}` | Update weekly schedule |
| DELETE | `/api/weekly-schedules/{id}` | Delete weekly schedule |

### Groups

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/groups` | Get all groups |
| GET | `/api/groups/active` | Get active groups only |
| GET | `/api/groups/{id}` | Get group by ID |
| POST | `/api/groups` | Create new group |
| PUT | `/api/groups/{id}` | Update group |
| DELETE | `/api/groups/{id}` | Delete group |

### Staff

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/staff` | Get all staff |
| GET | `/api/staff/active` | Get active staff only |
| GET | `/api/staff/{id}` | Get staff by ID |
| GET | `/api/staff/group/{groupId}` | Get staff by group |
| POST | `/api/staff` | Create new staff member |
| PUT | `/api/staff/{id}` | Update staff |
| DELETE | `/api/staff/{id}` | Delete staff |

### Schedule Entries

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/schedules/week/{year}/{week}` | Get all entries for a week |
| GET | `/api/schedules/staff/{staffId}/week/{year}/{week}` | Get staff schedule for week |
| GET | `/api/schedules/date/{date}` | Get all entries for a date |
| GET | `/api/schedules/on-duty?date=2026-02-02&time=10:00` | Who is working at specific time |
| GET | `/api/schedules/daily-totals/{year}/{week}` | Get daily totals for a week |
| POST | `/api/schedules/entries` | Create schedule entry |
| PUT | `/api/schedules/entries/{id}` | Update schedule entry |
| DELETE | `/api/schedules/entries/{id}` | Delete schedule entry |

## 🔥 Auto-Calculation Feature

The **ScheduleEntry** entity has `@PrePersist` and `@PreUpdate` methods that **automatically calculate** working hours and breaks:

```java
@PrePersist
@PreUpdate
public void calculateWorkingHours() {
    if (!"normal".equalsIgnoreCase(status) || startTime == null || endTime == null) {
        workingHoursMinutes = 0;
        breakMinutes = 0;
        return;
    }

    long totalMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
    double totalHours = totalMinutes / 60.0;
    
    // 30 minutes break if > 6 hours
    breakMinutes = totalHours > 6 ? 30 : 0;
    workingHoursMinutes = (int) (totalMinutes - breakMinutes);
}
```

**This means:**
- When you create a schedule entry with `start=09:00` and `end=17:00`
- JPA automatically calculates: `workingHoursMinutes = 450` (7.5 hours) and `breakMinutes = 30`
- No need for database triggers or manual calculation!

## 📝 Example API Calls

### Get Schedule for Week 6, 2026

```bash
curl http://localhost:8080/api/schedules/week/2026/6
```

### Create a Schedule Entry

```bash
curl -X POST http://localhost:8080/api/schedules/entries \
  -H "Content-Type: application/json" \
  -d '{
    "weeklyScheduleId": 1,
    "staffId": 5,
    "dayOfWeek": 0,
    "workDate": "2026-02-02",
    "startTime": "09:00",
    "endTime": "17:00",
    "status": "normal"
  }'
```

Response:
```json
{
  "id": 123,
  "staffName": "Zua Caldeira Alexandre",
  "workingHoursMinutes": 450,
  "breakMinutes": 30,
  "workingHoursFormatted": "7:30",
  "breakTimeFormatted": "0:30"
}
```

### Get Daily Totals

```bash
curl http://localhost:8080/api/schedules/daily-totals/2026/6
```

### Who is Working at 10:00 on Feb 2?

```bash
curl "http://localhost:8080/api/schedules/on-duty?date=2026-02-02&time=10:00"
```

## 🗄️ Database Integration

The API works with your existing MySQL database:

- **No schema changes needed** - Uses existing tables
- **JPA entities** map directly to MySQL tables
- **Repositories** provide query methods
- **Transactions** ensure data consistency

## 🔧 Configuration Options

### application.properties

```properties
# Database connection
spring.datasource.url=jdbc:mysql://localhost:3306/kita_casa_azul
spring.datasource.username=kita_admin
spring.datasource.password=your_password

# JPA settings
spring.jpa.hibernate.ddl-auto=none  # Don't auto-generate schema
spring.jpa.show-sql=true            # Log SQL queries

# Server port
server.port=8080

# Timezone
spring.jackson.time-zone=Europe/Berlin
```

### Change Port

Edit `application.properties`:
```properties
server.port=9090
```

### Production Settings

For production, change:
```properties
spring.jpa.show-sql=false
logging.level.com.kita.dienstplan=INFO
spring.jpa.hibernate.ddl-auto=validate
```

## 🧪 Testing

### Test with curl

```bash
# Get all staff
curl http://localhost:8080/api/staff

# Get Alexandre's schedule for week 6
curl http://localhost:8080/api/schedules/staff/6/week/2026/6

# Create new group
curl -X POST http://localhost:8080/api/groups \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Group", "isActive": true}'
```

### Test with Postman

1. Import the API endpoints
2. Set base URL to `http://localhost:8080`
3. Test all CRUD operations

## 📦 Build for Production

```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/dienstplan-1.0.0.jar
```

## 🔐 Security (TODO)

For production, add:
- Spring Security
- JWT authentication
- Role-based access control
- HTTPS/SSL

## 🌐 Frontend Integration

CORS is enabled for all origins. To connect your web calculator:

```javascript
// Fetch schedule for week 6
fetch('http://localhost:8080/api/schedules/week/2026/6')
  .then(response => response.json())
  .then(data => console.log(data));
```

## 🐛 Troubleshooting

### Port already in use
```bash
# Change port in application.properties
server.port=9090
```

### Database connection failed
```bash
# Check MySQL is running
sudo systemctl status mysql

# Verify credentials in application.properties
```

### Build errors
```bash
# Clean and rebuild
mvn clean install -U
```

## 📚 Technology Stack

- **Spring Boot** 3.2.2
- **Spring Data JPA** - Database access
- **Hibernate** - ORM
- **MySQL Connector** - Database driver
- **Lombok** - Code generation
- **Jackson** - JSON processing
- **Maven** - Build tool

## 🎯 Next Steps

1. ✅ Set up database connection
2. ✅ Run the application
3. ✅ Test API endpoints
4. 📝 Add Spring Security (optional)
5. 🌐 Connect frontend
6. 🚀 Deploy to production

## 📖 Documentation

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [MySQL Connector/J](https://dev.mysql.com/doc/connector-j/en/)

---

**Version:** 1.0.0  
**Java:** 17+  
**Spring Boot:** 3.2.2  
**Database:** MySQL 8.0+
