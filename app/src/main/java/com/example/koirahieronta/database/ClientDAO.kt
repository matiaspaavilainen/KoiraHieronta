package com.example.koirahieronta.database

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertClient(client: Client): Long

    @Update
    suspend fun updateClient(client: Client)

    @Query("SELECT clientId FROM Client WHERE clientEmail = :clientEmail")
    fun getClientId(clientEmail: String): Long

    @Query("SELECT * FROM Client WHERE clientId = :clientId")
    fun getClientWithDogs(clientId: Long): ClientWithDogs

    @Transaction
    @Query("SELECT * FROM Client ORDER BY clientName")
    fun getClientsWithDogs(): Flow<List<ClientWithDogs>>

    @Query("SELECT clientName FROM Client WHERE clientId = :id")
    fun getClient(id: Long): String

    @Delete
    fun deleteClient(client: Client)
}