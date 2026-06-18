import { FileText, TrendingUp, TrendingDown } from "lucide-react";
import type { WeeklyReport } from "@nursy/shared";

type WeeklyReportCardProps = {
  report: WeeklyReport;
};

export function WeeklyReportCard({ report }: WeeklyReportCardProps) {
  return (
    <div className="rounded-lg border border-moss/10 bg-white p-5 shadow-sm">
      <div className="mb-4 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <FileText aria-hidden="true" size={20} className="text-moss" />
          <h2 className="text-lg font-semibold text-ink">Weekly Report</h2>
        </div>
        <span className="rounded-md bg-cloud px-2.5 py-1 text-xs font-medium text-moss">
          {report.weekStart} – {report.weekEnd}
        </span>
      </div>

      <div className="grid gap-4 sm:grid-cols-3">
        <div className="rounded-lg bg-cloud/70 p-4">
          <p className="text-sm font-medium text-ink/60">Avg. Health Score</p>
          <div className="mt-1 flex items-center gap-2">
            <p className="text-2xl font-bold text-ink">
              {report.averageHealthScore}
            </p>
            <span className="text-sm text-ink/60">/100</span>
          </div>
        </div>

        <div className="rounded-lg bg-cloud/70 p-4">
          <p className="text-sm font-medium text-ink/60">Adherence</p>
          <div className="mt-1 flex items-center gap-2">
            <p className="text-2xl font-bold text-ink">
              {report.adherenceRate}%
            </p>
            {report.adherenceRate >= 80 ? (
              <TrendingUp aria-hidden="true" size={20} className="text-mint" />
            ) : (
              <TrendingDown aria-hidden="true" size={20} className="text-coral" />
            )}
          </div>
        </div>

        <div className="rounded-lg bg-cloud/70 p-4">
          <p className="text-sm font-medium text-ink/60">Symptoms</p>
          <p className="mt-1 text-sm leading-6 text-ink/70">
            {report.symptomSummary}
          </p>
        </div>
      </div>

      {report.highlights.length > 0 && (
        <div className="mt-4">
          <p className="text-sm font-medium text-ink/60">Highlights</p>
          <ul className="mt-2 space-y-1">
            {report.highlights.map((highlight, i) => (
              <li key={i} className="flex items-start gap-2 text-sm text-ink/70">
                <span className="mt-1.5 size-1.5 shrink-0 rounded-full bg-mint" />
                {highlight}
              </li>
            ))}
          </ul>
        </div>
      )}

      {report.recommendations.length > 0 && (
        <div className="mt-4 rounded-lg bg-cloud/40 p-4">
          <p className="text-sm font-medium text-ink/60">Recommendations</p>
          <ul className="mt-2 space-y-1">
            {report.recommendations.map((rec, i) => (
              <li key={i} className="flex items-start gap-2 text-sm text-ink/70">
                <span className="mt-1.5 size-1.5 shrink-0 rounded-full bg-amber" />
                {rec}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
