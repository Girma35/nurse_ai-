---
name: nursy-authentication-profile
description: Build or modify Nursy AI authentication, user profile, health profile, allergies, chronic conditions, blood type, emergency contacts, or profile sync behavior. Use when working on user identity and personal health context.
---

# Nursy Authentication Profile

## Objective

Implement identity and health profile data without blocking offline health tracking. Store the user's health context locally via `ProfileEntity` and `EmergencyContactEntity`, and sync it when possible via `RecordMapper.profileToPayload()` and `RecordMapper.emergencyContactToPayload()`.

## Data Model

### ProfileEntity (Room entity, table `profiles`)

```kotlin
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val fullName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val weightKg: Double? = null,
    val heightCm: Double? = null,
    val bloodType: String = "",
    val allergies: String = "",       // comma-separated
    val chronicConditions: String = "", // comma-separated
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: String = "queued"
)
```

### EmergencyContactEntity (Room entity, table `emergency_contacts`)

```kotlin
@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: String = "queued"
)
```

### Shared TypeScript types (`packages/shared/src/index.ts`)

```typescript
export type UserProfile = { id, userId, fullName, dateOfBirth, gender, weightKg, heightCm, bloodType, allergies: string[], chronicConditions: string[] };
export type EmergencyContact = { id, userId, name, relationship, phoneNumber };
export type EmergencyHealthCard = { userId, fullName, bloodType, allergies, conditions, emergencyContacts };
```

## Start Here

- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/ProfileEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/entity/EmergencyContactEntity.kt`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/data/local/dao/HealthDao.kt` — `observeProfile()`, `getProfile()`, `upsertProfile()`, `observeEmergencyContacts()`, `upsertEmergencyContact()`, `deleteEmergencyContact()`
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/ProfileScreen.kt` — form with all profile fields
- Inspect `apps/mobile/app/src/main/java/com/nursyai/ui/screens/EmergencyCardScreen.kt` — emergency card display + add contact form
- Inspect `apps/mobile/app/src/main/java/com/nursyai/sync/RecordMapper.kt` — `profileToPayload()` and `emergencyContactToPayload()`
- Inspect `infra/README.md` for DynamoDB sync key patterns
- Check `packages/shared/src/index.ts` before adding web or backend profile types

## Implementation Guidance

- **Offline-first**: Keep profile data available offline after first entry. All fields use local persistence as source of truth.
- **Allergies/conditions**: Stored as comma-separated strings in Room, typed as `string[]` in shared models. Use `split(", ")` to convert.
- **Separate credentials**: Authentication credentials are separate from health profile records. No auth system is wired yet — a `demoUserId = "demo-user"` is hardcoded in the ViewModel.
- **Sync metadata**: Every entity tracks `createdAt`, `updatedAt`, and `syncState`. The `RecordMapper` converts entities to `SyncPayload` with DynamoDB key pattern `PK=USER#<userId>`, `SK=<entityType>#<timestamp>#<entityId>`.
- **Emergency card**: Data is read from the same `ProfileEntity` and `EmergencyContactEntity` sources. The `EmergencyCardScreen` combines both.
- **No cloud auth gate**: Cloud auth is not required before saving local health profile fields.

## Expected Deliverables

- `ProfileEntity` with `HealthDao` methods (observe, get, upsert, pending profiles).
- `EmergencyContactEntity` with `HealthDao` methods (observe, get, upsert, delete, pending contacts).
- `ProfileScreen.kt` — Compose form for editing all profile fields.
- `RecordMapper.profileToPayload()` and `RecordMapper.emergencyContactToPayload()` for sync.
- Shared TypeScript `UserProfile`, `EmergencyContact`, `EmergencyHealthCard` types.

## Verify

- Confirm profile edits persist after app restart (Room storage).
- Confirm emergency card renders in airplane mode (local data only).
- Confirm shared TypeScript model changes match the mobile data shape.
- Confirm `RecordMapper` tests produce correct DynamoDB key patterns.
