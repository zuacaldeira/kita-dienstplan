# 🔒 Security & Authentication Guide

## Overview

The Kita Casa Azul API is now secured with **JWT (JSON Web Token) authentication**. Only authorized admins can access the API.

## 👥 Admins

**Two admins are pre-configured:**
- Username: `uwe` (Otto Uwe)
- Username: `alexandre` (Zua Caldeira Alexandre)

**Default Password:** `password123` (⚠️ CHANGE THIS IMMEDIATELY!)

## 🚀 Setup

### 1. Run Security Migration

```bash
mysql -u kita_admin -p kita_casa_azul < security-migration.sql
```

This will:
- ✅ Create `admins` table
- ✅ Add `created_by` and `updated_by` columns to all tables
- ✅ Insert Uwe and Alexandre as admins

### 2. Start the Application

```bash
cd kita-spring-api
mvn spring-boot:run
```

### 3. Change Default Passwords

**IMPORTANT:** Change the default passwords before production use!

Generate BCrypt hash: https://bcrypt-generator.com/

Then update in MySQL:
```sql
UPDATE admins SET password = 'YOUR_BCRYPT_HASH' WHERE username = 'uwe';
UPDATE admins SET password = 'YOUR_BCRYPT_HASH' WHERE username = 'alexandre';
```

## 🔑 Authentication Flow

### 1. Login

**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "username": "alexandre",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "alexandre",
  "fullName": "Zua Caldeira Alexandre",
  "message": "Login successful"
}
```

### 2. Use Token in Requests

Include the token in the `Authorization` header:

```bash
curl -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  http://localhost:8080/api/staff
```

### 3. Token Expiration

Tokens expire after **24 hours**. After expiration, login again to get a new token.

## 📡 API Examples

### Login (No Authentication Required)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alexandre",
    "password": "password123"
  }'
```

Save the returned token!

### Get Current Admin Info

```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/auth/me
```

### Access Protected Endpoints

All other endpoints require authentication:

```bash
# Get all staff
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/staff

# Create schedule entry
curl -X POST \
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
  }' \
  http://localhost:8080/api/schedules/entries
```

## 📊 Audit Tracking

All database changes are automatically tracked:

**Example:** When Alexandre creates a schedule entry:
```sql
SELECT id, staff_id, work_date, created_by, updated_by 
FROM schedule_entries 
WHERE id = 123;
```

Result:
```
id  | staff_id | work_date  | created_by | updated_by
123 | 6        | 2026-02-02 | alexandre  | alexandre
```

When Uwe updates it later:
```
id  | staff_id | work_date  | created_by | updated_by
123 | 6        | 2026-02-02 | alexandre  | uwe
```

**Audit fields in all tables:**
- `created_by` - Admin who created the record
- `updated_by` - Admin who last modified the record
- `created_at` - Timestamp when created
- `updated_at` - Timestamp when last updated

## 🔒 Security Features

### ✅ What's Protected

1. **All API endpoints** (except login)
2. **Database modifications** tracked by admin
3. **JWT tokens** for stateless authentication
4. **BCrypt password** hashing
5. **CORS** configured for security
6. **Session management** - stateless

### ✅ How It Works

1. **Login** → Server generates JWT token
2. **Token** contains username and expiration
3. **Client** sends token with each request
4. **Server** validates token
5. **Audit** automatically tracks admin

## 🛠️ Configuration

### JWT Settings (application.properties)

```properties
# JWT secret key (CHANGE IN PRODUCTION!)
jwt.secret=your-secret-key-minimum-256-bits

# Token expiration (24 hours in milliseconds)
jwt.expiration=86400000
```

### Change Token Expiration

For 12 hours: `jwt.expiration=43200000`  
For 7 days: `jwt.expiration=604800000`

## 🌐 Frontend Integration

### JavaScript Example

```javascript
// Login
const login = async (username, password) => {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({username, password})
  });
  
  const data = await response.json();
  
  // Save token
  localStorage.setItem('token', data.token);
  localStorage.setItem('username', data.username);
  
  return data;
};

// Use token in requests
const getStaff = async () => {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8080/api/staff', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
};

// Create schedule entry
const createEntry = async (entry) => {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8080/api/schedules/entries', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(entry)
  });
  
  return await response.json();
};
```

### React Hook Example

```jsx
import { useState, useEffect } from 'react';

const useAuth = () => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [user, setUser] = useState(null);

  const login = async (username, password) => {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({username, password})
    });
    
    const data = await response.json();
    setToken(data.token);
    setUser(data);
    localStorage.setItem('token', data.token);
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
  };

  return { token, user, login, logout };
};
```

## 🔍 Query Audit History

### Find who created a staff member

```sql
SELECT id, full_name, created_by, created_at 
FROM staff 
WHERE id = 6;
```

### Find all entries created by Uwe

```sql
SELECT * FROM schedule_entries 
WHERE created_by = 'uwe' 
ORDER BY created_at DESC;
```

### Find recent modifications by Alexandre

```sql
SELECT 
    'schedule_entries' as table_name,
    id,
    updated_by,
    updated_at
FROM schedule_entries 
WHERE updated_by = 'alexandre'

UNION ALL

SELECT 
    'staff' as table_name,
    id,
    updated_by,
    updated_at
FROM staff 
WHERE updated_by = 'alexandre'

ORDER BY updated_at DESC
LIMIT 20;
```

## 🚨 Troubleshooting

### Problem: "401 Unauthorized"
**Solution:** Token is missing or expired. Login again.

### Problem: "403 Forbidden"
**Solution:** Token is invalid. Verify you're sending it correctly.

### Problem: "Bad credentials"
**Solution:** Wrong username or password.

### Problem: Audit fields show "system"
**Solution:** The change was made before authentication was added, or by a background process.

## 📝 Best Practices

1. ✅ **Change default passwords** immediately
2. ✅ **Use HTTPS** in production
3. ✅ **Store tokens** securely (localStorage for web, Keychain for mobile)
4. ✅ **Handle token expiration** gracefully
5. ✅ **Never commit** JWT secret to version control
6. ✅ **Rotate secrets** periodically
7. ✅ **Use strong passwords** (BCrypt with high cost factor)
8. ✅ **Monitor audit logs** for suspicious activity

## 🎯 Next Steps

1. ✅ Run `security-migration.sql`
2. ✅ Start the application
3. ✅ Test login with both admins
4. ✅ Change default passwords
5. ✅ Update frontend to use authentication
6. ✅ Test audit tracking
7. ✅ Configure production secrets

---

**Security is enabled!** Only Uwe and Alexandre can access the API. All changes are tracked. 🔒
