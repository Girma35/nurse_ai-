# Repository Guidelines

## Project Structure & Module Organization

Nursy AI is a mobile-only Android project. `apps/mobile` contains the Kotlin app with Jetpack Compose UI, Room entities/DAOs under `app/src/main/java/com/nursyai/data/local`, local rules in `ai`, notifications in `notification`, and WorkManager sync code in `sync`. `infra/dynamodb` contains the DynamoDB table definition used by the mobile sync backend. Product planning lives in `guide`, `component`, `manual.md`, and `skills/`.

## Product Direction

The product principle is mobile-first, offline-first, cloud-enhanced. The Android app must work fully offline. AWS is only for auth, backup, sync, and recovery. Cloud AI is optional and should be used for occasional weekly/monthly report generation, not daily app logic.

## Health Safety Wording

Nursy AI is a health tracking companion, not a diagnostic or treatment product. All app copy, local insight text, cloud report prompts, notifications, and generated summaries must avoid diagnosis, disease prediction, prescribing treatment, or replacing clinician advice.

Use safe phrasing such as:

- "This is a pattern worth reviewing with a doctor."
- "You might want to mention this at your next appointment."
- "Here is a summary of your symptoms."
- "No diagnosis is provided, only tracking insights."

Do not write phrases such as "you may have disease X", "this is likely diabetes/depression/infection", treatment instructions, or emergency diagnosis claims. If symptoms look serious, phrase the app output as a tracking summary and suggest reviewing it with a qualified professional or using local emergency services when the user already believes it is urgent.

## Build, Test, and Development Commands

- `cd apps/mobile && ./gradlew build`: build the Android app.
- `cd apps/mobile && ./gradlew test`: run JVM unit tests.
- `cd apps/mobile && ./gradlew assembleDebug`: build a debug APK.
- `cd apps/mobile && ./gradlew compileDebugKotlin`: run a faster Kotlin compile check.

## Coding Style & Naming Conventions

Use Kotlin for app code. Kotlin classes and Compose functions should use PascalCase; functions and properties should use camelCase. Keep files focused by feature and follow existing package boundaries. Prefer deterministic local rules for core health logic before adding cloud AI behavior.

## Testing Guidelines

Run Gradle tests/builds for touched Android work. Add focused tests when changing Room behavior, parser rules, local health scoring, sync mapping, notification scheduling, or cloud report data contracts. Name tests after the behavior under test, for example `LocalRulesEngineTest`.

## Commit & Pull Request Guidelines

Recent history uses short Conventional Commit-style messages such as `feat: initialize Gradle wrapper` and `docs: add component specification`. Prefer `feat:`, `fix:`, `docs:`, `chore:`, or `test:`. Pull requests should include a clear summary, verification commands run, linked issues when relevant, and screenshots for visible mobile UI changes.

## Security & Configuration Tips

Do not commit `.env`, Android `local.properties`, credentials, signing keys, or generated build output. Keep Room as the offline source of truth and treat DynamoDB sync as a cloud backup/recovery layer, not a replacement for local data.
