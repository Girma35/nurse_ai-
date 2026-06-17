export type MoodLevel = "low" | "down" | "steady" | "good" | "great";

export type SleepQuality = "poor" | "fair" | "good" | "excellent";

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

export type EmergencyContact = {
  name: string;
  relationship: string;
  phone: string;
};

export type EmergencyHealthCard = {
  userId: string;
  fullName: string;
  bloodType: string;
  allergies: string[];
  conditions: string[];
  emergencyContacts: EmergencyContact[];
};

export type SyncRecord<T> = {
  id: string;
  userId: string;
  entityType: "checkIn" | "symptom" | "medication" | "profile" | "emergencyCard";
  payload: T;
  updatedAt: string;
  syncState: "local" | "queued" | "synced" | "conflict";
};

export function calculateAdherenceScore(medications: Medication[]): number {
  const totalTaken = medications.reduce((sum, medication) => sum + medication.takenCount, 0);
  const totalMissed = medications.reduce((sum, medication) => sum + medication.missedCount, 0);
  const total = totalTaken + totalMissed;

  if (total === 0) {
    return 100;
  }

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
  const symptomPenalty = symptoms.reduce((sum, symptom) => sum + symptom.severity * 3, 0);
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
