import { Lightbulb } from "lucide-react";
import type { HealthInsight } from "@nursy/shared";

type InsightCarouselProps = {
  insights: HealthInsight[];
};

export function InsightCarousel({ insights }: InsightCarouselProps) {
  if (insights.length === 0) return null;

  return (
    <div className="rounded-lg border border-amber/20 bg-amber/[0.04] p-5">
      <div className="mb-4 flex items-center gap-2">
        <Lightbulb aria-hidden="true" size={20} className="text-amber" />
        <h2 className="text-lg font-semibold text-ink">Insights</h2>
      </div>
      <div className="grid gap-3">
        {insights.map((insight) => {
          const severityColor =
            insight.severity === "alert"
              ? "border-coral/30 bg-coral/5"
              : insight.severity === "warning"
                ? "border-amber/30 bg-amber/5"
                : "border-mint/30 bg-mint/5";

          return (
            <div
              key={insight.id}
              className={`rounded-lg border p-4 ${severityColor}`}
            >
              <div className="flex items-start gap-3">
                <div
                  className={`mt-1 size-2 shrink-0 rounded-full ${
                    insight.severity === "alert"
                      ? "bg-coral"
                      : insight.severity === "warning"
                        ? "bg-amber"
                        : "bg-mint"
                  }`}
                />
                <div>
                  <p className="font-semibold text-ink">{insight.title}</p>
                  <p className="mt-1 text-sm leading-6 text-ink/70">
                    {insight.message}
                  </p>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
