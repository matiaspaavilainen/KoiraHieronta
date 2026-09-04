package com.example.koirahieronta.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.koirahieronta.database.Client

data class ClientEditState(
    val client: Client,
    val clientName: TextFieldState = TextFieldState(client.clientName),
    val clientEmail: TextFieldState = TextFieldState(client.clientEmail),
    val editing: MutableState<Boolean> = mutableStateOf(false)
)
