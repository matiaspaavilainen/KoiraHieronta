package com.example.koirahieronta.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.koirahieronta.components.CardEditButtons
import com.example.koirahieronta.components.EditableTextField
import com.example.koirahieronta.components.MusclePicker
import com.example.koirahieronta.database.MassageWithDog
import com.example.koirahieronta.viewmodels.MassageEditState
import com.example.koirahieronta.viewmodels.MassageViewModel
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MassagesScreen() {
    val massageViewModel: MassageViewModel = hiltViewModel()
    val state by massageViewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.massages, key = { it.massage.id }) { massage ->
            MassageCard(massage, massageViewModel)
        }
    }
}

@Composable
fun MassageCard(massage: MassageWithDog, massageViewModel: MassageViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val showDeleteConfirmation = remember { mutableStateOf(false) }

    val massageEditState = MassageEditState(massage.massage)

    val finishedStrokesRight = remember {
        mutableStateOf(massage.massage.markedMusclesRight)
    }
    val finishedStrokesLeft = remember {
        mutableStateOf(massage.massage.markedMusclesLeft)
    }

    val df = DateFormat.getDateInstance(2, Locale.GERMANY)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp, 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(df.format(Date(massage.massage.visitDate)))
                Text(massage.dog.name)
                IconButton(onClick = {
                    expanded = !expanded
                }) {
                    if (expanded) {
                        Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Close")
                    } else {
                        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "open")
                    }
                }
            }

            AnimatedVisibility(expanded) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MusclePicker(
                        massageViewModel,
                        finishedStrokesRight,
                        finishedStrokesLeft,
                        viewingMode = true
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        EditableTextField(
                            massageEditState.reasonForVisit,
                            "Hoitokäynnin syy",
                            readOnly = !massageEditState.editing.value
                        )
                        EditableTextField(
                            massageEditState.movement,
                            "Liikkuminen",
                            readOnly = !massageEditState.editing.value
                        )
                        EditableTextField(
                            massageEditState.currentHealth,
                            "Terveydentila",
                            readOnly = !massageEditState.editing.value
                        )
                        EditableTextField(
                            massageEditState.structuralNotes,
                            "Huomiot rakenteesta",
                            readOnly = !massageEditState.editing.value
                        )
                        EditableTextField(
                            massageEditState.otherNotes,
                            "Muut huomiot",
                            readOnly = !massageEditState.editing.value
                        )
                    }

                    if (showDeleteConfirmation.value) {
                        AlertDialog(title = {
                            Text(
                                "Poista hieronta?"
                            )
                        }, text = {
                            Text("Haluatko poistaa tämän hierontakerran pysyvästi? Tätä toimintoa ei voi peruuttaa.")
                        }, onDismissRequest = {

                        }, confirmButton = {
                            TextButton(
                                onClick = {
                                    massageViewModel.deleteMassage(massage.massage)
                                }) {
                                Text("Poista")
                            }

                        }, dismissButton = {
                            TextButton(
                                onClick = {
                                    showDeleteConfirmation.value = false
                                }) {
                                Text("Peruuta")
                            }
                        })
                    }

                    CardEditButtons({ showDeleteConfirmation.value = true }, {

                        if (massageEditState.editing.value) {
                            massageViewModel.updateMassage(
                                massage.massage.copy(
                                    reasonForVisit = massageEditState.reasonForVisit.text.toString(),
                                    movement = massageEditState.movement.text.toString(),
                                    currentHealth = massageEditState.currentHealth.text.toString(),
                                    structuralNotes = massageEditState.structuralNotes.text.toString(),
                                    otherNotes = massageEditState.otherNotes.text.toString()
                                )
                            )
                        }
                        massageEditState.editing.value = !massageEditState.editing.value
                    }, massageEditState.editing.value)
                }
            }
        }
    }
}

