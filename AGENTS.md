# Repository Guidelines

## Project Structure & Module Organization

Nursy AI is a mobile-first monorepo. `apps/mobile` contains the Android app in Kotlin with Jetpack Compose, Room entities/DAOs under `app/src/main/java/com/nursyai/data/local`, local rules in `ai`, and sync work in `sync`. `apps/web` contains the Next.js dashboard, with app routes in `src/app`, reusable UI in `src/components`, and demo data in `src/lib`. `packages/shared` holds shared TypeScript health models and helpers. `infra/dynamodb` contains the DynamoDB table definition. Product planning lives in `guide`, `component`, and `skills/`.

## Build, Test, and Development Commands

- `npm install`: install root workspace dependencies.
- `npm run dev:web`: start the Next.js dashboard from `apps/web`.
- `npm run build:web`: build the Vercel dashboard.
- `npm run lint:web`: run Next.js linting for the web app.
- `npm run typecheck:web`: run TypeScript checks for the web app.
- `npm run typecheck --workspace @nursy/shared`: type-check shared models.
- `cd apps/mobile && ./gradlew build`: build Android when the SDK is configured.

## Coding Style & Naming Conventions

Use TypeScript for web/shared code and Kotlin for Android code. Keep React components in PascalCase, helpers and variables in camelCase, and shared model types in PascalCase. Kotlin classes and Compose functions should use PascalCase; functions and properties should use camelCase. Prefer small, feature-focused files and follow existing package boundaries. Use TailwindCSS utility classes in the web app.

## Testing Guidelines

There are no dedicated test suites yet. For current changes, run type checks and builds for touched workspaces. Add focused tests when introducing scoring helpers, Room behavior, parser rules, sync mapping, or dashboard data transforms. Name tests after the behavior under test, for example `LocalRulesEngineTest` or `health-score.test.ts`.

## Commit & Pull Request Guidelines

Recent history uses short Conventional Commit-style messages such as `feat: initialize Gradle wrapper` and `docs: add component specification`. Prefer `feat:`, `fix:`, `docs:`, `chore:`, or `test:`. Pull requests should include a clear summary, verification commands run, linked issues when relevant, and screenshots for visible web or mobile UI changes.

## Security & Configuration Tips

Do not commit `.env`, Android `local.properties`, credentials, or generated build output. Keep Room as the offline source of truth and treat DynamoDB sync as a cloud persistence layer, not a replacement for local data.
