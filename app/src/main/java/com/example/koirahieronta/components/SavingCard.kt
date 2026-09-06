package com.example.koirahieronta.components

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.koirahieronta.viewmodels.ClientViewModel
import com.example.koirahieronta.viewmodels.DogViewModel
import com.example.koirahieronta.viewmodels.MassageViewModel
import com.example.koirahieronta.viewmodels.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SavingCard(
    clientViewModel: ClientViewModel,
    dogViewModel: DogViewModel,
    massageViewModel: MassageViewModel,
    settingsViewModel: SettingsViewModel,
    saveOk: Boolean,
    expandSavingCard: MutableState<Boolean>,
    showFailedFile: MutableState<Boolean>,
    toggleCard: (String) -> Unit,
    resetStates: () -> Unit,

    ) {
    val context = LocalContext.current
    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
    val dogState by dogViewModel.state.collectAsStateWithLifecycle()
    val clientState by clientViewModel.state.collectAsStateWithLifecycle()
    val massageState by massageViewModel.state.collectAsStateWithLifecycle()

    var openPaymentMenu by remember { mutableStateOf(false) }

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
                                        Icons.Rounded.KeyboardArrowUp, contentDescription = "Close"
                                    )

                                } else {
                                    Icon(
                                        Icons.Rounded.KeyboardArrowDown, contentDescription = "Open"
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
                        }.invokeOnCompletion {
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
                                    dogState.dateOfBirth,
                                    dogViewModel.longTermIllnessState.text.toString(),
                                    dogViewModel.hobbiesState.text.toString(),
                                )

                                reportUri = uriResult.getOrNull()
                            }

                            if (settingsState.createReceipt) {
                                val uriResult = settingsViewModel.generatePdfReceipt(
                                    context, massageState.massageId
                                )

                                receiptUri = uriResult.getOrNull()
                            }

                            if ((reportUri == null && settingsState.createReport) || (receiptUri == null && settingsState.createReceipt)) {
                                showFailedFile.value = true
                            } else {
                                if (settingsState.sendReportEmail) {
                                    settingsViewModel.sendReportEmail(
                                        context,
                                        clientViewModel.clientEmailState.text.toString(),
                                        listOf(reportUri, receiptUri)
                                    )
                                }
                            }

                            resetStates()
                            toggleCard("Asiakas")

                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = saveOk && (settingsViewModel.paymentMethod.text.isNotBlank() || !settingsState.createReceipt)
                ) {
                    Text("Tallenna")
                }
            }
        }
    }
}