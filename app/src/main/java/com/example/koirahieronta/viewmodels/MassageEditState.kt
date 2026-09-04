package com.example.koirahieronta.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.koirahieronta.database.Massage

data class MassageEditState(
    val massage: Massage, val reasonForVisit: TextFieldState = TextFieldState(
        massage.reasonForVisit.orEmpty()
    ), val movement: TextFieldState = TextFieldState(
        massage.movement.orEmpty()
    ), val currentHealth: TextFieldState = TextFieldState(
        massage.currentHealth.orEmpty()
    ), val structuralNotes: TextFieldState = TextFieldState(
        massage.structuralNotes.orEmpty()
    ), val otherNotes: TextFieldState = TextFieldState(
        massage.otherNotes.orEmpty()
    ), val editing: MutableState<Boolean> = mutableStateOf(false)
)
