package com.example.koirahieronta.data

import com.example.koirahieronta.database.Client
import com.example.koirahieronta.database.ClientDAO
import com.example.koirahieronta.database.ClientWithDogs
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClientRepository @Inject constructor(
    private val clientDAO: ClientDAO
) {
    fun getAllClients(): Flow<List<ClientWithDogs>> {
        return clientDAO.getClientsWithDogs()
    }

    suspend fun createClient(
        clientName: String, clientEmail: String
    ): Long {
        val client = Client(
            clientName, clientEmail
        )

        var clientId = clientDAO.insertClient(client)
        if (clientId == -1L) {
            clientId = clientDAO.getClientId(clientName)
        }

        return clientId
    }

    fun getClientWithDogs(id: Long): ClientWithDogs {
        return clientDAO.getClientWithDogs(id)
    }

    fun getClientName(id: Long): String {
        return clientDAO.getClient(id)
    }

    fun deleteClient(client: Client) {
        clientDAO.deleteClient(client)
    }

    suspend fun updateClient(client: Client) {
        clientDAO.updateClient(client)
    }
}