import { AlertTriangle, RefreshCw } from "lucide-react";

type ErrorStateProps = {
  title?: string;
  message?: string;
  onRetry?: () => void;
};

export function ErrorState({
  title = "Something went wrong",
  message = "We couldn't load your health data. This could be a temporary issue.",
  onRetry
}: ErrorStateProps) {
  return (
    <div className="flex flex-col items-center justify-center rounded-lg border border-coral/20 bg-coral/5 px-6 py-16 text-center">
      <span className="mb-4 grid size-16 place-items-center rounded-full bg-coral/10 text-coral">
        <AlertTriangle aria-hidden="true" size={32} />
      </span>
      <h3 className="text-lg font-semibold text-ink">{title}</h3>
      <p className="mt-2 max-w-md text-sm leading-6 text-ink/65">{message}</p>
      {onRetry && (
        <button
          onClick={onRetry}
          className="focus-ring mt-6 inline-flex items-center gap-2 rounded-md bg-moss px-4 py-2 text-sm font-medium text-white shadow-sm"
        >
          <RefreshCw aria-hidden="true" size={16} />
          Try again
        </button>
      )}
    </div>
  );
}
