import type {
  DailyCheckIn,
  SymptomLog,
  Medication,
  WeeklyReport,
  HealthInsight,
  MedicationDoseEvent
} from "@nursy/shared";
import { calculateHealthScore } from "@nursy/shared";

type ReportInput = {
  userId: string;
  weekStart: string;
  weekEnd: string;
  checkIns: DailyCheckIn[];
  symptoms: SymptomLog[];
  medications: Medication[];
  doseEvents: MedicationDoseEvent[];
};

/**
 * Generate a weekly health report from synced user data.
 * This runs in the cloud backend after sync is stable.
 */
export function generateWeeklyReport(input: ReportInput): WeeklyReport {
  const { userId, weekStart, weekEnd, checkIns, symptoms, medications } = input;

  // Calculate average health score for the week
  const scores = checkIns.map((ci) => calculateHealthScore(ci, symptoms, medications));
  const avgScore =
    scores.length > 0
      ? Math.round(scores.reduce((a, b) => a + b, 0) / scores.length)
      : 0;

  // Calculate adherence rate
  const totalTaken = medications.reduce((sum, m) => sum + m.takenCount, 0);
  const totalMissed = medications.reduce((sum, m) => sum + m.missedCount, 0);
  const totalDoses = totalTaken + totalMissed;
  const adherenceRate = totalDoses > 0 ? Math.round((totalTaken / totalDoses) * 100) : 100;

  // Symptom summary
  const symptomNames = symptoms.map((s) => s.name);
  const uniqueSymptoms = [...new Set(symptomNames)];
  const symptomSummary =
    uniqueSymptoms.length > 0
      ? `${uniqueSymptoms.join(", ")} (${symptoms.length} logs this week)`
      : "No symptoms reported this week";

  // Generate highlights
  const highlights: string[] = [];
  const completedCheckIns = checkIns.length;
  if (completedCheckIns >= 5) {
    highlights.push(`Completed ${completedCheckIns} daily check-ins this week`);
  } else if (completedCheckIns > 0) {
    highlights.push(`Completed ${completedCheckIns} daily check-ins`);
  }
  if (adherenceRate >= 80) {
    highlights.push(`Strong medication adherence at ${adherenceRate}%`);
  }
  if (uniqueSymptoms.length === 0) {
    highlights.push("No symptoms reported — a positive week");
  }

  // Generate recommendations
  const recommendations: string[] = [];
  if (completedCheckIns < 7) {
    recommendations.push("Try to complete a daily check-in every day for better trend data");
  }
  if (adherenceRate < 80) {
    recommendations.push("Consider setting medication reminders to improve adherence");
  }
  if (uniqueSymptoms.length > 2) {
    recommendations.push(
      `Multiple symptoms detected (${uniqueSymptoms.join(", ")}). Discuss patterns with your healthcare provider`
    );
  }
  if (avgScore < 60) {
    recommendations.push(
      "Your health score is below average this week — review sleep, hydration, and stress levels"
    );
  }
  if (recommendations.length === 0) {
    recommendations.push("Keep up the great work maintaining your health routine");
  }

  return {
    id: `weekly-${weekStart}`,
    userId,
    weekStart,
    weekEnd,
    averageHealthScore: avgScore,
    adherenceRate,
    symptomSummary,
    highlights,
    recommendations,
    generatedAt: new Date().toISOString()
  };
}

/**
 * Analyze symptom trends over a period.
 */
export function analyzeSymptomTrends(symptoms: SymptomLog[]): HealthInsight[] {
  const insights: HealthInsight[] = [];

  // Group by symptom name
  const grouped = new Map<string, SymptomLog[]>();
  for (const symptom of symptoms) {
    const key = symptom.name.toLowerCase();
    const existing = grouped.get(key) || [];
    existing.push(symptom);
    grouped.set(key, existing);
  }

  for (const [name, logs] of grouped) {
    if (logs.length >= 3) {
      const avgSeverity = Math.round(
        logs.reduce((sum, s) => sum + s.severity, 0) / logs.length
      );
      insights.push({
        id: `trend-${name}`,
        userId: symptoms[0]?.userId ?? "unknown",
        title: `${name} trend`,
        message: `${name} appeared ${logs.length} times with average severity ${avgSeverity}/5. ${logs.length >= 5 ? "Consider discussing with a healthcare provider." : "Monitor for changes."}`,
        severity: logs.length >= 5 ? "warning" : "info",
        sourceRecordIds: logs.map((s) => s.id),
        createdAt: new Date().toISOString()
      });
    }
  }

  // Medication effectiveness heuristic
  const improving = logsAreDecreasing(symptoms);
  if (improving) {
    insights.push({
      id: "trend-improving",
      userId: symptoms[0]?.userId ?? "unknown",
      title: "Improving trend",
      message: "Symptom severity is decreasing over time. Your current routine may be effective.",
      severity: "info",
      sourceRecordIds: symptoms.map((s) => s.id),
      createdAt: new Date().toISOString()
    });
  }

  return insights;
}

/**
 * Simple heuristic: check if symptom severity is decreasing over time.
 */
function logsAreDecreasing(symptoms: SymptomLog[]): boolean {
  if (symptoms.length < 2) return false;
  const sorted = [...symptoms].sort(
    (a, b) => new Date(a.startedAt).getTime() - new Date(b.startedAt).getTime()
  );
  const midPoint = Math.floor(sorted.length / 2);
  const firstHalf = sorted.slice(0, midPoint);
  const secondHalf = sorted.slice(midPoint);

  const firstAvg =
    firstHalf.reduce((sum, s) => sum + s.severity, 0) / firstHalf.length;
  const secondAvg =
    secondHalf.reduce((sum, s) => sum + s.severity, 0) / secondHalf.length;

  return secondAvg < firstAvg;
}
