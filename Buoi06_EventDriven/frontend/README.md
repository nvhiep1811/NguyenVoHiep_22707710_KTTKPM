# Movie Ticket Frontend

This is a minimal React + Vite frontend for the Event-Driven Movie Ticket system.

What I scaffolded:
- React + Vite app
- Pages: Home (list movies), Movie (details + book), Register
- `src/api.js` connects to backend endpoints via `VITE_API_BASE`
- `Dockerfile` to build a static site served by nginx
- `docker-compose.yml` updated to include `frontend` service (exposes port 3000)

How to run locally (dev):

1. cd frontend
2. npm install
3. VITE_API_BASE=http://localhost:8080 npm run dev

How to build/run with Docker Compose:

1. docker compose up --build
2. Open http://localhost:3000

Notes about requirements and next steps:
- I scanned the repo and the provided schema docx; I could not reliably extract readable text from the PDF/Docx binary inside the workspace here. Please confirm any specific UI/UX requirements from the PDF (e.g., responsive grid, exact fields, required views, or final acceptance criteria).
- From the services, the frontend integrates with these endpoints:
  - `GET /api/movies` and `GET /api/movies/{id}`
  - `POST /api/bookings` (payload: `userId, movieId, showTimeId, movieTitle, seats, totalPrice`)
  - `POST /api/users/register`
  - `POST /api/payments/simulate` (used to simulate payment after booking)
  - `GET /api/notifications/users/{userId}` (future)

Suggested final requirement items to confirm from the PDF:
- Required pages (dashboard, admin, analytics?)
- Exact registration/login fields and auth flow
- Styling requirements or component library preference
- Accessibility / responsive breakpoints
- Any required integration tests or E2E flows

If you confirm, I'll implement the remaining pages and polish styles, add authentication, and wire notifications. If you want, I can also extract specific pages or text from the attached PDF/docx if you want to upload the extracted text or allow me to run a local extraction command.
