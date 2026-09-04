package com.example.koirahieronta.viewmodels

import com.example.koirahieronta.database.ClientWithDogs

data class ClientState(
    val clients: List<ClientWithDogs> = emptyList(), val selectedClientId: Long? = null
)
