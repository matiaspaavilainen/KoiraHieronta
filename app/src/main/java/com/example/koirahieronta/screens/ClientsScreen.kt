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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.koirahieronta.components.CardEditButtons
import com.example.koirahieronta.components.EditableTextField
import com.example.koirahieronta.components.NullableTextField
import com.example.koirahieronta.database.ClientWithDogs
import com.example.koirahieronta.viewmodels.ClientEditState
import com.example.koirahieronta.viewmodels.ClientViewModel
import com.example.koirahieronta.viewmodels.DogViewModel
import com.example.koirahieronta.viewmodels.MassageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ClientsScreen(
    clientViewModel: ClientViewModel, dogViewModel: DogViewModel
) {
    val clientState by clientViewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clientState.clients, key = { it.client.clientId }) { client ->
            ClientCard(client, clientViewModel, dogViewModel)
        }
    }

}

@Composable
fun ClientCard(
    client: ClientWithDogs,
    clientViewModel: ClientViewModel,
    dogViewModel: DogViewModel,
) {
    val massageViewModel: MassageViewModel = hiltViewModel()

    val clientEditState = ClientEditState(client.client)
    val dogState = dogViewModel.state.collectAsStateWithLifecycle()

    var expanded by rememberSaveable { mutableStateOf(false) }
    val showDeleteConfirmation = remember { mutableStateOf(false) }

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
                Text(client.client.clientName)
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
                    EditableTextField(
                        clientEditState.clientName,
                        "Nimi",
                        multiLine = false,
                        readOnly = !clientEditState.editing.value,
                        imeAction = ImeAction.Next,
                    )

                    EditableTextField(
                        clientEditState.clientEmail,
                        "Sähköposti",
                        multiLine = false,
                        readOnly = !clientEditState.editing.value,
                        imeAction = ImeAction.Done,
                    )

                    val dogs = StringBuilder()
                    client.dogs.forEachIndexed { index, dog -> dogs.insert(index, dog.name + "\n") }

                    NullableTextField("Koirat", dogs.toString().trimEnd('\n'))

                    if (showDeleteConfirmation.value) {
                        AlertDialog(title = {
                            Text(
                                "Poista asiakas?"
                            )
                        }, text = {
                            Text("Haluatko poistaa tämän asiakkaan ja kaikki liittyvät tiedot (koirat ja hieronnat)? Tätä toimintoa ei voi peruuttaa.")
                        }, onDismissRequest = {

                        }, confirmButton = {
                            TextButton(
                                onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        client.dogs.forEach { dog ->
                                            dogState.value.dogs.filter { it.dog == dog }
                                                .forEach { (dog, massages) ->
                                                    massages.forEach { massage ->
                                                        massageViewModel.deleteMassage(massage)
                                                    }
                                                    dogViewModel.deleteDog(dog)
                                                }
                                            clientViewModel.deleteClient(client.client)
                                        }
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
                            if (clientEditState.editing.value) {
                                clientViewModel.updateClient(
                                    client.client.copy(
                                        clientName = clientEditState.clientName.text.toString(),
                                        clientEmail = clientEditState.clientEmail.text.toString()
                                    )
                                )
                            }
                            clientEditState.editing.value = !clientEditState.editing.value
                        }, clientEditState.editing.value
                    )
                }
            }
        }
    }
}