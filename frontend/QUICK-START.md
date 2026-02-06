# 🚀 Quick Start - Angular Frontend

## 5-Minute Setup

### 1. Prerequisites Check

```bash
node --version  # Should be 18+
npm --version   # Should be 9+
ng version      # Should be 17+
```

If Angular CLI not installed:
```bash
npm install -g @angular/cli
```

### 2. Install Dependencies (2 minutes)

```bash
cd kita-angular-frontend
npm install
```

### 3. Start Backend

```bash
# In another terminal, start Spring Boot backend
cd kita-spring-api
mvn spring-boot:run
```

Backend must be running on **http://localhost:8080**

### 4. Start Frontend (1 minute)

```bash
ng serve
# or
npm start
```

Browser opens automatically at **http://localhost:4200**

### 5. Login (1 minute)

```
Username: alexandre
Password: password123
```

## ✅ You're Done!

You should now see the dashboard! 🎉

## 🎯 What You Can Do

✅ View dashboard overview  
✅ See weekly schedules  
✅ Browse staff list  
✅ View groups  
✅ Navigate between weeks  

## 🐛 Troubleshooting

### "Cannot connect to backend"
→ Make sure Spring Boot is running on port 8080

### "Login failed"
→ Check credentials: `alexandre` / `password123`

### "npm install fails"
→ Try: `npm install --legacy-peer-deps`

### Port 4200 already in use
→ Change port: `ng serve --port 4300`

### Angular CLI not found
→ Install: `npm install -g @angular/cli`

## 📝 Next Steps

1. ✅ Explore the 4 dashboard tabs
2. ✅ Try changing weeks in Schedule tab
3. ✅ View staff and groups
4. ✅ Check the code in `src/app/components/`
5. ✅ Start customizing!

## 🔧 Development Commands

```bash
# Development server
ng serve

# Production build
ng build --configuration=production

# Run tests
ng test

# Code linting
ng lint
```

---

**Full docs:** `README.md`
