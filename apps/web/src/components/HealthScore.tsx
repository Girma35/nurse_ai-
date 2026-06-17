type HealthScoreProps = {
  score: number;
};

export function HealthScore({ score }: HealthScoreProps) {
  const normalizedScore = Math.max(0, Math.min(100, score));
  const angle = normalizedScore * 3.6;

  return (
    <div className="flex items-center gap-5">
      <div
        className="grid size-28 shrink-0 place-items-center rounded-full"
        style={{
          background: `conic-gradient(#285947 ${angle}deg, #dfe9e2 ${angle}deg 360deg)`
        }}
        aria-label={`Health score ${normalizedScore} out of 100`}
      >
        <div className="grid size-20 place-items-center rounded-full bg-white shadow-soft">
          <span className="text-3xl font-semibold text-ink">{normalizedScore}</span>
        </div>
      </div>
      <div>
        <p className="text-sm font-medium uppercase tracking-[0.14em] text-moss">
          Health score
        </p>
        <h2 className="mt-1 text-2xl font-semibold text-ink">Stable today</h2>
        <p className="mt-2 max-w-xs text-sm leading-6 text-ink/70">
          Sleep and fatigue are the main factors affecting the current score.
        </p>
      </div>
    </div>
  );
}
