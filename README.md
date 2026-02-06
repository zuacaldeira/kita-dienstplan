# 🏫 Kita Casa Azul - Dienstplan System

**Simple Installation Guide - Angular Version**

---

## 📋 What You Need

Before starting, install these:

1. **Java 17** - [Download here](https://adoptium.net/)
2. **MySQL 8.0** - [Download here](https://dev.mysql.com/downloads/)
3. **Node.js 18+** - [Download here](https://nodejs.org/)
4. **Maven** - [Download here](https://maven.apache.org/download.cgi)
5. **Angular CLI** - Install with: `npm install -g @angular/cli`

---

## 🚀 Installation Steps

### Step 1: Setup Database (10 seconds)

```bash
cd backend
chmod +x quick-setup.sh
./quick-setup.sh
```

**That's it!** The script creates everything automatically.

You'll see:
```
✅ Database and user created
✅ Data loaded
✅ Setup complete!
```

---

### Step 2: Start Backend

```bash
# Still in backend folder
mvn spring-boot:run
```

Wait until you see:
```
Started DienstplanApplication in X seconds
```

**Backend is now running on http://localhost:8080** ✅

---

### Step 3: Start Frontend

Open a **NEW terminal window**:

```bash
cd frontend
npm install
ng serve
```

Wait until you see:
```
Angular Live Development Server is listening on localhost:4200
```

**Frontend is now running on http://localhost:4200** ✅

---

### Step 4: Login

1. Open browser: **http://localhost:4200**
2. Login with:
   - **Username:** alexandre
   - **Password:** password123

**Done!** 🎉

---

## 📊 What You'll See

### Dashboard with 4 tabs:

1. **Übersicht** (Overview)
   - Active staff count
   - Age groups count
   - Current week number

2. **Dienstplan** (Schedule)
   - Weekly view
   - Staff schedules
   - Navigate weeks with ← →

3. **Mitarbeiter** (Staff)
   - List of all staff
   - Roles and groups
   - Edit buttons (UI ready)

4. **Altersgruppen** (Age Groups)
   - Kindergarten groups
   - Group descriptions

---

## 🔧 If Something Goes Wrong

### Backend won't start?

**Check MySQL is running:**
```bash
sudo systemctl status mysql
# or on macOS:
brew services list
```

**If MySQL isn't running:**
```bash
sudo systemctl start mysql
# or on macOS:
brew services start mysql
```

Then run the setup script again:
```bash
cd backend
./quick-setup.sh
mvn spring-boot:run
```

---

### Frontend won't start?

**Delete and reinstall:**
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
ng serve
```

---

### Can't login?

1. Make sure backend is running (check http://localhost:8080)
2. Use correct credentials:
   - Username: **alexandre**
   - Password: **password123**
3. Check browser console (F12) for errors

---

## 📂 Project Structure

```
kita-dienstplan-angular/
├── backend/              ← Spring Boot API
│   ├── quick-setup.sh   ← DATABASE SETUP SCRIPT
│   ├── pom.xml
│   └── src/
└── frontend/            ← Angular App
    ├── package.json
    └── src/
```

---

## 🔐 Default Passwords

**⚠️ These are for DEVELOPMENT only!**

### Database:
- Database: `kita_casa_azul`
- User: `kita_admin`
- Password: `password123`

### Admin Users:
- Username: `alexandre` or `uwe`
- Password: `password123`

**Change these before production!**

---

## 🛠️ Useful Commands

### Reset Database
```bash
cd backend
./quick-setup.sh
```

### Stop Backend
Press `Ctrl + C` in backend terminal

### Stop Frontend
Press `Ctrl + C` in frontend terminal

### View Database
```bash
mysql -u kita_admin -p kita_casa_azul
# Password: password123

# Then run:
SHOW TABLES;
SELECT * FROM staff;
```

---

## ✅ Next Steps

1. ✅ Explore the dashboard
2. ✅ Check the schedule view
3. ✅ Look at staff and groups
4. ✅ Read the source code in `src/`
5. ✅ Customize as needed

---

## 📞 Quick Help

**Backend not connecting to database?**
- Run `./quick-setup.sh` again
- Check MySQL is running

**Frontend shows errors?**
- Make sure backend is running
- Check http://localhost:8080 works
- Clear browser cache

**Port already in use?**
- Backend: Change port in `application.properties`
- Frontend: Run `ng serve --port 4300`

---

**That's it! You now have a working staff scheduling system!** 🎉

**Questions? Check the files in backend/ and frontend/ folders.**
