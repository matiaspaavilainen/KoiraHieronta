package com.example.koirahieronta.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.ink.strokes.Stroke
import com.example.koirahieronta.viewmodels.MassageViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MassageCreation(
    massageViewModel: MassageViewModel,
    expandMassageCard: MutableState<Boolean>,
    drawingMode: MutableState<Boolean>,
    finishedStrokesRight: MutableState<List<Stroke>>,
    finishedStrokesLeft: MutableState<List<Stroke>>,
    massageOk: Boolean,
    toggleCard: (String) -> Unit,
) {
    val openVisitDateDialog = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    if (expandMassageCard.value) {
        LaunchedEffect(Unit) {
            delay(200.milliseconds)
            focusRequester.requestFocus()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp, 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Hieronta")
                IconButton(onClick = {
                    toggleCard("Hieronta")
                }) {
                    if (expandMassageCard.value) {
                        Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Close")
                    } else {
                        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "open")
                    }
                }
            }

            if (expandMassageCard.value) {
                Column(modifier = Modifier.fillMaxSize()) {
                    DatePickerField(
                        massageViewModel.visitDateTextState,
                        openVisitDateDialog,
                        massageViewModel::setVisitDate,
                        "Käyntipäivämäärä"
                    )
                    EditableTextField(
                        massageViewModel.reasonForVisitState,
                        "Hoitokäynnin syy",
                        focusRequester = focusRequester
                    )
                    EditableTextField(massageViewModel.currentHealthState, "Terveydentila")
                    EditableTextField(massageViewModel.movementState, "Liikkuminen")
                    EditableTextField(massageViewModel.structuralNotesState, "Huomiot rakenteesta")
                    EditableTextField(massageViewModel.otherNotesState, "Muut huomiot")

                    MusclePicker(
                        massageViewModel, finishedStrokesRight, finishedStrokesLeft, drawingMode
                    )
                }

                Button(
                    onClick = {
                        massageViewModel.setMuscleMarkings(
                            finishedStrokesRight.value, finishedStrokesLeft.value
                        )
                        toggleCard("Tallennus")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 4.dp, 0.dp, 0.dp),
                    shape = RoundedCornerShape(4.dp),
                    enabled = massageOk
                ) {
                    Text("Seuraava")
                }
            }
        }
    }
}
