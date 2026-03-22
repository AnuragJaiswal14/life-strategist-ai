# AI Life Strategist & Life Audit

A comprehensive mobile app and backend stack to analyze your habits, goals, routines, and decisions, providing weekly strategic life direction driven by AI.

## Project Structure

This repository is split into two primary components:
1. **`android/`**: The modern Jetpack Compose + MVVM + Clean Architecture Android App.
2. **`backend/`**: The NestJS + Prisma backend serving the REST API and orchestrating the AI Prompts to OpenAI.

## Tech Stack Overview

### Backend
- **Framework**: NestJS
- **Database**: PostgreSQL (Prisma ORM)
- **AI**: OpenAI GPT-4o integration
- **DevOps**: Docker & Docker Compose setup, GitHub actions CI/CD

### Android
- **UI**: Jetpack Compose + Material 3 + Vico Charts
- **Architecture**: MVVM + Clean Architecture
- **DI**: Dagger Hilt
- **Network & Local DB**: Retrofit, OkHttp, Room

## Setup & Running

### Backend
1. Navigate to `backend/nest-backend`.
2. Ensure you have Docker installed and running.
3. Replace `mock-key` in `ai.service.ts` or set the `OPENAI_API_KEY` in `.env`.
4. Run:
```bash
docker compose up -d
npm install
npx prisma db push
npx prisma generate
npm run start:dev
```

### Android Application
1. Open the `android/` directory in Android Studio.
2. Sync Project with Gradle Files.
3. By default, the app is configured to connect to your localhost backend if running on an emulator (`http://10.0.2.2:3000/`).
4. Click **Run** on a connected device or emulator.

## Design Highlights
- **Clean Architecture**: Decoupled Data layer (Retrofit/Room APIs), Domain layer (Models and Use Cases), and Presentation layer (ViewModels and Jetpack Compose).
- **AI Insights**: Weekly reports pull directly from your submitted habit data over the past 7 days, passing it through an LLM to derive a structured, JSON-formatted action plan avoiding "fluff".
- **Dynamic Charting**: Vico is utilized for creating fast, reactive energy-level charts in Jetpack Compose based on the database flow.
