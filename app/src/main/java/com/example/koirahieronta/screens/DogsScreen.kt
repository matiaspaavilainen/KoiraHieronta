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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.koirahieronta.components.CardEditButtons
import com.example.koirahieronta.components.DatePickerField
import com.example.koirahieronta.components.EditableTextField
import com.example.koirahieronta.components.NullableTextField
import com.example.koirahieronta.database.DogWithMassages
import com.example.koirahieronta.viewmodels.ClientViewModel
import com.example.koirahieronta.viewmodels.DogEditState
import com.example.koirahieronta.viewmodels.DogViewModel
import com.example.koirahieronta.viewmodels.MassageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogsScreen(
    dogViewModel: DogViewModel, massageViewModel: MassageViewModel, clientViewModel: ClientViewModel
) {
    val dogState by dogViewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dogState.dogs, key = { it.dog.dogId }) { dog ->
            DogCard(dog, dogViewModel, massageViewModel, clientViewModel)
        }
    }
}

@Composable
fun DogCard(
    dog: DogWithMassages,
    dogViewModel: DogViewModel,
    massageViewModel: MassageViewModel,
    clientViewModel: ClientViewModel
) {
    val dogEditState = DogEditState(dog.dog)
    val df = DateFormat.getDateInstance(2, Locale.GERMANY)

    val birthDateState = rememberTextFieldState(df.format(Date(dog.dog.dateOfBirth)))
    var ownerName = ""
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            ownerName = clientViewModel.getClientName(dog.dog.ownerId)
        }
    }

    var expanded by rememberSaveable { mutableStateOf(false) }
    val openBirthDateDialog = remember { mutableStateOf(false) }
    val showDeleteConfirmation = remember { mutableStateOf(false) }

    val initialFocusRequester = remember { FocusRequester() }
    val dateFocusRequester = remember { FocusRequester() }
    val afterDateFocusRequester = remember { FocusRequester() }

    Card(
        modifier = Modifier.fillMaxWidth(), onClick = { expanded = !expanded }) {
        Column(modifier = Modifier.padding(8.dp, 4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(dog.dog.name)
                Text(dog.dog.breed)
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
                Column {
                    if (dogEditState.editing.value) {
                        LaunchedEffect(Unit) {
                            initialFocusRequester.requestFocus()
                        }
                    }

                    NullableTextField("Omistaja", ownerName)

                    EditableTextField(
                        dogEditState.name,
                        "Nimi",
                        multiLine = false,
                        readOnly = !dogEditState.editing.value,
                        imeAction = ImeAction.Next,
                        customKeyboardActionHandler = {
                            dateFocusRequester.requestFocus()
                            openBirthDateDialog.value = true
                        })

                    fun setEditDOB(date: Long) {
                        dogEditState.birthDate = date
                    }

                    DatePickerField(
                        birthDateState,
                        openBirthDateDialog,
                        ::setEditDOB,
                        "Syntymäaika",
                        buttonEnabled = dogEditState.editing.value,
                        focusRequester = afterDateFocusRequester,
                    )

                    EditableTextField(
                        dogEditState.breed,
                        "Rotu",
                        multiLine = false,
                        readOnly = !dogEditState.editing.value,
                        imeAction = ImeAction.Next,
                        focusRequester = afterDateFocusRequester
                    )

                    EditableTextField(
                        dogEditState.hobbies,
                        "Harrastukset",
                        readOnly = !dogEditState.editing.value,
                    )

                    EditableTextField(
                        dogEditState.longTermIllness,
                        "Perussairaudet",
                        readOnly = !dogEditState.editing.value
                    )

                    NullableTextField("Käyntikerrat", dog.massages.size.toString())

                    if (showDeleteConfirmation.value) {
                        AlertDialog(title = {
                            Text(
                                "Poista koira?"
                            )
                        }, text = {
                            Text("Haluatko poistaa tämän koiran ja kaikki sen hierontakerrat pysyvästi? Tätä toimintoa ei voi peruuttaa.")
                        }, onDismissRequest = {

                        }, confirmButton = {
                            TextButton(
                                onClick = {
                                    dogViewModel.deleteDog(dog.dog)
                                    dog.massages.forEach { massage ->
                                        massageViewModel.deleteMassage(massage)
                                    }
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

                    CardEditButtons(
                        {
                        showDeleteConfirmation.value = true
                    }, {
                        if (dogEditState.editing.value) {
                            dogViewModel.updateDog(
                                dog.dog.copy(
                                    name = dogEditState.name.text.toString(),
                                    dateOfBirth = dogEditState.birthDate,
                                    breed = dogEditState.breed.text.toString(),
                                    longTermIllness = dogEditState.longTermIllness.text.toString(),
                                    hobbies = dogEditState.hobbies.text.toString()
                                )
                            )
                        }
                        dogEditState.editing.value = !dogEditState.editing.value
                    }, dogEditState.editing.value
                    )
                }
            }
        }
    }
}