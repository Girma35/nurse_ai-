export function DashboardSkeleton() {
  return (
    <div className="mx-auto flex max-w-7xl flex-col gap-6 px-4 py-5 sm:px-6 lg:px-8">
      <header className="border-b border-moss/10 pb-5">
        <div className="h-8 w-48 animate-pulse rounded-md bg-moss/10" />
        <div className="mt-3 h-4 w-36 animate-pulse rounded-md bg-moss/5" />
      </header>

      <section className="grid gap-4 lg:grid-cols-[1.25fr_0.75fr]">
        <div className="h-40 animate-pulse rounded-lg bg-moss/5" />
        <div className="grid gap-4 sm:grid-cols-3 lg:grid-cols-1">
          {[1, 2, 3].map((i) => (
            <div key={i} className="h-24 animate-pulse rounded-lg bg-moss/5" />
          ))}
        </div>
      </section>

      <section className="grid gap-4 xl:grid-cols-[0.9fr_1.1fr]">
        <div className="h-64 animate-pulse rounded-lg bg-moss/5" />
        <div className="h-64 animate-pulse rounded-lg bg-moss/5" />
      </section>

      <section className="grid gap-4 xl:grid-cols-[1.1fr_0.9fr]">
        <div className="h-32 animate-pulse rounded-lg bg-moss/5" />
        <div className="h-48 animate-pulse rounded-lg bg-moss/5" />
      </section>
    </div>
  );
}

export function MetricCardSkeleton() {
  return (
    <div className="h-24 animate-pulse rounded-lg border border-moss/10 bg-white p-4 shadow-sm">
      <div className="h-3 w-16 rounded bg-moss/10" />
      <div className="mt-3 h-6 w-20 rounded bg-moss/10" />
      <div className="mt-2 h-3 w-28 rounded bg-moss/5" />
    </div>
  );
}
