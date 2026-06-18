# Animations

## Motion Rules

- Use motion to explain state changes, not decorate every element.
- Prefer short durations: 150-300ms.
- Respect reduced-motion expectations where platform APIs are available.
- Avoid blocking input with long animations.

## Compose APIs

Use:

- `AnimatedVisibility` for appearing/disappearing sections
- `animate*AsState` for simple value transitions
- `updateTransition` for coordinated state changes
- `LazyListState` for scroll-aware behavior

Keep animations deterministic and easy to remove.
