import type {
  DailyCheckIn,
  SymptomLog,
  Medication,
  EmergencyHealthCard,
  EmergencyContact,
  UserProfile,
  HealthInsight,
  WeeklyReport,
  MedicationDoseEvent
} from "@nursy/shared";

// ─── Weekly check-ins (last 7 days) ──────────────────────────

export const weeklyCheckIns: DailyCheckIn[] = [
  {
    id: "ci-mon",
    userId: "demo-user",
    date: "2026-06-15",
    mood: "down",
    energyLevel: 4,
    sleepHours: 5.5,
    sleepQuality: "poor",
    stressLevel: 7,
    waterIntakeMl: 900,
    notes: "Rough start to the week. Head pressure and low energy."
  },
  {
    id: "ci-tue",
    userId: "demo-user",
    date: "2026-06-16",
    mood: "steady",
    energyLevel: 6,
    sleepHours: 6.0,
    sleepQuality: "fair",
    stressLevel: 5,
    waterIntakeMl: 1200,
    notes: "Slightly better. Fatigue still present."
  },
  {
    id: "ci-wed",
    userId: "demo-user",
    date: "2026-06-17",
    mood: "steady",
    energyLevel: 7,
    sleepHours: 6.5,
    sleepQuality: "fair",
    stressLevel: 3,
    waterIntakeMl: 1450,
    notes: "Mild headache in the morning. Improved after lunch."
  },
  {
    id: "ci-thu",
    userId: "demo-user",
    date: "2026-06-18",
    mood: "good",
    energyLevel: 7,
    sleepHours: 7.0,
    sleepQuality: "good",
    stressLevel: 3,
    waterIntakeMl: 1800,
    notes: "Feeling better. Headache intensity decreased."
  },
  {
    id: "ci-fri",
    userId: "demo-user",
    date: "2026-06-19",
    mood: "good",
    energyLevel: 8,
    sleepHours: 7.5,
    sleepQuality: "good",
    stressLevel: 3,
    waterIntakeMl: 2000,
    notes: "Good day overall. Energy levels improving."
  }
];

// ─── Symptoms ────────────────────────────────────────────────

export const allSymptoms: SymptomLog[] = [
  {
    id: "sym-headache-1",
    userId: "demo-user",
    name: "Headache",
    severity: 3,
    startedAt: "2026-06-15T08:00:00Z",
    durationHours: 48,
    active: false,
    notes: "Throbbing pain, light sensitivity."
  },
  {
    id: "sym-headache-2",
    userId: "demo-user",
    name: "Headache",
    severity: 3,
    startedAt: "2026-06-16T07:30:00Z",
    durationHours: 36,
    active: false,
    notes: "Continued headache. Responds to rest."
  },
  {
    id: "sym-headache-3",
    userId: "demo-user",
    name: "Headache",
    severity: 2,
    startedAt: "2026-06-17T08:30:00Z",
    durationHours: 52,
    active: true,
    notes: "Light sensitivity, lower severity today."
  },
  {
    id: "sym-fatigue-1",
    userId: "demo-user",
    name: "Fatigue",
    severity: 3,
    startedAt: "2026-06-15T17:00:00Z",
    durationHours: 72,
    active: true,
    notes: "Persistent low energy. Connected to sleep quality."
  },
  {
    id: "sym-cough",
    userId: "demo-user",
    name: "Cough",
    severity: 2,
    startedAt: "2026-06-16T10:00:00Z",
    durationHours: 48,
    active: true,
    notes: "Dry cough, occasional."
  }
];

// ─── Medications with dose history ───────────────────────────

export const allMedications: Medication[] = [
  {
    id: "med-iron",
    userId: "demo-user",
    name: "Iron supplement",
    dose: "65 mg",
    frequency: "Daily",
    scheduledTimes: ["08:00"],
    takenCount: 6,
    missedCount: 1,
    active: true
  },
  {
    id: "med-vitamin-d",
    userId: "demo-user",
    name: "Vitamin D",
    dose: "1000 IU",
    frequency: "Daily",
    scheduledTimes: ["20:00"],
    takenCount: 7,
    missedCount: 0,
    active: true
  }
];

// ─── Dose Events ─────────────────────────────────────────────

export const doseEvents: MedicationDoseEvent[] = [
  {
    id: "dose-iron-mon",
    userId: "demo-user",
    medicationId: "med-iron",
    scheduledTime: "2026-06-15T08:00:00Z",
    takenAt: "2026-06-15T08:05:00Z",
    status: "taken"
  },
  {
    id: "dose-iron-tue",
    userId: "demo-user",
    medicationId: "med-iron",
    scheduledTime: "2026-06-16T08:00:00Z",
    status: "missed"
  },
  {
    id: "dose-iron-wed",
    userId: "demo-user",
    medicationId: "med-iron",
    scheduledTime: "2026-06-17T08:00:00Z",
    takenAt: "2026-06-17T08:10:00Z",
    status: "taken"
  },
  {
    id: "dose-iron-thu",
    userId: "demo-user",
    medicationId: "med-iron",
    scheduledTime: "2026-06-18T08:00:00Z",
    takenAt: "2026-06-18T07:55:00Z",
    status: "taken"
  },
  {
    id: "dose-vitd-mon",
    userId: "demo-user",
    medicationId: "med-vitamin-d",
    scheduledTime: "2026-06-15T20:00:00Z",
    takenAt: "2026-06-15T20:30:00Z",
    status: "taken"
  },
  {
    id: "dose-vitd-tue",
    userId: "demo-user",
    medicationId: "med-vitamin-d",
    scheduledTime: "2026-06-16T20:00:00Z",
    takenAt: "2026-06-16T21:00:00Z",
    status: "taken"
  },
  {
    id: "dose-vitd-wed",
    userId: "demo-user",
    medicationId: "med-vitamin-d",
    scheduledTime: "2026-06-17T20:00:00Z",
    takenAt: "2026-06-17T20:15:00Z",
    status: "taken"
  },
  {
    id: "dose-vitd-thu",
    userId: "demo-user",
    medicationId: "med-vitamin-d",
    scheduledTime: "2026-06-18T20:00:00Z",
    takenAt: "2026-06-18T20:05:00Z",
    status: "taken"
  }
];

// ─── Profile ─────────────────────────────────────────────────

export const demoProfile: UserProfile = {
  id: "profile-1",
  userId: "demo-user",
  fullName: "Amina Bekele",
  dateOfBirth: "1992-04-15",
  gender: "Female",
  weightKg: 63,
  heightCm: 168,
  bloodType: "O+",
  allergies: ["Penicillin", "Sulfa drugs"],
  chronicConditions: ["Iron deficiency anemia"]
};

export const demoEmergencyContacts: EmergencyContact[] = [
  {
    id: "ec-1",
    userId: "demo-user",
    name: "Dawit Bekele",
    relationship: "Brother",
    phoneNumber: "+251 900 000 000"
  },
  {
    id: "ec-2",
    userId: "demo-user",
    name: "Dr. Sarah",
    relationship: "Primary Care",
    phoneNumber: "+251 911 000 000"
  }
];

export const emergencyCard: EmergencyHealthCard = {
  userId: "demo-user",
  fullName: "Amina Bekele",
  bloodType: "O+",
  allergies: demoProfile.allergies,
  conditions: demoProfile.chronicConditions,
  emergencyContacts: demoEmergencyContacts
};

// ─── Insights from LocalRulesEngine logic ────────────────────

export const demoInsights: HealthInsight[] = [
  {
    id: "insight-low-sleep",
    userId: "demo-user",
    title: "Low sleep detected",
    message: "Average sleep of 6.3h/night this week is below the recommended 7-9 hours. Fatigue may be connected.",
    severity: "warning",
    sourceRecordIds: weeklyCheckIns.slice(0, 3).map((ci) => ci.id),
    createdAt: new Date().toISOString()
  },
  {
    id: "insight-headache-repeat",
    userId: "demo-user",
    title: "Repeated headache logs",
    message: "Headache logged 3 times this week. Watch the trend and discuss with a provider if it continues.",
    severity: "warning",
    sourceRecordIds: allSymptoms.filter((s) => s.name === "Headache").map((s) => s.id),
    createdAt: new Date().toISOString()
  },
  {
    id: "insight-improving",
    userId: "demo-user",
    title: "Energy improving",
    message: "Energy levels have trended from 4/10 to 8/10 over the week. Your current routine may be helping.",
    severity: "info",
    sourceRecordIds: weeklyCheckIns.map((ci) => ci.id),
    createdAt: new Date().toISOString()
  }
];

// ─── Weekly Report ───────────────────────────────────────────

export const demoWeeklyReport: WeeklyReport = {
  id: "weekly-2026-06-15",
  userId: "demo-user",
  weekStart: "2026-06-15",
  weekEnd: "2026-06-21",
  averageHealthScore: 72,
  adherenceRate: 93,
  symptomSummary: "Headache (3 logs), Fatigue (1 log), Cough (1 log)",
  highlights: [
    "Completed 5 daily check-ins this week",
    "Strong medication adherence at 93%",
    "Energy levels improved from 4 to 8/10"
  ],
  recommendations: [
    "Consider earlier bedtime to address low sleep pattern",
    "Headache frequency warrants monitoring — keep tracking triggers",
    "Great medication adherence — stay consistent"
  ],
  generatedAt: new Date().toISOString()
};
