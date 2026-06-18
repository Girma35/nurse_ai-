# Nursy AI Handoff

Current status:
Nursy AI MVP skeleton is mostly finished.

Done:
- Offline-first Android app structure
- Room local database
- Main health screens
- Local rules engine
- Optional Offline AI settings/download skeleton
- Debug AI controls
- Appwrite SDK/client skeleton
- Premium AI direction defined

Current work in progress:
- Premium-only AI features
- AI daily review
- AI weekly review
- Custom AI insight
- Premium-only medication label scan
- Safe urgent-review wording

Next step:
Finish the premium AI feature wiring and make sure the app compiles.

Important rule:
Do not add new features after this until real users test the app and give feedback.

Feature boundary:
Free users:
- Normal offline tracking
- Local rules
- Manual medication entry

Premium users:
- Download offline AI
- Medication scan/photo OCR assist
- AI daily review
- AI weekly review
- Custom AI insight
- AI summaries

Safety boundary:
AI is an assistant, not a doctor.
It can summarize, explain, draft, and highlight patterns.
It must not diagnose, prescribe, or make emergency decisions.

When returning:
Tell Codex: "Continue from the premium AI wiring."
