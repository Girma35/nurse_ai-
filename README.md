# Nursy AI

Nursy AI is a mobile-first, offline-capable AI health companion designed to help users track daily health status, log symptoms and medications, detect health trends, and generate useful health insights and reports.

The product is built around real-world use in low-connectivity environments:

> Everything works offline first, then syncs to cloud intelligence.

## Product Strategy

### Primary Product: Mobile App

The main user-facing product is a Kotlin Android app built with Jetpack Compose. Users should be able to use the core experience every day, even without an internet connection.

### Secondary Product: Web App

The web app is a Next.js dashboard deployed on Vercel. It exists for demo access, judge evaluation, and data visualization. It is not intended to be the full product experience.

## Repository Layout

```text
apps/
  mobile/        Android app skeleton for the offline-first mobile product
  web/           Next.js dashboard for Vercel demos and visualization
packages/
  shared/        Shared TypeScript health models and scoring helpers
infra/
  dynamodb/      DynamoDB table definition for sync records
```

## Getting Started

### Prerequisites

- Node.js 22 or newer
- npm 10 or newer
- Android Studio for the mobile app
- Android SDK 35 for the current Android scaffold

### Install Dependencies

```bash
npm install
```

### Run the Web Dashboard

```bash
npm run dev:web
```

The dashboard runs from `apps/web` and uses mock health data until the backend API is connected.

### Build the Web Dashboard

```bash
npm run build:web
```

### Run the Android App

1. Open `apps/mobile` in Android Studio.
2. Let Android Studio sync the Gradle project.
3. Confirm the Android SDK path in `local.properties`.
4. Run the `app` configuration on an emulator or device.

The mobile scaffold includes Compose screens, Room entities, a DAO, a local rules engine starter, and a WorkManager sync worker placeholder.

## Deployment

The repository includes `vercel.json` at the root. Vercel should install dependencies with `npm install` and build the dashboard with:

```bash
npm run build:web
```

## Mobile App

### Tech Stack

- Kotlin
- Jetpack Compose
- Room DB for offline storage
- WorkManager for background sync
- Retrofit for API calls
- Local rules engine for lightweight on-device health logic

### Core Features

#### Authentication and Profile

- Email login and signup
- User profile
- Health profile including age, gender, weight, height, blood type, allergies, and chronic conditions

#### Daily Health Check-In

- Mood tracking with five levels
- Energy level from 1 to 10
- Sleep hours and sleep quality
- Stress level
- Water intake
- Free-text notes

#### Symptom Tracking

- Add symptoms such as headache, fever, or fatigue
- Track severity from 1 to 5
- Track symptom duration
- Add symptom notes
- View date-based symptom history

#### Medication Management

- Add medications
- Track dose and frequency
- Schedule reminders
- Track missed doses
- Calculate adherence score

#### Health Dashboard

- Health score from 0 to 100
- Today's summary
- Active symptoms
- Medication status
- Quick health insights

#### Health Timeline

- Chronological health history
- Symptom events
- Medication events
- Daily log visualization

#### AI Symptom Journal

Users can enter natural language health notes, such as:

```text
I feel tired and have headache for 3 days
```

The app extracts structured health data, such as fatigue and headache, and logs it into the system.

#### AI Health Insights

Local on-device logic handles basic insights:

- Repeated symptom detection
- Sleep versus fatigue warnings
- Medication reminders

Cloud AI handles advanced analysis:

- Symptom trend analysis
- Health deterioration detection
- Medication effectiveness analysis
- Weekly health report generation

#### Emergency Health Card

The emergency health card is available offline and includes:

- Name
- Blood type
- Allergies
- Emergency contacts
- Chronic conditions
- One-tap access

#### Notifications

- Medication reminders
- Check-in reminders
- Appointment alerts
- Hydration reminders

#### Offline Mode

The mobile app is fully functional without internet access. User data is stored locally in Room DB and synced automatically when connectivity is available.

#### Sync Engine

- Syncs local Room DB data to cloud DynamoDB
- Runs background sync with WorkManager
- Supports conflict-safe updates

## Web App

### Tech Stack

- Next.js
- React
- TailwindCSS
- Vercel Hosting
- AWS SDK for DynamoDB access

### Purpose

The web app provides a live dashboard connected to the same backend as the mobile app. It is used for demo access, judge evaluation, and health data visualization.

## Architecture Summary

```text
Android App
  -> Room DB
  -> WorkManager Sync
  -> Cloud Backend
  -> DynamoDB
  -> Next.js Dashboard on Vercel
```

## Guiding Principle

Nursy AI should remain useful without connectivity, then become more intelligent when cloud services are available.
