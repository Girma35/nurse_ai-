// ─── Health Enums ───────────────────────────────────────────

export type MoodLevel = "low" | "down" | "steady" | "good" | "great";
export type SleepQuality = "poor" | "fair" | "good" | "excellent";
export type DoseStatus = "taken" | "missed" | "skipped";
export type SyncState = "local" | "queued" | "synced" | "conflict";
export type EntityType = "checkIn" | "symptom" | "medication" | "doseEvent" | "profile" | "emergencyContact" | "insight";

// ─── Core Health Records ────────────────────────────────────

export type DailyCheckIn = {
  id: string;
  userId: string;
  date: string;
  mood: MoodLevel;
  energyLevel: number;
  sleepHours: number;
  sleepQuality: SleepQuality;
  stressLevel: number;
  waterIntakeMl: number;
  notes?: string;
};

export type SymptomLog = {
  id: string;
  userId: string;
  name: string;
  severity: 1 | 2 | 3 | 4 | 5;
  startedAt: string;
  durationHours?: number;
  active?: boolean;
  notes?: string;
};

export type Medication = {
  id: string;
  userId: string;
  name: string;
  dose: string;
  frequency: string;
  scheduledTimes: string[];
  takenCount: number;
  missedCount: number;
  active: boolean;
};

export type MedicationDoseEvent = {
  id: string;
  userId: string;
  medicationId: string;
  scheduledTime: string;
  takenAt?: string;
  status: DoseStatus;
};

// ─── Profile & Emergency ────────────────────────────────────

export type UserProfile = {
  id: string;
  userId: string;
  fullName: string;
  dateOfBirth: string;
  gender: string;
  weightKg: number | null;
  heightCm: number | null;
  bloodType: string;
  allergies: string[];
  chronicConditions: string[];
};

export type EmergencyContact = {
  id: string;
  userId: string;
  name: string;
  relationship: string;
  phoneNumber: string;
};

export type EmergencyHealthCard = {
  userId: string;
  fullName: string;
  bloodType: string;
  allergies: string[];
  conditions: string[];
  emergencyContacts: EmergencyContact[];
};

// ─── Insights & Reports ─────────────────────────────────────

export type HealthInsight = {
  id: string;
  userId: string;
  title: string;
  message: string;
  severity: "info" | "warning" | "alert";
  sourceRecordIds: string[];
  createdAt: string;
};

export type WeeklyReport = {
  id: string;
  userId: string;
  weekStart: string;
  weekEnd: string;
  averageHealthScore: number;
  adherenceRate: number;
  symptomSummary: string;
  highlights: string[];
  recommendations: string[];
  generatedAt: string;
};

// ─── Sync ───────────────────────────────────────────────────

export type SyncRecord<T> = {
  id: string;
  userId: string;
  entityType: EntityType;
  payload: T;
  updatedAt: string;
  syncState: SyncState;
};

export type SyncSummary = {
  pendingCheckIns: number;
  pendingSymptoms: number;
  pendingMedications: number;
  lastSyncedAt: string | null;
};

// ─── Timeline ───────────────────────────────────────────────

export type TimelineEvent = {
  id: string;
  date: string;
  time: string;
  type: "checkin" | "symptom" | "medication" | "dose" | "insight" | "reminder";
  label: string;
  detail: string;
  sourceRecordId: string;
};

// ─── Health Helpers ─────────────────────────────────────────

export function calculateAdherenceScore(medications: Medication[]): number {
  const totalTaken = medications.reduce((sum, med) => sum + med.takenCount, 0);
  const totalMissed = medications.reduce((sum, med) => sum + med.missedCount, 0);
  const total = totalTaken + totalMissed;

  if (total === 0) return 100;

  return Math.round((totalTaken / total) * 100);
}

export function calculateHealthScore(
  checkIn: DailyCheckIn,
  symptoms: SymptomLog[],
  medications: Medication[]
): number {
  const sleepScore = Math.min(checkIn.sleepHours / 8, 1) * 20;
  const energyScore = (checkIn.energyLevel / 10) * 20;
  const stressScore = Math.max(0, (5 - checkIn.stressLevel) / 5) * 15;
  const hydrationScore = Math.min(checkIn.waterIntakeMl / 2000, 1) * 15;
  const symptomPenalty = symptoms.reduce((sum, s) => sum + s.severity * 3, 0);
  const adherenceScore = calculateAdherenceScore(medications) * 0.3;

  return Math.max(
    0,
    Math.min(
      100,
      Math.round(sleepScore + energyScore + stressScore + hydrationScore + adherenceScore - symptomPenalty)
    )
  );
}

export function formatSeverityLabel(severity: SymptomLog["severity"]): string {
  const labels: Record<SymptomLog["severity"], string> = {
    1: "Very mild",
    2: "Mild",
    3: "Moderate",
    4: "High",
    5: "Severe"
  };
  return labels[severity];
}

export function getHealthScoreLabel(score: number): string {
  if (score >= 80) return "Good";
  if (score >= 60) return "Fair";
  if (score >= 40) return "Concerning";
  return "Needs attention";
}

export function calculateHealthScoreFromFields(
  energyLevel: number,
  sleepHours: number,
  stressLevel: number,
  waterIntakeMl: number,
  symptomSeveritySum: number,
  adherenceRate: number
): number {
  const sleepScore = Math.min(sleepHours / 8, 1) * 20;
  const energyScore = (energyLevel / 10) * 20;
  const stressScore = Math.max(0, (5 - stressLevel) / 5) * 15;
  const hydrationScore = Math.min(waterIntakeMl / 2000, 1) * 15;
  const symptomPenalty = symptomSeveritySum * 3;
  const adherenceComponent = adherenceRate * 0.3;

  return Math.max(
    0,
    Math.min(
      100,
      Math.round(sleepScore + energyScore + stressScore + hydrationScore + adherenceComponent - symptomPenalty)
    )
  );
}

export function buildTimelineEvents(
  checkIns: DailyCheckIn[],
  symptoms: SymptomLog[],
  medications: Medication[],
  doseEvents: MedicationDoseEvent[]
): TimelineEvent[] {
  const events: TimelineEvent[] = [];

  for (const checkIn of checkIns) {
    events.push({
      id: `timeline-ci-${checkIn.id}`,
      date: checkIn.date,
      time: "12:00",
      type: "checkin",
      label: "Check-in recorded",
      detail: `Mood: ${checkIn.mood}, Energy: ${checkIn.energyLevel}/10, Sleep: ${checkIn.sleepHours}h`,
      sourceRecordId: checkIn.id
    });
  }

  for (const symptom of symptoms) {
    const date = new Date(symptom.startedAt).toISOString().slice(0, 10);
    const time = new Date(symptom.startedAt).toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit", hour12: false });
    events.push({
      id: `timeline-sym-${symptom.id}`,
      date,
      time,
      type: "symptom",
      label: `Symptom: ${symptom.name}`,
      detail: `Severity: ${formatSeverityLabel(symptom.severity)}${symptom.durationHours ? `, Duration: ${symptom.durationHours}h` : ""}`,
      sourceRecordId: symptom.id
    });
  }

  for (const dose of doseEvents) {
    const date = new Date(dose.scheduledTime).toISOString().slice(0, 10);
    const time = new Date(dose.scheduledTime).toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit", hour12: false });
    const med = medications.find(m => m.id === dose.medicationId);
    events.push({
      id: `timeline-dose-${dose.id}`,
      date,
      time,
      type: "dose",
      label: `Medication ${dose.status}`,
      detail: `${med?.name ?? "Unknown"} — ${dose.status === "taken" ? "Taken" : dose.status === "missed" ? "Missed" : "Skipped"}`,
      sourceRecordId: dose.id
    });
  }

  events.sort((a, b) => {
    const dateCompare = b.date.localeCompare(a.date);
    if (dateCompare !== 0) return dateCompare;
    return b.time.localeCompare(a.time);
  });

  return events;
}
