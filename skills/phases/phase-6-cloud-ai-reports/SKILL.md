---
name: nursy-phase-6-cloud-ai-reports
description: Execute optional Nursy AI cloud AI report work for the mobile-only product, including weekly/monthly summaries, trend reports, compact structured prompts, cached report records, DynamoDB storage, and mobile display of synced report insights.
---

# Nursy Phase 6 — Optional Cloud AI Reports

## Objective

Add premium cloud intelligence only after offline data capture and sync are stable. Cloud AI should generate occasional reports from synced structured data. It must not become the core daily app engine.

## Architecture

```text
Room local records
  -> WorkManager sync
  -> DynamoDB user records
  -> Lambda report job
  -> Optional Bedrock call
  -> report/insight sync record
  -> mobile displays cached result
```

## Report Inputs

Use compact structured summaries instead of raw journals:

- date range
- check-in count
- average sleep, energy, stress, hydration
- symptom names, counts, severity trend
- medication adherence percentage
- source record ids

## Report Outputs

Store generated reports as user-scoped records with:

- `id`
- `userId`
- `periodStart`
- `periodEnd`
- `reportType`: `weekly` or `monthly`
- `summary`
- `highlights`
- `reviewNotes`
- `sourceRecordIds`
- `generatedAt`
- `modelProvider` and `modelName` when cloud AI is used

## Guardrails

- Mobile must work without reports.
- Do not diagnose, predict disease, prescribe treatment, or replace doctor advice.
- Use safe language: "This is a pattern worth reviewing with a doctor", "You might want to mention this at your next appointment", "Here is a summary of your symptoms", and "No diagnosis is provided, only tracking insights".
- Do not write "you may have disease X", "this is likely diabetes/depression/infection", prescribing instructions, or emergency diagnosis claims.
- Cache reports by `userId + reportType + periodStart + dataHash`.
- Never send sensitive free-text notes by default.
- Keep prompts short and deterministic.
- Cloud failures should leave previous reports available.

## Key Files

- `apps/mobile/app/src/main/java/com/nursyai/sync/SyncApiClient.kt`
- `apps/mobile/app/src/main/java/com/nursyai/sync/RecordMapper.kt`
- `apps/mobile/app/src/main/java/com/nursyai/ai/LocalRulesEngine.kt`
- `infra/dynamodb/nursy-ai-table.json`
- `infra/README.md`

## Exit Criteria

- Report records are user-scoped in DynamoDB.
- Report generation is explicit or scheduled, not tied to every app load.
- Mobile can fetch and display cached reports when online.
- Offline screens remain useful when reports are missing.

## Verify

- Confirm report generation uses user-scoped source records only.
- Confirm repeated requests return cached reports for unchanged data.
- Confirm cloud AI failures do not break local tracking.
- Confirm generated text stays in the health tracking category and avoids diagnosis, disease prediction, and treatment language.
