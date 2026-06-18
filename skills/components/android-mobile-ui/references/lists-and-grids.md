# Lists And Grids

## Dynamic Lists

Use `LazyColumn` for dynamic or potentially long lists.

```kotlin
LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(
        items = records,
        key = { it.id }
    ) { record ->
        RecordRow(record = record)
    }
}
```

## Empty Lists

Every list screen needs an empty state. Empty states should explain what is missing and offer one action when possible.

## Item Rows

- Keep primary text first.
- Put metadata below or trailing.
- Use icons only when they improve scanning.
- Use stable keys for stateful rows.
- Avoid cramped rows; touch targets must be at least 48dp.
