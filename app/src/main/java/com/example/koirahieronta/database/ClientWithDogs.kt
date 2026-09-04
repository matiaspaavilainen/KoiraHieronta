package com.example.koirahieronta.database

import androidx.room3.Embedded
import androidx.room3.Relation

data class ClientWithDogs(
    @Embedded val client: Client,
    @Relation(
        parentColumns = ["clientId"],
        entityColumns = ["ownerId"]
    )
    val dogs: List<Dog>
)
