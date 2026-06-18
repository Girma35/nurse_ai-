# Layouts And Navigation

## Compose Screen Structure

Use `Scaffold` when a screen has top bars, bottom bars, floating actions, or snackbars. Keep the screen body as a separate composable.

```kotlin
@Composable
fun ExampleScreen(
    uiState: ExampleUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Example") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        ExampleContent(
            uiState = uiState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}
```

## Premium Mobile Feel

- Prefer calm hierarchy over decorative clutter.
- Use generous but not wasteful spacing: 16dp screen padding, 12dp related item spacing, 24dp between sections.
- Use a clear primary action per screen.
- Keep cards at 8dp-12dp radius unless the existing design system says otherwise.
- Use section headers for scanability.
- Avoid nesting cards inside cards.

## Navigation

- Use bottom navigation for 3-5 top-level destinations.
- Keep emergency or high-importance actions quickly reachable.
- Use modal bottom sheets for short secondary tasks.
- Use full screens for multi-step or high-focus flows.
