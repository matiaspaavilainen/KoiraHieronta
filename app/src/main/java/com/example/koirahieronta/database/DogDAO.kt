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
interface DogDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDog(dog: Dog): Long

    @Update
    suspend fun updateDog(dog: Dog)

    @Delete
    suspend fun deleteDog(dog: Dog)

    @Query("SELECT dogId FROM Dog WHERE name = :dogName AND dateOfBirth = :dateOfBirth")
    suspend fun getDogId(dogName: String, dateOfBirth: Long): Long

    @Transaction
    @Query("SELECT * FROM Dog ORDER BY name")
    fun getDogsWithMassages(): Flow<List<DogWithMassages>>
    @Transaction
    @Query("SELECT * FROM Dog WHERE dogId = :dogId")
    suspend fun getDogWithMassages(dogId: Long): DogWithMassages
}