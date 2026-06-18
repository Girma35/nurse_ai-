# Nursy AI Infrastructure

This folder contains the small AWS layer for the mobile app.

The cloud role is intentionally narrow:

- authenticate users
- back up and sync mobile records
- support recovery on a new device
- optionally generate premium weekly/monthly reports

The Android Room database remains the offline source of truth.

## Recommended AWS Shape

```text
Android App
  -> API Gateway HTTP API
  -> Lambda
  -> DynamoDB
  -> Optional Bedrock report job
```

Use Cognito for auth when wiring real accounts. Avoid EC2, GPU instances, Aurora/RDS, OpenSearch, NAT Gateway, and always-on services for the early mobile product.

## DynamoDB

`dynamodb/nursy-ai-table.json` defines a single-table DynamoDB design for mobile sync records.

Suggested key pattern:

- `PK`: `USER#<userId>`
- `SK`: `<ENTITY>#<timestamp>#<entityId>`
- `GSI1PK`: `<ENTITY>#<entityType>`
- `GSI1SK`: `<updatedAt>`

The mobile app should treat Room DB as the source of truth while offline. WorkManager sync jobs can upsert queued local records into DynamoDB when network access is available.

## Cost Guardrails

With USD $100 credits for 6 months, keep the expected mobile-only spend near $0-$5/month during development.

- Use DynamoDB on-demand for low traffic.
- Keep Lambda/API Gateway request volume small.
- Set CloudWatch log retention to a short window.
- Cache generated reports by user and date range.
- Call cloud AI only for explicit report generation.
