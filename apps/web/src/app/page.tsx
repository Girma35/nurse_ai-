import {
  Activity,
  Bell,
  CalendarDays,
  Droplets,
  FileText,
  HeartPulse,
  Pill,
  Shield,
  Smartphone,
  Stethoscope
} from "lucide-react";
import {
  calculateAdherenceScore,
  calculateHealthScore,
  formatSeverityLabel
} from "@nursy/shared";
import { HealthScore } from "@/components/HealthScore";
import { SyncStatus } from "@/components/SyncStatus";
import {
  activeSymptoms,
  emergencyCard,
  latestCheckIn,
  medications,
  timeline
} from "@/lib/mock-data";

const healthScore = calculateHealthScore(latestCheckIn, activeSymptoms, medications);
const adherenceScore = calculateAdherenceScore(medications);

export default function DashboardPage() {
  return (
    <main className="min-h-screen px-4 py-5 sm:px-6 lg:px-8">
      <div className="mx-auto flex max-w-7xl flex-col gap-6">
        <header className="flex flex-col gap-4 border-b border-moss/10 pb-5 md:flex-row md:items-center md:justify-between">
          <div>
            <div className="flex items-center gap-3">
              <span className="grid size-11 place-items-center rounded-lg bg-moss text-white">
                <HeartPulse aria-hidden="true" size={24} />
              </span>
              <div>
                <p className="text-sm font-medium uppercase tracking-[0.14em] text-moss">
                  Nursy AI
                </p>
                <h1 className="text-2xl font-semibold text-ink sm:text-3xl">
                  Health dashboard
                </h1>
              </div>
            </div>
          </div>
          <div className="flex flex-wrap items-center gap-2">
            <button className="focus-ring inline-flex h-10 items-center gap-2 rounded-md border border-moss/15 bg-white px-3 text-sm font-medium text-ink shadow-sm">
              <Bell aria-hidden="true" size={16} />
              Reminders
            </button>
            <button className="focus-ring inline-flex h-10 items-center gap-2 rounded-md bg-moss px-3 text-sm font-medium text-white shadow-sm">
              <FileText aria-hidden="true" size={16} />
              Weekly report
            </button>
          </div>
        </header>

        <section className="grid gap-4 lg:grid-cols-[1.25fr_0.75fr]">
          <div className="rounded-lg border border-moss/10 bg-white p-5 shadow-soft">
            <HealthScore score={healthScore} />
          </div>

          <div className="grid gap-4 sm:grid-cols-3 lg:grid-cols-1">
            <MetricCard
              icon={Droplets}
              label="Water"
              value={`${latestCheckIn.waterIntakeMl} ml`}
              detail="Goal: 2000 ml"
            />
            <MetricCard
              icon={Pill}
              label="Adherence"
              value={`${adherenceScore}%`}
              detail="Last 7 doses"
            />
            <MetricCard
              icon={CalendarDays}
              label="Sleep"
              value={`${latestCheckIn.sleepHours} h`}
              detail={`${latestCheckIn.sleepQuality} quality`}
            />
          </div>
        </section>

        <section className="grid gap-4 xl:grid-cols-[0.9fr_1.1fr]">
          <div className="rounded-lg border border-moss/10 bg-white p-5 shadow-sm">
            <div className="mb-4 flex items-center justify-between gap-3">
              <div>
                <p className="text-sm font-medium uppercase tracking-[0.14em] text-moss">
                  Active symptoms
                </p>
                <h2 className="mt-1 text-xl font-semibold text-ink">Today</h2>
              </div>
              <Stethoscope className="text-moss" aria-hidden="true" size={24} />
            </div>
            <div className="grid gap-3">
              {activeSymptoms.map((symptom) => (
                <article
                  className="rounded-lg border border-moss/10 bg-cloud/55 p-4"
                  key={symptom.id}
                >
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <h3 className="font-semibold text-ink">{symptom.name}</h3>
                      <p className="mt-1 text-sm leading-6 text-ink/70">
                        {symptom.notes}
                      </p>
                    </div>
                    <span className="rounded-md bg-white px-2.5 py-1 text-sm font-medium text-moss">
                      {formatSeverityLabel(symptom.severity)}
                    </span>
                  </div>
                </article>
              ))}
            </div>
          </div>

          <div className="rounded-lg border border-moss/10 bg-white p-5 shadow-sm">
            <div className="mb-4 flex items-center justify-between gap-3">
              <div>
                <p className="text-sm font-medium uppercase tracking-[0.14em] text-moss">
                  Timeline
                </p>
                <h2 className="mt-1 text-xl font-semibold text-ink">Daily health flow</h2>
              </div>
              <Activity className="text-coral" aria-hidden="true" size={24} />
            </div>
            <div className="space-y-3">
              {timeline.map((event) => (
                <article className="grid grid-cols-[64px_1fr] gap-3" key={event.id}>
                  <span className="pt-1 text-sm font-medium text-ink/55">
                    {event.time}
                  </span>
                  <div className="rounded-lg border border-moss/10 bg-white p-3 shadow-sm">
                    <h3 className="font-medium text-ink">{event.label}</h3>
                    <p className="mt-1 text-sm leading-6 text-ink/65">{event.detail}</p>
                  </div>
                </article>
              ))}
            </div>
          </div>
        </section>

        <section className="grid gap-4 xl:grid-cols-[1.1fr_0.9fr]">
          <div>
            <div className="mb-3 flex items-center gap-2">
              <Smartphone className="text-moss" aria-hidden="true" size={20} />
              <h2 className="text-xl font-semibold text-ink">Offline sync</h2>
            </div>
            <SyncStatus />
          </div>

          <div className="rounded-lg border border-coral/20 bg-white p-5 shadow-sm">
            <div className="mb-4 flex items-center justify-between gap-3">
              <div>
                <p className="text-sm font-medium uppercase tracking-[0.14em] text-coral">
                  Emergency card
                </p>
                <h2 className="mt-1 text-xl font-semibold text-ink">
                  {emergencyCard.fullName}
                </h2>
              </div>
              <Shield className="text-coral" aria-hidden="true" size={24} />
            </div>
            <dl className="grid gap-3 sm:grid-cols-2">
              <InfoTerm label="Blood type" value={emergencyCard.bloodType} />
              <InfoTerm label="Allergies" value={emergencyCard.allergies.join(", ")} />
              <InfoTerm label="Conditions" value={emergencyCard.conditions.join(", ")} />
              <InfoTerm
                label="Emergency contact"
                value={emergencyCard.emergencyContacts[0].name}
              />
            </dl>
          </div>
        </section>
      </div>
    </main>
  );
}

type MetricCardProps = {
  icon: React.ComponentType<{ size?: number; className?: string; "aria-hidden"?: string }>;
  label: string;
  value: string;
  detail: string;
};

function MetricCard({ icon: Icon, label, value, detail }: MetricCardProps) {
  return (
    <article className="rounded-lg border border-moss/10 bg-white p-4 shadow-sm">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-medium text-ink/60">{label}</p>
          <h2 className="mt-1 text-2xl font-semibold text-ink">{value}</h2>
          <p className="mt-1 text-sm text-ink/60">{detail}</p>
        </div>
        <span className="grid size-10 place-items-center rounded-md bg-cloud text-moss">
          <Icon aria-hidden="true" size={18} />
        </span>
      </div>
    </article>
  );
}

function InfoTerm({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-lg bg-cloud/70 p-3">
      <dt className="text-sm font-medium text-ink/60">{label}</dt>
      <dd className="mt-1 font-semibold text-ink">{value}</dd>
    </div>
  );
}
