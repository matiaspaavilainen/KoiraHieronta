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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.koirahieronta.viewmodels.ClientViewModel

@Composable
fun ExistingClientSelection(
    clientViewModel: ClientViewModel,
) {
    val clientState by clientViewModel.state.collectAsState()
    var openClientMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                clientViewModel.selectedClientName,
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text("Asiakas") },
                trailingIcon = {
                    IconButton(onClick = { openClientMenu = !openClientMenu }) {
                        if (openClientMenu) {
                            Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Close")

                        } else {
                            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "Open")

                        }
                    }
                })
            DropdownMenu(
                expanded = openClientMenu,
                onDismissRequest = { openClientMenu = false },
            ) {
                clientState.clients.forEach { (client) ->
                    DropdownMenuItem({ Text(client.clientName) }, onClick = {
                        clientViewModel.setSelectedClientId(client.clientId)
                        clientViewModel.selectedClientName.edit {
                            replace(0, length, client.clientName)
                            openClientMenu = false
                        }
                        clientViewModel.clientNameState.edit {
                            replace(
                                0,
                                length,
                                client.clientName
                            )
                        }
                        clientViewModel.clientEmailState.edit {
                            replace(
                                0,
                                length,
                                client.clientEmail
                            )
                        }
                    })
                }
            }
        }
    }
}