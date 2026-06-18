---
name: nursy-medication-management
description: Build or modify Nursy AI medication management, dose, frequency, schedules, reminders, missed doses, dose events, medication status, or adherence scoring. Use when working on medication tracking.
---

# Nursy Medication Management

## Objective

Help users track medications, scheduled doses, missed doses, and adherence while keeping all essential behavior available offline. Medications have an active/inactive state, and dose events track individual taken/missed/skipped occurrences.

## Data Models

### MedicationEntity (Room entity, table `medications`)

```kotlin
@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val dose: String,
    val frequency: String,
    val scheduledTimesCsv: String,  // "08:00,20:00"
    val takenCount: Int = 0,
    val missedCount: Int = 0,
    val active: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: String = "queued"
)
```

### MedicationDoseEventEntity (Room entity, table `medication_dose_events`)

```kotlin
@Entity(tableName = "medication_dose_events")
data class MedicationDoseEventEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val medicationId: String,
    val scheduledTime: Long,    // epoch millis
    val takenAt: Long? = null,  // null = not taken yet
    val status: String = "missed",  // "taken" | "missed" | "skipped"
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: String = "queued"
)
```

### Shared TypeScript types

```typescript
export type Medication = { id, userId, name, dose, frequency, scheduledTimes: string[], takenCount, missedCount, active };
export type MedicationDoseEvent = { id, userId, medicationId, scheduledTime, takenAt?, status: DoseStatus };
export function calculateAdherenceScore(medications: Medication[]): number;
```

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/MedicationEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/MedicationDoseEventEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/dao/HealthDao.kt` — `observeActiveMedications()`, `observeAllMedications()`, `upsertMedication()`, `deactivateMedication()`, `observeDoseEventsForMedication()`, `getTakenDoseEvents()`, `getMissedDoseEvents()`, `markDoseAsTaken()`, `markDoseAsMissed()`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/MedicationManagementScreen.kt` — add medication form + list with schedule/deactivate actions
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/NursyViewModel.kt` — `addMedication()`, `deactivateMedication()`, `markDoseAsTaken()`, `scheduleDoseEventsForMedication()`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/notification/ReminderScheduler.kt` — `scheduleMedicationReminder()`, `cancelMedicationReminders()`
- Check Android notification and WorkManager dependencies in `apps/mobile/app/build.gradle.kts`

## Implementation Guidance

- **Dose events**: Tracked separately from medication definitions. The `scheduleDoseEventsForMedication()` method creates daily `MedicationDoseEventEntity` records for each scheduled time.
- **Adherence score**: The shared `calculateAdherenceScore()` uses `takenCount / (takenCount + missedCount)`. Deterministic and testable.
- **Deactivation**: `deactivateMedication()` sets `active = 0`. Inactive medications no longer appear in the active list and should not schedule future reminders.
- **Reminder integration**: `ReminderScheduler.scheduleMedicationReminder()` creates an Android alarm via `AlarmManager`. The alarm fires `MedicationReminderReceiver` which shows a notification via `NotificationHelper.showMedicationReminder()`.
- **Status tracking**: `markDoseAsTaken()` / `markDoseAsMissed()` update dose event status. The ViewModel also increments taken/missed counts on the parent medication.

## Expected Deliverables

- `MedicationEntity` and `MedicationDoseEventEntity` with full DAO.
- `MedicationManagementScreen.kt` — form to add, list to view, schedule + deactivate actions.
- `NursyViewModel` methods: `addMedication()`, `deactivateMedication()`, `markDoseAsTaken()`, `scheduleDoseEventsForMedication()`.
- `calculateAdherenceScore()` in shared models.
- `ReminderScheduler` integration for medication alarm reminders.

## Verify

- Save medication and dose events offline.
- Confirm adherence score changes after taken and missed events.
- Confirm inactive medications no longer appear in active list.
- Confirm `ReminderScheduler.scheduleMedicationReminder()` creates a `PendingIntent` with correct notification ID.
- Confirm `npm run typecheck:web` passes after shared model changes.
