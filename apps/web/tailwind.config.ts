import type { Config } from "tailwindcss";

const config: Config = {
  content: ["./src/**/*.{ts,tsx}", "../../packages/shared/src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        ink: "#16211c",
        moss: "#285947",
        mint: "#7bd9a2",
        coral: "#ef7b63",
        amber: "#f4b860",
        cloud: "#eef3ef"
      },
      boxShadow: {
        soft: "0 18px 50px rgba(22, 33, 28, 0.10)"
      }
    }
  },
  plugins: []
};

export default config;
