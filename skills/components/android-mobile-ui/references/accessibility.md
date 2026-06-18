# Accessibility

## Minimum Bar

- Meaningful icons/images need `contentDescription`.
- Decorative icons should use `contentDescription = null`.
- Touch targets should be at least 48dp.
- Text should wrap cleanly at large font sizes.
- Do not rely only on color to communicate severity.

## TalkBack

- Prefer concise labels.
- Group related small elements when they form one logical item.
- Avoid duplicate announcements from icon plus adjacent text.

## Health Context

Health UI should be calm and clear. Avoid alarmist language unless a user action truly requires urgent attention. Insights should not sound like diagnoses.
