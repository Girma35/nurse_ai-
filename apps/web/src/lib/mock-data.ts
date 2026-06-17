import type {
  DailyCheckIn,
  EmergencyHealthCard,
  Medication,
  SymptomLog
} from "@nursy/shared";

export const latestCheckIn: DailyCheckIn = {
  id: "checkin-2026-06-17",
  userId: "demo-user",
  date: "2026-06-17",
  mood: "steady",
  energyLevel: 7,
  sleepHours: 6.5,
  sleepQuality: "fair",
  stressLevel: 3,
  waterIntakeMl: 1450,
  notes: "Mild headache in the morning. Energy improved after lunch."
};

export const activeSymptoms: SymptomLog[] = [
  {
    id: "symptom-headache",
    userId: "demo-user",
    name: "Headache",
    severity: 2,
    startedAt: "2026-06-15T08:30:00Z",
    durationHours: 52,
    notes: "Light sensitivity, lower severity today."
  },
  {
    id: "symptom-fatigue",
    userId: "demo-user",
    name: "Fatigue",
    severity: 3,
    startedAt: "2026-06-16T17:00:00Z",
    durationHours: 19,
    notes: "Likely connected to reduced sleep."
  }
];

export const medications: Medication[] = [
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

export const emergencyCard: EmergencyHealthCard = {
  userId: "demo-user",
  fullName: "Amina Bekele",
  bloodType: "O+",
  allergies: ["Penicillin"],
  conditions: ["Anemia"],
  emergencyContacts: [
    {
      name: "Dawit Bekele",
      relationship: "Brother",
      phone: "+251 900 000 000"
    }
  ]
};

export const timeline = [
  {
    id: "timeline-1",
    time: "08:00",
    label: "Medication taken",
    detail: "Iron supplement, 65 mg",
    type: "medication"
  },
  {
    id: "timeline-2",
    time: "09:20",
    label: "Symptom logged",
    detail: "Headache severity decreased from 3 to 2",
    type: "symptom"
  },
  {
    id: "timeline-3",
    time: "12:45",
    label: "Check-in updated",
    detail: "Energy level moved to 7 after lunch",
    type: "checkin"
  },
  {
    id: "timeline-4",
    time: "20:00",
    label: "Medication due",
    detail: "Vitamin D reminder scheduled",
    type: "reminder"
  }
];
