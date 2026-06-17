# Nursy AI Infrastructure

This folder contains backend infrastructure definitions for the shared cloud layer.

## DynamoDB

`dynamodb/nursy-ai-table.json` defines a single-table DynamoDB design for offline sync records.

Suggested key pattern:

- `PK`: `USER#<userId>`
- `SK`: `<ENTITY>#<timestamp>#<entityId>`
- `GSI1PK`: `<ENTITY>#<entityType>`
- `GSI1SK`: `<updatedAt>`

The mobile app should treat Room DB as the source of truth while offline. WorkManager sync jobs can upsert queued local records into DynamoDB when network access is available.
