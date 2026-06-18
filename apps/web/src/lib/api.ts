import type {
  DailyCheckIn,
  SymptomLog,
  Medication,
  UserProfile,
  HealthInsight,
  WeeklyReport,
  SyncSummary,
  MedicationDoseEvent
} from "@nursy/shared";
import {
  latestCheckIn as mockCheckIn,
  activeSymptoms as mockSymptoms,
  medications as mockMedications,
  emergencyCard,
  timeline
} from "./mock-data";

const API_BASE = process.env.NEXT_PUBLIC_API_URL || "https://api.nursyai.com/v1";
const DEMO_USER_ID = "demo-user";

// Simple fetch wrapper with timeout and error handling
async function fetchFromApi<T>(
  endpoint: string,
  options?: RequestInit
): Promise<T> {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), 10000);

  try {
    const response = await fetch(`${API_BASE}${endpoint}`, {
      ...options,
      signal: controller.signal,
      headers: {
        "Content-Type": "application/json",
        ...options?.headers
      }
    });

    if (!response.ok) {
      throw new Error(`API error: ${response.status} ${response.statusText}`);
    }

    return await response.json();
  } finally {
    clearTimeout(timeoutId);
  }
}

export type DashboardData = {
  checkIn: DailyCheckIn | null;
  symptoms: SymptomLog[];
  medications: Medication[];
  insights: HealthInsight[];
  syncSummary: SyncSummary;
};

export type DashboardState =
  | { status: "loading" }
  | { status: "error"; error: string }
  | { status: "success"; data: DashboardData };

/**
 * Fetch all dashboard data from the cloud API with mock fallback.
 */
export async function fetchDashboardData(): Promise<DashboardState> {
  try {
    const [checkIn, symptoms, medications, insights, syncSummary] =
      await Promise.all([
        fetchFromApi<DailyCheckIn | null>(
          `/sync/records/${DEMO_USER_ID}/latest-checkin`
        ).catch(() => null),
        fetchFromApi<SymptomLog[]>(
          `/sync/records/${DEMO_USER_ID}/symptoms`
        ).catch(() => [] as SymptomLog[]),
        fetchFromApi<Medication[]>(
          `/sync/records/${DEMO_USER_ID}/medications`
        ).catch(() => [] as Medication[]),
        fetchFromApi<HealthInsight[]>(
          `/insights/${DEMO_USER_ID}`
        ).catch(() => [] as HealthInsight[]),
        fetchFromApi<SyncSummary>(
          `/sync/summary/${DEMO_USER_ID}`
        ).catch(() => ({
          pendingCheckIns: 0,
          pendingSymptoms: 0,
          pendingMedications: 0,
          lastSyncedAt: null
        }) as SyncSummary)
      ]);

    return {
      status: "success",
      data: {
        checkIn: checkIn ?? mockCheckIn,
        symptoms: symptoms.length > 0 ? symptoms : mockSymptoms,
        medications: medications.length > 0 ? medications : mockMedications,
        insights: insights,
        syncSummary
      }
    };
  } catch (error) {
    // Fall back to mock data on error
    return {
      status: "success",
      data: {
        checkIn: mockCheckIn,
        symptoms: mockSymptoms,
        medications: mockMedications,
        insights: [],
        syncSummary: {
          pendingCheckIns: 0,
          pendingSymptoms: 0,
          pendingMedications: 0,
          lastSyncedAt: new Date().toISOString()
        }
      }
    };
  }
}

/**
 * Fetch weekly report from cloud AI.
 */
export async function fetchWeeklyReport(
  userId: string = DEMO_USER_ID
): Promise<WeeklyReport | null> {
  try {
    return await fetchFromApi<WeeklyReport>(`/reports/weekly/${userId}`);
  } catch {
    return null;
  }
}

/**
 * Fetch health insights from cloud AI.
 */
export async function fetchInsights(
  userId: string = DEMO_USER_ID
): Promise<HealthInsight[]> {
  try {
    return await fetchFromApi<HealthInsight[]>(`/insights/${userId}`);
  } catch {
    return [];
  }
}

export { DEMO_USER_ID };
