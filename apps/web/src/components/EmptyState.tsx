import { HeartPulse } from "lucide-react";

type EmptyStateProps = {
  title?: string;
  description?: string;
  icon?: React.ComponentType<{ size?: number; className?: string }>;
};

export function EmptyState({
  title = "No data yet",
  description = "Start tracking your health from the mobile app. Your check-ins, symptoms, and medications will appear here after syncing.",
  icon: Icon = HeartPulse
}: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center rounded-lg border border-dashed border-moss/20 bg-white/60 px-6 py-16 text-center">
      <span className="mb-4 grid size-16 place-items-center rounded-full bg-cloud text-moss">
        <Icon aria-hidden="true" size={32} />
      </span>
      <h3 className="text-lg font-semibold text-ink">{title}</h3>
      <p className="mt-2 max-w-md text-sm leading-6 text-ink/65">{description}</p>
    </div>
  );
}
