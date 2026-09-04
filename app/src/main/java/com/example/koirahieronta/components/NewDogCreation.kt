package com.example.koirahieronta.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.koirahieronta.viewmodels.DogViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun NewDogCreation(
    dogViewModel: DogViewModel, expandDogCard: MutableState<Boolean>
) {
    val openBirthDateDialog = remember { mutableStateOf(false) }
    val birthDateState = rememberTextFieldState()

    val afterDateFocusRequester = remember { FocusRequester() }
    val focusRequester = remember { FocusRequester() }


    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (expandDogCard.value) {
            LaunchedEffect(Unit) {
                delay(200.milliseconds)
                focusRequester.requestFocus()
            }
        }

        EditableTextField(
            dogViewModel.dogNameState,
            "Koiran nimi",
            multiLine = false,
            imeAction = ImeAction.Next,
            capitalization = KeyboardCapitalization.Words,
            focusRequester = focusRequester,
            customKeyboardActionHandler = {
                openBirthDateDialog.value = true
            })

        DatePickerField(
            birthDateState,
            openBirthDateDialog,
            dogViewModel::setDateOfBirth,
            "Syntymäaika",
            focusRequester = afterDateFocusRequester,
        )

        EditableTextField(
            dogViewModel.dogBreedState,
            "Rotu",
            multiLine = false,
            imeAction = ImeAction.Next,
            focusRequester = afterDateFocusRequester
        )

        EditableTextField(dogViewModel.hobbiesState, "Harrastukset ja liikunta")

        EditableTextField(dogViewModel.longTermIllnessState, "Perussairaudet")
    }
}