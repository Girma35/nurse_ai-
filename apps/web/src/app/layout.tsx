import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Nursy AI Dashboard",
  description: "Offline-first health dashboard for Nursy AI"
};

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
