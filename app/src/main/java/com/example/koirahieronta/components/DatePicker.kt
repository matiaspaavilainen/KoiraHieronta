package com.example.koirahieronta.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import java.text.DateFormat
import java.util.Locale

@Composable
fun DatePickerField(
    state: TextFieldState,
    openDialog: MutableState<Boolean>,
    updateFunction: (Long) -> Unit,
    label: String,
    buttonEnabled: Boolean = true,
    focusRequester: FocusRequester? = null,
) {


    OutlinedTextField(
        state = state,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { openDialog.value = true }, enabled = buttonEnabled) {
                Icon(Icons.Rounded.CalendarMonth, contentDescription = "Calendar")
            }
        })

    if (openDialog.value) {
        DatePickerModalInput(
            onDateSelected = { datePickerState ->
                if (datePickerState.selectedDateMillis != null) {
                    state.edit {
                        replace(
                            0,
                            length,
                            DateFormat.getDateInstance(2, Locale.GERMANY)
                                .format(datePickerState.selectedDateMillis)
                        )
                    }
                    updateFunction(datePickerState.selectedDateMillis!!)
                    focusRequester?.requestFocus()
                }
            }, onDismiss = {
                openDialog.value = false
            }, openDialog
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (DatePickerState) -> Unit,
    onDismiss: () -> Unit,
    openDateDialog: MutableState<Boolean>
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDate = java.time.LocalDate.now(),
        initialDisplayMode = DisplayMode.Picker,
    )

    DatePickerDialog(onDismissRequest = { openDateDialog.value = false }, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState)
            onDismiss()
        }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Peruuta")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

