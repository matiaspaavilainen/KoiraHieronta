package com.example.koirahieronta.database

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MassageDAO {

    @Upsert
    suspend fun upsertMassage(massage: Massage): Long

    @Delete
    suspend fun deleteMassage(massage: Massage)

    @Transaction
    @Query("SELECT * FROM Massage ORDER BY visitDate DESC")
    fun getMassagesWithDog(): Flow<List<MassageWithDog>>
}