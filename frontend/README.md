# 🅰️ Kita Casa Azul - Angular Frontend

Modern Angular 17 frontend for Kita Casa Azul Dienstplan Management System.

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ 
- npm or yarn
- Angular CLI 17+
- Spring Boot backend running on `http://localhost:8080`

### Installation

```bash
# Install Angular CLI globally (if not installed)
npm install -g @angular/cli

# Install dependencies
npm install

# Start development server
ng serve
# or
npm start
```

App opens at **http://localhost:4200**

## 📁 Project Structure

```
kita-angular-frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── login/
│   │   │   │   ├── login.component.ts
│   │   │   │   ├── login.component.html
│   │   │   │   └── login.component.css
│   │   │   └── dashboard/
│   │   │       ├── dashboard.component.ts
│   │   │       ├── dashboard.component.html
│   │   │       └── dashboard.component.css
│   │   ├── services/
│   │   │   ├── auth.service.ts
│   │   │   └── api.service.ts
│   │   ├── guards/
│   │   │   ├── auth.guard.ts
│   │   │   └── jwt.interceptor.ts
│   │   ├── models/
│   │   │   └── models.ts
│   │   ├── app.component.ts
│   │   ├── app.config.ts
│   │   └── app.routes.ts
│   ├── environments/
│   │   └── environment.ts
│   ├── index.html
│   ├── main.ts
│   └── styles.css
├── angular.json
├── tsconfig.json
├── proxy.conf.json
└── package.json
```

## 🔑 Default Login Credentials

- **Username:** `alexandre` or `uwe`
- **Password:** `password123`

⚠️ Change these in production!

## ✨ Features

### 🔐 Authentication
- JWT-based login
- Protected routes with Auth Guard
- HTTP Interceptor for automatic token injection
- Auto-redirect on token expiration
- Persistent login (localStorage)

### 📊 Dashboard
- **Overview Tab**
  - Total staff count
  - Active groups
  - Current week display
  - Quick actions

- **Schedule Tab (Dienstplan)**
  - Weekly schedule view
  - Navigate between weeks
  - Staff schedules
  - Status indicators (normal, frei, krank, etc.)

- **Staff Tab (Mitarbeiter)**
  - List all active staff
  - Staff details (role, employment type)
  - Add new staff (TODO)
  - Edit staff (TODO)

- **Groups Tab (Gruppen)**
  - List all groups
  - Group descriptions
  - Add new groups (TODO)

## 🎨 Design

### Technology Stack
- **Angular** 17 (Standalone Components)
- **TypeScript** 5.2+
- **RxJS** 7.8+
- **HttpClient** for API calls

### Features
- Standalone components (no NgModule)
- Functional guards and interceptors
- Type-safe models with TypeScript
- Reactive programming with RxJS

### Color Scheme
- Primary: `#667eea` (purple-blue gradient)
- Secondary: `#764ba2` (purple)
- Success: `#4CAF50`
- Error: `#f44336`
- Background: `#f5f5f5`

## 🔧 Configuration

### API URL

Default proxy configuration in `proxy.conf.json`:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

For production, update `src/environments/environment.ts`:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-api-url.com/api'
};
```

## 🌐 API Integration

### Services

All API calls are centralized in services:

```typescript
// Authentication
this.authService.login(username, password).subscribe(...);

// Get staff
this.apiService.getActiveStaff().subscribe(...);

// Get schedule for week
this.apiService.getWeekSchedule(2026, 6).subscribe(...);
```

### Authentication Flow

1. User logs in → Token saved to localStorage
2. HTTP Interceptor adds `Authorization: Bearer TOKEN` to all requests
3. On 401 error → Auto redirect to login
4. User logs out → Token cleared from localStorage

### Type Safety

All API responses are typed with TypeScript interfaces:

```typescript
export interface Staff {
  id: number;
  fullName: string;
  role: string;
  // ... more fields
}
```

## 📱 Components

### Login Component (`/login`)
- Username/password form
- Error handling
- Modern gradient design
- Auto-focus on username field
- Form validation

### Dashboard Component (`/`)
- Protected route (requires login)
- 4 tabs: Overview, Schedule, Staff, Groups
- Header with user info and logout button
- Responsive navigation
- Lazy loading of tab data

## 🛠️ Development

### Available Scripts

```bash
# Start development server (port 4200)
ng serve

# Build for production
ng build

# Run tests
ng test

# Run linter
ng lint
```

### Development Server

```bash
ng serve
```

Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

### Build

```bash
ng build
```

Build artifacts stored in `dist/` directory.

### Production Build

```bash
ng build --configuration=production
```

Optimized production build with:
- AOT compilation
- Tree shaking
- Minification
- Lazy loading

## 🚀 Deployment

### Build for Production

```bash
ng build --configuration=production
```

### Deploy to Static Hosting

The `dist/kita-dienstplan` folder contains all files ready for deployment to:
- **Netlify**
- **Vercel**
- **AWS S3 + CloudFront**
- **Firebase Hosting**
- **Azure Static Web Apps**
- Any static file hosting

### Environment Variables

Create `environment.prod.ts`:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-production-api.com/api'
};
```

## 🐛 Troubleshooting

### CORS Errors
- Ensure Spring Boot backend has CORS enabled
- Check `SecurityConfiguration.java` in backend
- Use proxy in development (`proxy.conf.json`)

### API Connection Failed
- Verify backend is running on port 8080
- Check proxy configuration
- Verify network settings

### Login Not Working
- Check browser console for errors
- Verify credentials (alexandre / password123)
- Ensure backend security is configured

### Token Expired
- Tokens expire after 24 hours
- Simply login again
- Auto-redirect handles this

### Port 4200 in Use
- Change port: `ng serve --port 4300`
- Or kill process using port 4200

## 📚 Angular Features Used

### Standalone Components
```typescript
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html'
})
```

### Functional Guards
```typescript
export const authGuard = () => {
  const authService = inject(AuthService);
  return authService.isAuthenticated ? true : false;
};
```

### HTTP Interceptors
```typescript
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  // Add token to requests
  const token = authService.getToken();
  req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` }});
  return next(req);
};
```

### RxJS Observables
```typescript
this.apiService.getStaff().subscribe({
  next: (data) => this.staff = data,
  error: (error) => console.error(error)
});
```

## 🎯 Roadmap / TODO

- [ ] Add schedule entry creation form
- [ ] Implement staff CRUD operations
- [ ] Add group management
- [ ] Weekly totals display
- [ ] Export to PDF/Excel
- [ ] Calendar view
- [ ] Mobile responsive improvements
- [ ] Dark mode
- [ ] Notifications
- [ ] Search and filters
- [ ] Unit tests
- [ ] E2E tests

## 🤝 Integration with Backend

### Backend Must Be Running

```bash
# In backend directory
cd kita-spring-api
mvn spring-boot:run
```

Backend should be at `http://localhost:8080`

### API Endpoints Used

- `POST /api/auth/login` - Authentication
- `GET /api/auth/me` - Current user
- `GET /api/staff` - All staff
- `GET /api/staff/active` - Active staff only
- `GET /api/groups` - All groups
- `GET /api/groups/active` - Active groups
- `GET /api/schedules/week/{year}/{week}` - Week schedule
- `GET /api/schedules/daily-totals/{year}/{week}` - Daily totals

## 📖 Documentation

- Backend API: See `kita-spring-api/README.md`
- Security: See `SECURITY-GUIDE.md`
- Angular Docs: https://angular.dev

## ✨ Features Highlight

### Current Features
✅ JWT Authentication with interceptor  
✅ Protected Routes with guard  
✅ Dashboard with 4 tabs  
✅ Staff listing  
✅ Groups listing  
✅ Weekly schedule view  
✅ Week navigation  
✅ Statistics overview  
✅ Responsive design  
✅ TypeScript type safety  
✅ RxJS observables  

### Coming Soon
🚧 Schedule entry creation  
🚧 Staff CRUD operations  
🚧 Group management  
🚧 Advanced filtering  
🚧 Reports generation  
🚧 Unit tests  
🚧 E2E tests  

---

**Version:** 1.0.0  
**Angular:** 17.0.0  
**TypeScript:** 5.2.2  
**Node:** 18+  
**Created:** February 2026
