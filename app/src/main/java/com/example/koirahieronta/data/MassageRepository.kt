package com.example.koirahieronta.data

import androidx.ink.strokes.Stroke
import com.example.koirahieronta.database.Massage
import com.example.koirahieronta.database.MassageDAO
import com.example.koirahieronta.database.MassageWithDog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MassageRepository @Inject constructor(
    private val massageDAO: MassageDAO
) {
    fun getAllMassages(): Flow<List<MassageWithDog>> {
        return massageDAO.getMassagesWithDog()
    }

    suspend fun createMassage(
        massagedDogId: Long,
        massageClientId: Long,
        visitDate: Long,
        reasonForVisit: String,
        movement: String,
        currentHealth: String,
        structuralNotes: String,
        otherNotes: String,
        markedMusclesRight: List<Stroke>,
        markedMusclesLeft: List<Stroke>
    ): Long {
        val massage = Massage(
            massagedDogId = massagedDogId,
            massageClientId = massageClientId,
            visitDate = visitDate,
            reasonForVisit = reasonForVisit,
            movement = movement,
            currentHealth = currentHealth,
            structuralNotes = structuralNotes,
            otherNotes = otherNotes,
            markedMusclesRight = markedMusclesRight,
            markedMusclesLeft = markedMusclesLeft
        )
        return massageDAO.upsertMassage(massage)
    }

    suspend fun upsertMassage(massage: Massage) {
        massageDAO.upsertMassage(massage)
    }

    suspend fun deleteMassage(massage: Massage) {
        massageDAO.deleteMassage(massage)
    }
}