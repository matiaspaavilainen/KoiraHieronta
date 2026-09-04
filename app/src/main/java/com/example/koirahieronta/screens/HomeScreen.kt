package com.example.koirahieronta.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.ink.strokes.Stroke
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.koirahieronta.components.CheckBoxRow
import com.example.koirahieronta.components.ClientCreationOrSelection
import com.example.koirahieronta.components.DogCreationOrSelection
import com.example.koirahieronta.components.MassageCreation
import com.example.koirahieronta.viewmodels.ClientViewModel
import com.example.koirahieronta.viewmodels.DogViewModel
import com.example.koirahieronta.viewmodels.MassageViewModel
import com.example.koirahieronta.viewmodels.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Locale
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun HomeScreen(
    massageViewModel: MassageViewModel,
    dogViewModel: DogViewModel,
    clientViewModel: ClientViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
    val dogState by dogViewModel.state.collectAsStateWithLifecycle()
    val massageState by massageViewModel.state.collectAsStateWithLifecycle()
    val clientState by clientViewModel.state.collectAsStateWithLifecycle()

    val dogSelectedIndex = remember { mutableIntStateOf(0) }
    val clientSelectedIndex = remember { mutableIntStateOf(0) }

    val scrollState = rememberScrollState()
    val expandClientCard = remember { mutableStateOf(true) }
    val expandDogCard = remember { mutableStateOf(false) }
    val expandMassageCard = remember { mutableStateOf(false) }
    val expandSavingCard = remember { mutableStateOf(false) }
    var openPaymentMenu by remember { mutableStateOf(false) }
    var showFailedFile by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val expandedCard = mutableMapOf(
        Pair("Asiakas", expandClientCard),
        Pair("Koira", expandDogCard),
        Pair("Hieronta", expandMassageCard),
        Pair("Tallennus", expandSavingCard)
    )

    val drawingMode = remember { mutableStateOf(false) }

    val finishedStrokesRight = remember {
        mutableStateOf<List<Stroke>>(emptyList())
    }
    val finishedStrokesLeft = remember {
        mutableStateOf<List<Stroke>>(emptyList())
    }

    fun toggleCard(currentCard: String) {
        expandedCard.forEach { (card, state) ->
            if (card == currentCard) {
                state.value = !state.value
            } else {
                expandedCard.getValue(card).value = false
            }
        }
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
            clientViewModel,
            { clientViewModel.clearAllStates() },
            clientSelectedIndex,
            listOf("Uusi Asiakas", "Valitse Asiakas"),
            clientOk,
            expandClientCard,
            ::toggleCard
        )

        LaunchedEffect(clientSelectedIndex.intValue) {
            if (clientSelectedIndex.intValue == 1) {
                dogSelectedIndex.intValue = 1
            }
        }

        val dogOk =
            dogViewModel.dogNameState.text.isNotBlank() && dogViewModel.dogBreedState.text.isNotBlank() && dogState.dateOfBirth > 0

        DogCreationOrSelection(
            dogViewModel,
            clientViewModel,
            { dogViewModel.clearAllStates() },
            dogSelectedIndex,
            listOf("Uusi Koira", "Valitse Koira"),
            dogOk,
            expandDogCard,
            ::toggleCard
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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tallennus")
                    IconButton(onClick = {
                        toggleCard("Tallennus")
                    }) {
                        if (expandSavingCard.value) {
                            Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Close")
                        } else {
                            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "open")
                        }
                    }
                }

                if (expandSavingCard.value) {
                    CheckBoxRow(
                        "Tallenna käyntiyhteenveto PDF-tiedostona",
                        settingsState.createReport,
                        settingsViewModel::setCreateReport
                    )
                    CheckBoxRow(
                        "Tallenna kuitti PDF-tiedostona",
                        settingsState.createReceipt,
                        settingsViewModel::setCreateReceipt
                    )
                    CheckBoxRow(
                        "Lähetä tiedostot asiakkaan sähköpostiin",
                        settingsState.sendReportEmail,
                        settingsViewModel::setSendFilesToClient
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            settingsViewModel.paymentMethod,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Maksutapa") },
                            trailingIcon = {
                                IconButton(onClick = { openPaymentMenu = !openPaymentMenu }) {
                                    if (openPaymentMenu) {
                                        Icon(
                                            Icons.Rounded.KeyboardArrowUp,
                                            contentDescription = "Close"
                                        )

                                    } else {
                                        Icon(
                                            Icons.Rounded.KeyboardArrowDown,
                                            contentDescription = "Open"
                                        )

                                    }
                                }
                            })
                        DropdownMenu(
                            expanded = openPaymentMenu,
                            onDismissRequest = { openPaymentMenu = false },
                        ) {
                            settingsState.paymentMethods.forEach { paymentMethod ->
                                DropdownMenuItem({ Text(paymentMethod) }, onClick = {
                                    settingsViewModel.paymentMethod.edit {
                                        replace(0, length, paymentMethod)
                                        openPaymentMenu = false
                                    }
                                })
                            }
                        }
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Tallennetaan...", Toast.LENGTH_SHORT).show()

                            CoroutineScope(Dispatchers.IO).launch {
                                if (clientState.selectedClientId == null) {
                                    val (dogId, clientId) = dogViewModel.saveDog(clientViewModel.saveClient())
                                    massageViewModel.saveMassage(dogId, clientId)

                                } else if (dogState.selectedDogId == null) {
                                    val (dogId, clientId) = dogViewModel.saveDog(clientState.selectedClientId!!)
                                    massageViewModel.saveMassage(dogId, clientId)

                                } else {
                                    massageViewModel.saveMassage(
                                        dogState.selectedDogId!!, clientState.selectedClientId!!
                                    )
                                }

                                var reportUri: Uri? = null
                                var receiptUri: Uri? = null


                                if (settingsState.createReport) {
                                    val uriResult = massageViewModel.generatePdfReport(
                                        context,
                                        settingsViewModel.saveDirectory.text.toString().toUri(),
                                        clientViewModel.clientNameState.text.toString(),
                                        clientViewModel.clientEmailState.text.toString(),
                                        dogViewModel.dogNameState.text.toString(),
                                        dogViewModel.dogBreedState.text.toString(),
                                        DateFormat.getDateInstance(2, Locale.GERMANY)
                                            .format(dogState.dateOfBirth),
                                        dogViewModel.longTermIllnessState.text.toString(),
                                        dogViewModel.hobbiesState.text.toString(),
                                    )

                                    reportUri = uriResult.getOrNull()
                                }

                                if (settingsState.createReceipt) {
                                    val uriResult = settingsViewModel.generatePdfReceipt(
                                        context,
                                        massageState.massageId,
                                        massageViewModel.visitDateTextState.text.toString()
                                    )

                                    receiptUri = uriResult.getOrNull()
                                }

                                if (reportUri == null || receiptUri == null) {
                                    println("fucke dup")
                                    showFailedFile = true
                                } else {
                                    if (settingsState.sendReportEmail) {
                                        massageViewModel.sendReportEmail(
                                            context,
                                            clientViewModel.clientEmailState.text.toString(),
                                            settingsViewModel.emailSubject.text.toString(),
                                            settingsViewModel.emailBody.text.toString(),
                                            reportUri,
                                            receiptUri,
                                        )
                                    }
                                }

                                finishedStrokesRight.value = emptyList()
                                finishedStrokesLeft.value = emptyList()

                                dogViewModel.clearAllStates()
                                clientViewModel.clearAllStates()
                                massageViewModel.clearAllStates()
                                settingsViewModel.clearStates()

                                expandClientCard.value = true
                                expandDogCard.value = false
                                expandMassageCard.value = false
                                expandSavingCard.value = false
                                clientSelectedIndex.intValue = 0
                                dogSelectedIndex.intValue = 0
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = dogOk && clientOk && massageOk && settingsViewModel.paymentMethod.text.isNotBlank()
                    ) {
                        Text("Tallenna")
                    }
                }
            }
        }

        if (showFailedFile) {
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
                        showFailedFile = false
                    }) {
                    Text("OK")
                }
            })
        }
    }
}
