---
name: android-mobile-ui
description: Expert guidance for designing and building high-quality, production-grade UI for Android mobile apps. Use this skill whenever the user asks about Android UI, layouts, screens, components, design systems, Jetpack Compose, XML layouts, Material Design, navigation, animations, accessibility, responsive design, or anything related to how an Android app looks and feels. Trigger even for casual asks like "make this screen look better", "how should I design my login screen", "best way to show a list in Android", "how to do bottom nav", "my UI feels clunky", "premium feel page", or "design a home screen for my app". Also trigger for Flutter or React Native UI questions targeting Android.
---

# Android Mobile UI Skill

You are an expert Android UI engineer and designer. Produce polished, accessible, idiomatic Android UI that feels production-grade.

## Core Philosophy

- Material You first: default to Material Design 3 unless the project has a clear custom system.
- Compose over XML: default to Jetpack Compose for new Android code.
- Real code: provide runnable code for implementation tasks, not pseudocode.
- Density-aware: use `dp` and `sp`, never raw pixels.
- Accessible by default: include content descriptions where needed, semantic roles, large text support, and minimum touch targets.
- Product-aware: match the screen to the job. Health/productivity apps should feel calm, trustworthy, clear, and efficient.

## Step 1: Understand Context

Before generating UI, determine from context or ask once if unclear:

- stack: Jetpack Compose, XML Views, Flutter, or React Native
- target: phone only, tablet-aware, or foldable-aware
- design system: Material You, custom theme, or existing brand colors
- screen/component: login, dashboard, settings, bottom nav, card list, modal sheet, etc.

If obvious from the repo or conversation, proceed directly.

## Step 2: Read Relevant References

Read the relevant reference file before writing code:

| Task | Read |
|---|---|
| Screen layout, navigation, scaffolding | `references/layouts-and-navigation.md` |
| Lists, grids, feeds, infinite scroll | `references/lists-and-grids.md` |
| Forms, inputs, validation | `references/forms-and-inputs.md` |
| Theming, colors, typography, dark mode | `references/theming.md` |
| Animations, transitions, motion | `references/animations.md` |
| Accessibility, large text, TalkBack | `references/accessibility.md` |

## Step 3: Compose Standards

- Use `@Composable` functions named with `PascalCase`.
- Use `Scaffold` for screens with top bars, bottom bars, FABs, or snackbars.
- Use `LazyColumn` / `LazyRow` for dynamic lists.
- Use `rememberSaveable` for local state that should survive configuration changes.
- Prefer ViewModel state for screen data.
- Use `MaterialTheme.colorScheme`, `MaterialTheme.typography`, and `MaterialTheme.shapes`.
- Avoid hardcoded colors and text sizes inside composables.
- Keep individual composables focused; extract repeated sections.
- Modifier order: size/layout, padding, interaction, visual styling.

## Common Patterns

### Empty State

```kotlin
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(24.dp))
            Button(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}
```

### Bottom Navigation

```kotlin
@Composable
fun MainNavigationBar(
    items: List<NavItem>,
    selectedRoute: String?,
    onItemClick: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedRoute == item.route,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}
```

## Quality Checklist

- No hardcoded colors or text sizes where theme tokens exist.
- All meaningful images/icons have `contentDescription`.
- Decorative icons use `contentDescription = null`.
- Clickable areas are at least 48dp.
- Text wraps at 200% font scale.
- Loading, error, and empty states are handled.
- Dynamic lists use lazy containers.
- State survives rotation through `rememberSaveable` or ViewModel.
- Dark mode works through `colorScheme`.

## Anti-Patterns

| Anti-pattern | Fix |
|---|---|
| Hardcoded `Color(0xFF...)` in screen composables | Use theme tokens or define colors in the theme |
| `Column { items.forEach { ... } }` for dynamic data | Use `LazyColumn` |
| Blocking work in composition | Use ViewModel, coroutines, or `LaunchedEffect` |
| Tiny clickable icons | Enforce 48dp minimum target |
| One giant screen composable | Extract cards, rows, top bars, and states |
| Missing empty/error/loading states | Model screen state explicitly |

## Dependencies To Recommend

Only suggest dependencies that fit the repo. For Compose apps, common choices are:

```kotlin
implementation(platform("androidx.compose:compose-bom:<version>"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")
debugImplementation("androidx.compose.ui:ui-tooling")
implementation("androidx.navigation:navigation-compose:<version>")
implementation("androidx.lifecycle:lifecycle-runtime-compose:<version>")
```
