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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.koirahieronta.viewmodels.ClientViewModel
import com.example.koirahieronta.viewmodels.DogViewModel

@Composable
fun ClientCreationOrSelection(
    clientViewModel: ClientViewModel,
    clientSelectedIndex: MutableIntState,
    nextButtonEnabled: Boolean,
    expandClientCard: MutableState<Boolean>,
    toggleCard: (String) -> Unit,

    ) {

    if (clientSelectedIndex.intValue == 0) {
        LaunchedEffect(clientSelectedIndex) {
            clientViewModel.clearAllStates()
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
                Text("Asiakas")
                IconButton(onClick = {
                    toggleCard("Asiakas")
                }) {
                    if (expandClientCard.value) {
                        Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Close")
                    } else {
                        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "open")
                    }
                }
            }

            if (expandClientCard.value) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val buttonOptions = listOf("Uusi Asiakas", "Valitse Asiakas")
                        buttonOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index, count = buttonOptions.size
                                ),
                                onClick = { clientSelectedIndex.intValue = index },
                                selected = index == clientSelectedIndex.intValue,
                                label = { Text(label) })
                        }
                    }

                    if (clientSelectedIndex.intValue == 0) {
                        NewClientCreation(clientViewModel)
                    } else {
                        ExistingClientSelection(clientViewModel)
                    }

                    Button(
                        onClick = {
                            toggleCard("Koira")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 4.dp, 0.dp, 0.dp),
                        shape = RoundedCornerShape(4.dp),
                        enabled = nextButtonEnabled
                    ) {
                        Text("Seuraava")
                    }
                }
            }
        }
    }
}

@Composable
fun DogCreationOrSelection(
    dogViewModel: DogViewModel,
    clientViewModel: ClientViewModel,
    dogSelectedIndex: MutableIntState,
    nextButtonEnabled: Boolean,
    expandDogCard: MutableState<Boolean>,
    toggleCard: (String) -> Unit,

    ) {
    if (dogSelectedIndex.intValue == 0) {
        LaunchedEffect(dogSelectedIndex) {
            dogViewModel.clearAllStates()
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
                Text("Koira")
                IconButton(onClick = {
                    toggleCard("Koira")
                }) {
                    if (expandDogCard.value) {
                        Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Close")
                    } else {
                        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "open")
                    }
                }
            }

            if (expandDogCard.value) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val buttonOptions = listOf("Uusi Koira", "Valitse Koira")
                        buttonOptions.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index, count = buttonOptions.size
                                ),
                                onClick = { dogSelectedIndex.intValue = index },
                                selected = index == dogSelectedIndex.intValue,
                                label = { Text(label) })
                        }
                    }

                    if (dogSelectedIndex.intValue == 0) {
                        NewDogCreation(dogViewModel, expandDogCard)
                    } else {
                        ExistingDogSelection(dogViewModel, clientViewModel)
                    }

                    Button(
                        onClick = {
                            toggleCard("Hieronta")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 4.dp, 0.dp, 0.dp),
                        shape = RoundedCornerShape(4.dp),
                        enabled = nextButtonEnabled
                    ) {
                        Text("Seuraava")
                    }
                }
            }
        }
    }
}

