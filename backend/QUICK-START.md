# 🚀 Quick Start - Spring Boot API

## Prerequisites Checklist

- [ ] Java 17+ installed (`java -version`)
- [ ] Maven 3.6+ installed (`mvn -version`)
- [ ] MySQL 8.0+ running
- [ ] Database `kita_casa_azul` created and populated

## 5-Minute Setup

### 1. Configure Database (2 minutes)

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.username=kita_admin
spring.datasource.password=YOUR_PASSWORD_HERE
```

### 2. Build Project (1 minute)

```bash
cd kita-spring-api
mvn clean install
```

### 3. Run Application (1 minute)

```bash
mvn spring-boot:run
```

### 4. Test API (1 minute)

Open browser: **http://localhost:8080/api/staff**

Or with curl:
```bash
curl http://localhost:8080/api/staff
```

## ✅ Success!

If you see JSON data with staff members, **you're done!** 🎉

## 📍 Key Endpoints

```
http://localhost:8080/api/staff
http://localhost:8080/api/groups
http://localhost:8080/api/schedules/week/2026/6
http://localhost:8080/api/schedules/daily-totals/2026/6
```

## 🔥 Auto-Calculation Demo

Create a schedule entry:

```bash
curl -X POST http://localhost:8080/api/schedules/entries \
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

**Notice:** Working hours (7:30) and break (0:30) are **automatically calculated**!

Response:
```json
{
  "workingHoursMinutes": 450,
  "breakMinutes": 30,
  "workingHoursFormatted": "7:30",
  "breakTimeFormatted": "0:30"
}
```

## 🐛 Troubleshooting

### Build fails
```bash
mvn clean install -U
```

### Port 8080 in use
Change in `application.properties`:
```properties
server.port=9090
```

### Database connection error
1. Check MySQL is running: `sudo systemctl status mysql`
2. Verify database exists: `SHOW DATABASES;`
3. Check credentials in `application.properties`

## 📖 Next Steps

1. Read `README.md` for full documentation
2. Test all API endpoints
3. Integrate with your frontend
4. Deploy to production

---

**Questions?** Check `README.md` for detailed docs!
