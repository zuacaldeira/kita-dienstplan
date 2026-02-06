# 🔒 Security Quick Reference

## 🚀 Setup (3 Steps)

```bash
# 1. Run security migration
mysql -u kita_admin -p kita_casa_azul < security-migration.sql

# 2. Start application
cd kita-spring-api
mvn spring-boot:run

# 3. Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alexandre","password":"password123"}'
```

## 👥 Default Admins

| Username | Password | Full Name |
|----------|----------|-----------|
| `uwe` | `password123` | Otto Uwe |
| `alexandre` | `password123` | Zua Caldeira Alexandre |

⚠️ **CHANGE PASSWORDS IMMEDIATELY!**

## 🔑 Authentication

### Login
```bash
POST /api/auth/login
Body: {"username":"alexandre","password":"password123"}
Response: {"token":"eyJ...","username":"alexandre",...}
```

### Use Token
```bash
GET /api/staff
Header: Authorization: Bearer eyJ...
```

### Get Current User
```bash
GET /api/auth/me
Header: Authorization: Bearer eyJ...
```

## 📊 Audit Tracking

**Automatically tracked:**
- `created_by` - Who created
- `updated_by` - Who last modified
- `created_at` - When created
- `updated_at` - When last modified

**Example:**
```sql
SELECT id, staff_id, created_by, updated_by 
FROM schedule_entries 
WHERE created_by = 'alexandre';
```

## 🔐 Security Features

✅ JWT authentication  
✅ BCrypt password hashing  
✅ 24-hour token expiration  
✅ Audit tracking on all tables  
✅ Only admins can access API  
✅ CORS configured  

## 🛠️ Quick Commands

### Change Password
```sql
-- Generate hash at: https://bcrypt-generator.com/
UPDATE admins SET password = 'NEW_BCRYPT_HASH' WHERE username = 'uwe';
```

### Add New Admin
```sql
INSERT INTO admins (username, password, full_name, email)
VALUES ('elisa', 'BCRYPT_HASH', 'de Sá Zua Caldeira Elisa', 'elisa@kitacasaazul.de');
```

### View Audit History
```sql
SELECT * FROM schedule_entries 
WHERE updated_by = 'uwe' 
ORDER BY updated_at DESC 
LIMIT 10;
```

### Deactivate Admin
```sql
UPDATE admins SET is_active = FALSE WHERE username = 'old_admin';
```

## 🌐 Frontend Example

```javascript
// Login
const login = async () => {
  const res = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      username: 'alexandre',
      password: 'password123'
    })
  });
  const data = await res.json();
  localStorage.setItem('token', data.token);
};

// API Call
const getStaff = async () => {
  const token = localStorage.getItem('token');
  const res = await fetch('/api/staff', {
    headers: {'Authorization': `Bearer ${token}`}
  });
  return await res.json();
};
```

## 🚨 Common Issues

| Problem | Solution |
|---------|----------|
| 401 Unauthorized | Token expired - login again |
| 403 Forbidden | Invalid token - verify format |
| Bad credentials | Wrong username/password |

## 📝 Files Added

1. `security-migration.sql` - Database migration
2. `Admin.java` - Admin entity
3. `AdminRepository.java` - Admin repository
4. `JwtService.java` - JWT token service
5. `JwtAuthenticationFilter.java` - JWT filter
6. `SecurityConfiguration.java` - Security config
7. `AuthenticationController.java` - Login endpoint
8. `AuditingConfiguration.java` - Audit config

## ✅ Verification

```bash
# 1. Check admins exist
mysql> SELECT username, full_name FROM admins;

# 2. Check audit columns exist
mysql> DESCRIBE schedule_entries;
# Should see: created_by, updated_by

# 3. Test login
curl -X POST localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"uwe","password":"password123"}'

# 4. Test protected endpoint
curl -H "Authorization: Bearer YOUR_TOKEN" \
  localhost:8080/api/staff
```

---

**Full documentation:** `SECURITY-GUIDE.md`
