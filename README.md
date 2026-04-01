# OEMS Project

Online Examination Management System (OEMS) built with:
- Backend: Spring Boot + MongoDB
- Frontend: React + Vite + Tailwind CSS

## Project Structure

- `backend/`: Spring Boot REST API
- `frontend/`: React client app
- `run-oems.ps1`: Starts backend and frontend in separate terminals
- `stop-oems.ps1`: Stops backend/frontend processes
- `ARCHITECTURE.md`: High-level architecture notes

## Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 18+ and npm
- MongoDB running locally on `mongodb://localhost:27017`

## Quick Start (Windows PowerShell)

From the `project` folder:

```powershell
./run-oems.ps1
```

To stop running services:

```powershell
./stop-oems.ps1
```

## Manual Start

### 1) Start Backend

```powershell
cd backend
mvn spring-boot:run
```

Backend default URL:
- `http://localhost:8085`

### 2) Start Frontend

```powershell
cd frontend
npm install
npm run dev
```

Frontend default URL:
- `http://localhost:5173`

## Build

Backend:

```powershell
cd backend
mvn clean package
```

Frontend:

```powershell
cd frontend
npm run build
```

## Notes

- `backend/src/main/resources/application.yml` sets backend port to `8085`.
- `run-oems.ps1` and `stop-oems.ps1` currently reference port `8081` for backend process management. If needed, update those scripts to `8085` for full alignment.
- If Maven shows certificate warnings (`PKIX path building failed`), this is usually a Java trust/certificate issue, not an app-code error.
