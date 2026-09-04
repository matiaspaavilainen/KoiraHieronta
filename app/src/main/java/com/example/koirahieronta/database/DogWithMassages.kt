package com.example.koirahieronta.database

import androidx.room3.Embedded
import androidx.room3.Relation

data class DogWithMassages(
    @Embedded val dog: Dog, @Relation(
        parentColumns = ["dogId"], entityColumns = ["massagedDogId"]
    ) val massages: List<Massage>
)