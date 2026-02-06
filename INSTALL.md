# ⚡ QUICK START - 3 Steps to Run

## Step 1: Database Setup (10 seconds)

```bash
cd backend
chmod +x quick-setup.sh
./quick-setup.sh
```

✅ Wait for "Setup complete!"

---

## Step 2: Start Backend

```bash
mvn spring-boot:run
```

✅ Wait for "Started DienstplanApplication"

---

## Step 3: Start Frontend (NEW TERMINAL!)

```bash
cd frontend
npm install
ng serve
```

✅ Wait for "listening on localhost:4200"

---

## Open Browser

Go to: **http://localhost:4200**

Login:
- Username: **alexandre**
- Password: **password123**

---

## ✅ DONE!

You should see the dashboard with 4 tabs:
- Übersicht (Overview)
- Dienstplan (Schedule)
- Mitarbeiter (Staff)
- Altersgruppen (Groups)

---

## If It Doesn't Work

1. Make sure MySQL is running
2. Run `./quick-setup.sh` again
3. Read `README.md` for detailed help
