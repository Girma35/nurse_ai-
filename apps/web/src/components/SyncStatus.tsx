import { Cloud, Database, RefreshCw, Wifi } from "lucide-react";

type SyncStatusProps = {
  pendingCount?: number;
  lastSyncedAt?: string;
};

export function SyncStatus({
  pendingCount = 0,
  lastSyncedAt
}: SyncStatusProps) {
  const syncItems = [
    {
      label: "Room DB",
      value: "Ready",
      icon: Database
    },
    {
      label: "Sync worker",
      value: pendingCount > 0 ? `${pendingCount} pending` : "Synced",
      icon: RefreshCw
    },
    {
      label: "Cloud",
      value: lastSyncedAt ? "Synced" : "Online",
      icon: Cloud
    },
    {
      label: "Network",
      value: "Available",
      icon: Wifi
    }
  ];

  return (
    <div className="grid gap-3 sm:grid-cols-2">
      {syncItems.map((item) => {
        const Icon = item.icon;

        return (
          <div
            className="flex items-center justify-between rounded-lg border border-moss/10 bg-white p-4 shadow-sm"
            key={item.label}
          >
            <div className="flex items-center gap-3">
              <span className="grid size-10 place-items-center rounded-md bg-cloud text-moss">
                <Icon aria-hidden="true" size={18} />
              </span>
              <span className="text-sm font-medium text-ink">{item.label}</span>
            </div>
            <span className="text-sm text-ink/65">{item.value}</span>
          </div>
        );
      })}
      {lastSyncedAt && (
        <p className="col-span-full text-xs text-ink/45">
          Last synced: {new Date(lastSyncedAt).toLocaleString()}
        </p>
      )}
    </div>
  );
}
