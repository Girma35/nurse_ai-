# Forms And Inputs

## Form Layout

Use a vertical layout with clear grouping and one primary save action.

```kotlin
OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text("Name") },
    singleLine = true,
    modifier = Modifier.fillMaxWidth()
)
```

## Validation

- Validate close to the field.
- Preserve user input on rotation with ViewModel or `rememberSaveable`.
- Disable destructive actions while saving.
- Avoid blocking local save on network availability.

## Health App Forms

- Keep units visible: ml, kg, cm, hours.
- Use sliders or segmented controls for bounded values.
- Use text fields for free-form notes.
- Use defaults conservatively; health data should not be silently guessed.
