# Nursy AI

Nursy AI is a mobile-first, offline-first Android health companion.

```text
Mobile-first, offline-first, cloud-enhanced.
```

The phone is the primary system. The app must keep working without internet. AWS is used only for authentication, backup, sync, and recovery. Cloud AI is an optional premium enhancement for deeper weekly or monthly summaries, not a dependency for daily use.

## Product Principle

Nursy AI keeps the user's health companion usable without internet.

- Core app: works fully offline on the phone.
- AWS: handles auth, backup, sync, and recovery.
- Cloud AI: generates occasional premium reports from synced summaries.

This protects the product from high AWS bills, weak connectivity, slow AI responses, privacy risk, and cloud outages.

## Project Structure

```text
apps/
  mobile/        Android app in Kotlin, Jetpack Compose, Room, WorkManager
infra/
  dynamodb/      DynamoDB table definition for mobile sync records
skills/          Repo-local implementation guidance for mobile features
guide            Product and architecture guide
component        Mobile component and phase plan
manual.md        Build and verification checklist
```

## Android Stack

- Kotlin
- Jetpack Compose
- Room DB for offline storage
- WorkManager for background sync
- OkHttp / Retrofit for backend calls
- Local rules engine for deterministic on-device health insights

## AWS Stack

Keep AWS small and serverless:

- Amazon Cognito for auth
- API Gateway HTTP API for mobile endpoints
- Lambda for sync and report jobs
- DynamoDB for synced health records
- Optional Bedrock for weekly/monthly report generation

Avoid always-on servers, EC2 GPU instances, Aurora/RDS, OpenSearch, NAT Gateway, and self-hosted LLMs on AWS.

## Build Commands

From the mobile workspace:

```bash
cd apps/mobile
./gradlew build
./gradlew test
./gradlew assembleDebug
```

Build the debug APK:

```bash
cd apps/mobile
./gradlew assembleDebug
```

The APK is written to:

```text
apps/mobile/app/build/outputs/apk/debug/app-debug.apk
```

## Mobile Features

- Authentication and profile
- Daily health check-ins
- Symptom tracking
- Medication management
- Health dashboard
- Health timeline
- AI symptom journal
- Local health insights
- Emergency health card
- Notifications and reminders
- Offline Room storage
- WorkManager sync to AWS

## AI Strategy

Local AI handles everyday value:

- symptom parsing
- repeated symptom detection
- sleep and fatigue warnings
- hydration and stress insights
- medication adherence patterns
- offline dashboard insights

Cloud AI is reserved for occasional premium work:

- weekly health summaries
- monthly trend reports
- polished explanations from structured synced data

Do not send every journal note to cloud AI. Prefer sending compact structured summaries after local processing.

## Health Safety Wording

Nursy AI provides tracking insights, summaries, and patterns. It must not diagnose conditions, predict diseases, prescribe treatment, or replace clinician advice.

Safe product language:

- "This is a pattern worth reviewing with a doctor."
- "You might want to mention this at your next appointment."
- "Here is a summary of your symptoms."
- "No diagnosis is provided, only tracking insights."

Avoid wording such as "you may have disease X", "this is likely diabetes/depression/infection", treatment instructions, or emergency diagnosis claims.

## Architecture

```text
Android App
  -> Room DB
  -> LocalRulesEngine
  -> WorkManager Sync
  -> API Gateway + Lambda
  -> DynamoDB
  -> Optional Cloud AI Reports
  -> Synced report records back to mobile
```

Room remains the offline source of truth. DynamoDB is a cloud backup and recovery layer, not a replacement for local data.

## Cost Guardrails

With USD $100 in AWS credits for 6 months, target a mobile-only burn rate of $0-$5/month for early development.

Use budget alerts at:

- $5
- $15
- $30
- $60
- $90

Keep CloudWatch log retention short, cache generated reports, and only call cloud AI for explicit report generation.

## Security

Do not commit `.env`, Android `local.properties`, AWS credentials, signing keys, or generated build output. Treat health data as sensitive. Keep user-scoped keys and auth checks in every sync endpoint.
