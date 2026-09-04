package com.example.koirahieronta.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.koirahieronta.data.ClientRepository
import com.example.koirahieronta.database.Client
import com.example.koirahieronta.database.ClientWithDogs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ClientState())
    val clientNameState = TextFieldState()
    val clientEmailState = TextFieldState()
    val selectedClientName = TextFieldState(clientNameState.text.toString())
    private val _selectedClientWithDogs = MutableStateFlow<ClientWithDogs?>(null)
    val selectedClientWithDogs: StateFlow<ClientWithDogs?> = _selectedClientWithDogs.asStateFlow()
    private val _clients = clientRepository.getAllClients()
    val emailHasErrors by derivedStateOf {
        if (clientEmailState.text.isNotEmpty()) {
            // Email is considered erroneous until it completely matches EMAIL_ADDRESS.
            !android.util.Patterns.EMAIL_ADDRESS.matcher(clientEmailState.text).matches()
        } else {
            false
        }
    }

    val state = combine(_state, _clients) { state, clients ->
        state.copy(
            clients = clients
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ClientState())

    suspend fun saveClient(): Long {
        val clientName = clientNameState.text.toString()
        val clientEmail = clientEmailState.text.toString()

        val clientId = clientRepository.createClient(clientName, clientEmail)

        return clientId
    }

    fun setClientWithDogs(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedClientWithDogs.value = clientRepository.getClientWithDogs(id)
        }
    }

    fun setSelectedClientId(id: Long) {
        _state.update {
            it.copy(
                selectedClientId = id
            )
        }
    }

    fun clearAllStates() {
        _state.update {
            it.copy(
                selectedClientId = null
            )
        }
        selectedClientName.clearText()
        _selectedClientWithDogs.value = null
        clientNameState.clearText()
        clientEmailState.clearText()
    }

    fun getClientName(id: Long): String {
        return clientRepository.getClientName(id)
    }

    fun deleteClient(client: Client) {
        clientRepository.deleteClient(client)
    }

    fun updateClient(client: Client) {
        viewModelScope.launch { clientRepository.updateClient(client) }
    }
}

