package com.example.koirahieronta.data

import com.example.koirahieronta.database.Dog
import com.example.koirahieronta.database.DogDAO
import com.example.koirahieronta.database.DogWithMassages
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DogRepository @Inject constructor(
    private val dogDAO: DogDAO
) {
    fun getAllDogs(): Flow<List<DogWithMassages>> {
        return dogDAO.getDogsWithMassages()
    }

    suspend fun createDog(
        name: String,
        dateOfBirth: Long,
        breed: String,
        longTermIllness: String,
        hobbies: String,
        ownerId: Long
    ): Long {
        val dog = Dog(
            name = name,
            dateOfBirth = dateOfBirth,
            breed = breed,
            ownerId = ownerId,
            longTermIllness = longTermIllness,
            hobbies = hobbies,
        )

        var dogId = dogDAO.insertDog(dog)
        if (dogId == -1L) {
            dogId = dogDAO.getDogId(name, dateOfBirth)
        }

        return dogId
    }

    suspend fun deleteDog(dog: Dog) {
        dogDAO.deleteDog(dog)
    }

    suspend fun updateDog(dog: Dog) {
        dogDAO.updateDog(dog)
    }

    suspend fun getDogWithMassages(dogId: Long): DogWithMassages {
        return dogDAO.getDogWithMassages(dogId)
    }
}