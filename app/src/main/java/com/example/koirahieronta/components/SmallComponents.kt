package com.example.koirahieronta.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.brush.compose.createWithComposeColor


object DrawingBrush {
    val brush = Brush.createWithComposeColor(
        family = StockBrushes.marker(),
        size = 10f,
        color = Color.Red,
        epsilon = 0.1f,
    )
}

@Composable
fun CardEditButtons(onClickDelete: () -> Unit, onClickSave: () -> Unit, editState: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val buttonModifier = Modifier.padding(top = 4.dp)

        Button(
            onClick = onClickDelete, modifier = buttonModifier, shape = RoundedCornerShape(4.dp)
        ) {
            Icon(Icons.Rounded.DeleteForever, contentDescription = "Delete")
        }
        Button(onClick = onClickSave, modifier = buttonModifier, shape = RoundedCornerShape(4.dp)) {
            if (editState) {
                Icon(Icons.Rounded.Save, contentDescription = "Save")
            } else {
                Icon(Icons.Rounded.Edit, contentDescription = "Edit")
            }
        }
    }
}

@Composable
fun CheckBoxRow(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)

        Checkbox(
            checked = checked, onCheckedChange = { onChecked(it) })
    }
}

@Composable
fun EditableTextField(
    state: TextFieldState,
    label: String,
    multiLine: Boolean = true,
    readOnly: Boolean = false,
    imeAction: ImeAction = ImeAction.Default,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    focusRequester: FocusRequester? = null,
    customKeyboardActionHandler: KeyboardActionHandler? = null,
) {
    val textFieldModifier = focusRequester?.let {
        Modifier
            .fillMaxWidth()
            .focusRequester(it)
    }

    OutlinedTextField(
        state = state,
        modifier = textFieldModifier ?: Modifier.fillMaxWidth(),
        readOnly = readOnly,
        lineLimits = if (multiLine) TextFieldLineLimits.Default else TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            capitalization = capitalization,
            showKeyboardOnFocus = true,
        ),
        label = { Text(label) },
        onKeyboardAction = customKeyboardActionHandler,
    )
}


@Composable
fun NullableTextField(label: String, freeText: String?) {
    if (freeText?.isNotEmpty() == true) {
        OutlinedTextField(
            value = freeText,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            readOnly = true
        )
    }
}