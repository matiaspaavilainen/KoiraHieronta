package com.example.koirahieronta.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.ink.strokes.Stroke
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.koirahieronta.components.ClientCreationOrSelection
import com.example.koirahieronta.components.DogCreationOrSelection
import com.example.koirahieronta.components.MassageCreation
import com.example.koirahieronta.components.SavingCard
import com.example.koirahieronta.viewmodels.ClientViewModel
import com.example.koirahieronta.viewmodels.DogViewModel
import com.example.koirahieronta.viewmodels.MassageViewModel
import com.example.koirahieronta.viewmodels.SettingsViewModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun HomeScreen() {
    val massageViewModel: MassageViewModel = hiltViewModel()
    val dogViewModel: DogViewModel = hiltViewModel()
    val clientViewModel: ClientViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    val dogState by dogViewModel.state.collectAsStateWithLifecycle()

    val dogSelectedIndex = remember { mutableIntStateOf(0) }
    val clientSelectedIndex = remember { mutableIntStateOf(0) }

    val scrollState = rememberScrollState()

    val showFailedFile = remember { mutableStateOf(false) }

    val expandClientCard = remember { mutableStateOf(true) }
    val expandDogCard = remember { mutableStateOf(false) }
    val expandMassageCard = remember { mutableStateOf(false) }
    val expandSavingCard = remember { mutableStateOf(false) }

    val drawingMode = remember { mutableStateOf(false) }

    val finishedStrokesRight = remember {
        mutableStateOf<List<Stroke>>(emptyList())
    }
    val finishedStrokesLeft = remember {
        mutableStateOf<List<Stroke>>(emptyList())
    }

    val expandedCard = mutableMapOf(
        Pair("Asiakas", expandClientCard),
        Pair("Koira", expandDogCard),
        Pair("Hieronta", expandMassageCard),
        Pair("Tallennus", expandSavingCard)
    )

    fun toggleCard(currentCard: String) {
        expandedCard.forEach { (card, state) ->
            if (card == currentCard) {
                state.value = !state.value
            } else {
                expandedCard.getValue(card).value = false
            }
        }
    }

    fun resetStates() {
        finishedStrokesRight.value = emptyList()
        finishedStrokesLeft.value = emptyList()

        dogSelectedIndex.intValue = 0
        clientSelectedIndex.intValue = 0

        dogViewModel.clearAllStates()
        clientViewModel.clearAllStates()
        massageViewModel.clearAllStates()
        settingsViewModel.clearStates()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(8.dp)
            .verticalScroll(scrollState, enabled = !drawingMode.value),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {

        val clientOk =
            clientViewModel.clientNameState.text.isNotBlank() && clientViewModel.clientEmailState.text.isNotBlank()

        ClientCreationOrSelection(
            clientViewModel, clientSelectedIndex, clientOk, expandClientCard, ::toggleCard
        )

        LaunchedEffect(clientSelectedIndex.intValue) {
            if (clientSelectedIndex.intValue == 1) {
                dogSelectedIndex.intValue = 1
            }
        }

        val dogOk =
            dogViewModel.dogNameState.text.isNotBlank() && dogViewModel.dogBreedState.text.isNotBlank() && dogState.dateOfBirth > 0

        DogCreationOrSelection(
            dogViewModel, clientViewModel, dogSelectedIndex, dogOk, expandDogCard, ::toggleCard
        )

        val massageOk = massageViewModel.reasonForVisitState.text.isNotBlank()

        MassageCreation(
            massageViewModel,
            expandMassageCard,
            drawingMode,
            finishedStrokesRight,
            finishedStrokesLeft,
            massageOk,
            ::toggleCard
        )

        val saveOk = clientOk && dogOk && massageOk

        SavingCard(
            clientViewModel,
            dogViewModel,
            massageViewModel,
            settingsViewModel,
            saveOk,
            expandSavingCard,
            showFailedFile,
            ::toggleCard,
            ::resetStates
        )

        if (showFailedFile.value) {
            AlertDialog(title = {
                Text(
                    "Tiedoston luominen epäonnistui",
                )
            }, text = {
                Text("Yhden tai useamman tiedoston luominen epäonnistui. Hierontatapahtuma on tallennettu laitteelle, mutta PDF tiedostoja ei luotu. Varmista että tallennuskansio on valittu.")
            }, onDismissRequest = {

            }, confirmButton = {

            }, dismissButton = {
                TextButton(
                    onClick = {
                        showFailedFile.value = false
                    }) {
                    Text("OK")
                }
            })
        }
    }
}
