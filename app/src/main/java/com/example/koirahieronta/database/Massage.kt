package com.example.koirahieronta.database

import androidx.ink.strokes.Stroke
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity
data class Massage(
    val massagedDogId: Long,
    val massageClientId: Long,
    val visitDate: Long,
    val reasonForVisit: String?,
    val movement: String?,
    val currentHealth: String?,
    val structuralNotes: String?,
    val otherNotes: String?,
    val markedMusclesRight: List<Stroke>,
    val markedMusclesLeft: List<Stroke>,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)

