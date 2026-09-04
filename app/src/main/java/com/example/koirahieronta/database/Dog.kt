package com.example.koirahieronta.database

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    indices = [Index(value = ["name", "dateOfBirth"], unique = true)]
)
data class Dog(
    val name: String,
    val dateOfBirth: Long,
    val breed: String,
    val ownerId: Long,
    // open text
    val longTermIllness: String?,
    val hobbies: String?,
    @PrimaryKey(autoGenerate = true) val dogId: Long = 0L,
)
