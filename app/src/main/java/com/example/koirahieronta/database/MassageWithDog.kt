package com.example.koirahieronta.database

import androidx.room3.Embedded
import androidx.room3.Relation

data class MassageWithDog(
    @Embedded val massage: Massage,
    @Relation(
        parentColumns = ["massagedDogId"],
        entityColumns = ["dogId"]
    )
    val dog: Dog
)
