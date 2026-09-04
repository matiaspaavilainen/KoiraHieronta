package com.example.koirahieronta.viewmodels

import com.example.koirahieronta.database.DogWithMassages

data class DogState (
    val dogs: List<DogWithMassages> = emptyList(),
    val dateOfBirth: Long = 0L,
    val selectedDogId: Long? = null
)