package com.example.koirahieronta.viewmodels


import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.scale
import androidx.documentfile.provider.DocumentFile
import androidx.ink.strokes.Stroke
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.koirahieronta.components.createBitmapFromStrokes
import com.example.koirahieronta.data.MassageRepository
import com.example.koirahieronta.database.Massage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DateFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@HiltViewModel
class MassageViewModel @Inject constructor(
    private val massageRepository: MassageRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MassageState())
    private val df = DateFormat.getDateInstance(2, Locale.GERMANY)
    val reasonForVisitState = TextFieldState()
    val movementState = TextFieldState()
    val currentHealthState = TextFieldState()
    val structuralNotesState = TextFieldState()
    val otherNotesState = TextFieldState()
    val visitDateTextState = TextFieldState(
        df.format(Clock.System.now().toEpochMilliseconds())
    )
    private val _massages = massageRepository.getAllMassages()

    val state = combine(_state, _massages) { state, massages ->
        state.copy(
            massages = massages
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MassageState())


    fun saveMassage(massagedDogId: Long, massageClientId: Long) {
        val visitDate = _state.value.visitDate
        val reasonForVisit = reasonForVisitState.text.toString()
        val movement = movementState.text.toString()
        val currentHealth = currentHealthState.text.toString()
        val structuralNotes = structuralNotesState.text.toString()
        val otherNotes = otherNotesState.text.toString()
        val markedMusclesRight = _state.value.markedMusclesRight
        val markedMusclesLeft = _state.value.markedMusclesLeft

        viewModelScope.launch {

            val massageId = massageRepository.createMassage(
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

            _state.update {
                it.copy(
                    massageId = massageId
                )
            }
        }
    }

    fun updateMassage(massage: Massage) {
        viewModelScope.launch {
            massageRepository.upsertMassage(massage)
        }
        clearAllStates()
    }

    fun deleteMassage(massage: Massage) {
        viewModelScope.launch {
            massageRepository.deleteMassage(massage)
        }
    }

    fun setMuscleMarkings(finishedStrokesRight: List<Stroke>, finishedStrokesLeft: List<Stroke>) {
        _state.update {
            it.copy(
                markedMusclesRight = finishedStrokesRight, markedMusclesLeft = finishedStrokesLeft
            )
        }
    }

    fun setVisitDate(date: Long) {
        _state.update { it.copy(visitDate = date) }
    }

    fun setCanvasSize(size: IntSize) {
        _state.update { it.copy(canvasSize = size) }
    }

    fun clearAllStates() {
        reasonForVisitState.clearText()
        visitDateTextState.clearText()
        movementState.clearText()
        currentHealthState.clearText()
        structuralNotesState.clearText()
        otherNotesState.clearText()
        visitDateTextState.edit {
            append(
                df.format(Clock.System.now().toEpochMilliseconds())
            )
        }
        _state.update {
            it.copy(
                markedMusclesRight = emptyList(),
                markedMusclesLeft = emptyList(),
                visitDate = Clock.System.now().toEpochMilliseconds(),
                massageId = 0L
            )
        }
    }

    fun generatePdfReport(
        context: Context,
        directoryUri: Uri,
        clientName: String,
        clientEmail: String,
        dogName: String,
        dogBreed: String,
        dogDOB: Long,
        longTermIllness: String,
        hobbies: String,
    ): Result<Uri> {
        return runCatching {
            val resolver = context.contentResolver

            val directory = DocumentFile.fromTreeUri(context, directoryUri)
                ?: throw IOException("Could not access selected directory")

            if (!directory.canWrite()) {
                throw IOException("Selected directory is not writable")
            }

            val pdfDocument = PdfDocument()

            try {
                val pageInfo = PdfDocument.PageInfo.Builder(
                    595 * 2,
                    842 * 2,
                    1
                ).create()

                val page = pdfDocument.startPage(pageInfo)

                val paint = Paint().apply {
                    typeface = Typeface.MONOSPACE
                    textSize = 32f
                }

                var yOffset = 80f
                val xOffset = 80f

                fun addText(label: String, textContent: String) {
                    if (textContent.lines().size > 1) {
                        textContent.lines().forEachIndexed { index, line ->
                            if (index == 0) {
                                page.canvas.drawText(
                                    label + line, xOffset, yOffset, paint
                                )
                                yOffset += 40f
                            } else {
                                val fLine = StringBuilder(line).insert(0, "- ").toString()
                                page.canvas.drawText(
                                    fLine.padStart((label.length + fLine.length - 2), ' '),
                                    xOffset,
                                    yOffset,
                                    paint
                                )
                                yOffset += 40f
                            }
                        }
                    } else {
                        page.canvas.drawText(
                            label + textContent, xOffset, yOffset, paint
                        )
                        yOffset += 50f
                    }
                }

                addText("Päivämäärä: ", visitDateTextState.text.toString())

                val width = (1418 / 2.5f).toInt()
                val height = (886 / 2.5f).toInt()

                val bmpStrokeR = createBitmapFromStrokes(
                    context, _state.value.markedMusclesRight, true, _state.value.canvasSize
                ).scale(
                    width, height
                )
                val bmpStrokeL = createBitmapFromStrokes(
                    context, _state.value.markedMusclesLeft, false, _state.value.canvasSize
                ).scale(
                    width, height
                )

                paint.isAntiAlias = true

                page.canvas.drawBitmap(bmpStrokeR, 18f, yOffset, paint)
                page.canvas.drawBitmap(bmpStrokeL, width + 38f, yOffset, paint)

                paint.isAntiAlias = false

                yOffset += height + 80f

                addText("Omistajan nimi: ", clientName)
                addText("Sähköposti: ", clientEmail)

                yOffset += 40f

                addText("Koiran nimi: ", dogName)
                addText(
                    "Syntymäaika: ", DateFormat.getDateInstance(2, Locale.GERMANY).format(dogDOB)
                )
                addText("Rotu: ", dogBreed)
                addText("Perussairaudet: ", longTermIllness)

                yOffset += 40f

                addText("Hoitokäynnin syy: ", reasonForVisitState.text.toString())
                addText("Harrastukset ja liikunta: ", hobbies)
                addText("Terveydentila: ", currentHealthState.text.toString())
                addText("Liikkuminen: ", movementState.text.toString())
                addText("Huomiot rakenteesta: ", structuralNotesState.text.toString())
                addText("Muut huomiot: ", otherNotesState.text.toString())

                pdfDocument.finishPage(page)

                val fileName =
                    "hieronta_raportti_${_state.value.massageId}_${dogName}_${visitDateTextState.text}"

                val pdfFile = directory.createFile(
                    "application/pdf", fileName
                ) ?: throw IOException("Could not create PDF file")

                resolver.openOutputStream(pdfFile.uri)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                } ?: throw IOException("Could not open PDF output stream")

                pdfFile.uri
            } finally {
                pdfDocument.close()
            }
        }
    }

}