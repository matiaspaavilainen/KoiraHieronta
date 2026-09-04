package com.example.koirahieronta.viewmodels

import androidx.compose.ui.unit.IntSize
import androidx.ink.strokes.Stroke
import com.example.koirahieronta.database.MassageWithDog
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class MassageState @OptIn(ExperimentalTime::class) constructor(
    val massages: List<MassageWithDog> = emptyList(),
    val visitDate: Long = Clock.System.now().toEpochMilliseconds(),
    val markedMusclesRight: List<Stroke> = emptyList(),
    val markedMusclesLeft: List<Stroke> = emptyList(),
    val massageId: Long = 0L,
    val canvasSize: IntSize = IntSize.Zero
)
