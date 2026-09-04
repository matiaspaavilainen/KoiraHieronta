package com.example.koirahieronta.database

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    indices = [
        Index(value = ["clientEmail"], unique = true)
    ]
)
data class Client(
    val clientName: String,
    val clientEmail: String,
    @PrimaryKey(autoGenerate = true) val clientId: Long = 0,
)
