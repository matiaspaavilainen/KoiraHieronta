package com.example.koirahieronta.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.koirahieronta.components.CheckBoxRow
import com.example.koirahieronta.components.EditableTextField
import com.example.koirahieronta.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen() {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val expandUserInfoCard = remember { mutableStateOf(false) }
    val expandPreferenceCard = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(8.dp)
            .verticalScroll(remember { ScrollState(0) }),

        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserInfoCard(expandUserInfoCard, settingsViewModel)
        UserPreferences(expandPreferenceCard, settingsViewModel)
    }
}


@Composable
fun UserInfoCard(
    expandUserInfoCard: MutableState<Boolean>, settingsViewModel: SettingsViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp, 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Käyttäjä")
                IconButton(onClick = {
                    expandUserInfoCard.value = !expandUserInfoCard.value
                }) {
                    if (expandUserInfoCard.value) {
                        Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Close")
                    } else {
                        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "open")
                    }
                }
            }

            AnimatedVisibility(expandUserInfoCard.value) {
                Column {
                    EditableTextField(
                        settingsViewModel.userName,
                        "Nimi",
                        multiLine = false,
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    )
                    OutlinedTextField(
                        state = settingsViewModel.userEmail,
                        modifier = Modifier.fillMaxWidth(),
                        lineLimits = TextFieldLineLimits.SingleLine,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            capitalization = KeyboardCapitalization.None,
                            showKeyboardOnFocus = true,
                            keyboardType = KeyboardType.Email
                        ),
                        label = { Text("Sähköposti") },
                        isError = settingsViewModel.emailHasErrors,
                    )
                    OutlinedTextField(
                        state = settingsViewModel.userPhoneNum,
                        modifier = Modifier.fillMaxWidth(),
                        lineLimits = TextFieldLineLimits.SingleLine,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            capitalization = KeyboardCapitalization.None,
                            showKeyboardOnFocus = true,
                            keyboardType = KeyboardType.Number
                        ),
                        label = { Text("Puhelinnumero") },
                    )

                    EditableTextField(
                        settingsViewModel.userYCode,
                        "Y-tunnus",
                        multiLine = false,
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.None
                    )

                    Button(
                        onClick = {
                            settingsViewModel.updateSettings()
                            expandUserInfoCard.value = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = settingsViewModel.userName.text.isNotBlank() && settingsViewModel.userEmail.text.isNotBlank() && settingsViewModel.userPhoneNum.text.isNotBlank()
                    ) {
                        Text("Tallenna")
                    }
                }
            }
        }
    }
}

@Composable
fun UserPreferences(
    expandPreferenceCard: MutableState<Boolean>, settingsViewModel: SettingsViewModel
) {
    val settingsState by settingsViewModel.state.collectAsState()

    val context = LocalContext.current

    val directoryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            val flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

            context.contentResolver.takePersistableUriPermission(
                uri, flags
            )
            settingsViewModel.setSaveDirectory(uri)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp, 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Oletusvalinnat")
                IconButton(onClick = {
                    expandPreferenceCard.value = !expandPreferenceCard.value
                }) {
                    if (expandPreferenceCard.value) {
                        Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Close")
                    } else {
                        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "open")
                    }
                }
            }

            AnimatedVisibility(expandPreferenceCard.value) {
                Column {
                    OutlinedTextField(
                        settingsViewModel.saveDirectory,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tiedostojen tallennuskansio") },
                        trailingIcon = {
                            IconButton(onClick = { directoryPicker.launch(null) }) {
                                Icon(
                                    Icons.Rounded.FolderOpen, ""
                                )
                            }
                        })

                    OutlinedTextField(
                        state = settingsViewModel.unitPrice,
                        modifier = Modifier.fillMaxWidth(),
                        lineLimits = TextFieldLineLimits.SingleLine,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            capitalization = KeyboardCapitalization.None,
                            showKeyboardOnFocus = true,
                            keyboardType = KeyboardType.Number
                        ),
                        label = { Text("Yksikköhinta") },
                    )

                    EditableTextField(
                        settingsViewModel.additionalInfo, "Maksun lisätiedot"
                    )

                    EditableTextField(
                        settingsViewModel.instructions, "Jatko-ohjeet"
                    )

                    EditableTextField(
                        settingsViewModel.emailSubject, "Sähköpostin aihe", multiLine = false
                    )

                    EditableTextField(
                        settingsViewModel.emailBody, "Sähköpostin teksti"
                    )

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
                        "Lahetä tiedostot asiakkaan sähköpostiin",
                        settingsState.sendReportEmail,
                        settingsViewModel::setSendFilesToClient
                    )


                    Button(
                        onClick = {
                            settingsViewModel.updateSettings()
                            expandPreferenceCard.value = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Tallenna")
                    }
                }
            }
        }
    }
}