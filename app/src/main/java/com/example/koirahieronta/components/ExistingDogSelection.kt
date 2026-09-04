package com.example.koirahieronta.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.koirahieronta.viewmodels.ClientViewModel
import com.example.koirahieronta.viewmodels.DogViewModel

@Composable
fun ExistingDogSelection(
    dogViewModel: DogViewModel, clientViewModel: ClientViewModel
) {
    val clientState by clientViewModel.state.collectAsState()
    var openDogMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                dogViewModel.selectedDogName,
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text("Koira") },
                trailingIcon = {
                    IconButton(onClick = { openDogMenu = !openDogMenu }) {
                        if (openDogMenu) {
                            Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Close")

                        } else {
                            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "Open")

                        }
                    }
                })
            DropdownMenu(
                expanded = openDogMenu,
                onDismissRequest = { openDogMenu = false },
            ) {

                LaunchedEffect(clientState.selectedClientId) {
                    if (clientState.selectedClientId != null) {
                        clientViewModel.setClientWithDogs(clientState.selectedClientId!!)
                    }
                }

                val client by clientViewModel.selectedClientWithDogs.collectAsStateWithLifecycle()

                client?.dogs?.forEach { dog ->
                    DropdownMenuItem({ Text(dog.name) }, onClick = {
                        dogViewModel.selectedDogName.edit {
                            replace(0, length, dog.name)
                            openDogMenu = false
                        }
                        dogViewModel.setSelectedDogId(dog.dogId)
                        dogViewModel.dogNameState.edit { replace(0, length, dog.name) }
                        dogViewModel.setDateOfBirth(dog.dateOfBirth)
                        dogViewModel.dogBreedState.edit { replace(0, length, dog.breed) }
                        dogViewModel.longTermIllnessState.edit {
                            dog.longTermIllness?.let {
                                replace(
                                    0, length, it
                                )
                            }
                        }
                        dogViewModel.hobbiesState.edit {
                            dog.hobbies?.let {
                                replace(
                                    0, length, it
                                )
                            }
                        }
                    })
                }
            }
        }
    }
}
