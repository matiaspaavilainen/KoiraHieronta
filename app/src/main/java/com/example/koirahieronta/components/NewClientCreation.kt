package com.example.koirahieronta.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.koirahieronta.viewmodels.ClientViewModel

@Composable
fun NewClientCreation(clientViewModel: ClientViewModel) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        EditableTextField(
            clientViewModel.clientNameState,
            "Nimi",
            multiLine = false,
            imeAction = ImeAction.Next,
            capitalization = KeyboardCapitalization.Words,
        )

        OutlinedTextField(
            state = clientViewModel.clientEmailState,
            modifier = Modifier.fillMaxWidth(),
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.None,
                showKeyboardOnFocus = true,
                keyboardType = KeyboardType.Email
            ),
            label = { Text("Sähköposti") },
            isError = clientViewModel.emailHasErrors
        )
    }
}