package com.example.koirahieronta.viewmodels

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.koirahieronta.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DateFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    val userName = TextFieldState()
    val userEmail = TextFieldState()
    val userPhoneNum = TextFieldState()
    val userYCode = TextFieldState()
    val saveDirectory = TextFieldState()
    val unitPrice = TextFieldState()
    val additionalInfo = TextFieldState()
    val instructions = TextFieldState()
    val emailSubject = TextFieldState()
    val emailBody = TextFieldState()

    val paymentMethod = TextFieldState()

    val emailHasErrors by derivedStateOf {
        userEmail.text.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail.text)
            .matches()
    }

    private val df = DateFormat.getDateInstance(2, Locale.GERMANY)

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            repository.settings.collect { settings ->
                _state.update {
                    it.copy(
                        createReport = settings.createReport,
                        createReceipt = settings.createReceipt,
                        sendReportEmail = settings.sendReport,
                    )
                }

                userName.edit {
                    replace(0, length, settings.userName)
                }

                userEmail.edit {
                    replace(0, length, settings.userEmail)
                }

                userPhoneNum.edit {
                    replace(0, length, settings.userPhoneNum)
                }

                userYCode.edit {
                    replace(0, length, settings.userYCode)
                }

                saveDirectory.edit {
                    replace(0, length, settings.saveDirectory)
                }

                unitPrice.edit {
                    replace(0, length, settings.unitPrice.toString())
                }

                additionalInfo.edit {
                    replace(0, length, settings.additionalInfo)
                }

                instructions.edit {
                    replace(0, length, settings.instructions)
                }

                emailSubject.edit {
                    replace(0, length, settings.emailSubject)
                }

                emailBody.edit {
                    replace(0, length, settings.emailBody)
                }
            }
        }
    }

    fun updateSettings() {
        viewModelScope.launch {
            repository.updateSettings(
                userName = userName.text.toString(),
                userEmail = userEmail.text.toString(),
                userPhoneNum = userPhoneNum.text.toString(),
                userYCode = userYCode.text.toString(),
                saveDirectory = saveDirectory.text.toString(),

                unitPrice = unitPrice.text.toString().toDouble(),
                additionalInfo = additionalInfo.text.toString(),
                instructions = instructions.text.toString(),
                emailSubject = emailSubject.text.toString(),
                emailBody = emailBody.text.toString(),

                createReport = state.value.createReport,
                createReceipt = state.value.createReceipt,
                sendReport = state.value.sendReportEmail,
            )
        }
    }

    fun setCreateReport(value: Boolean) {
        _state.update {
            it.copy(
                createReport = value
            )
        }
    }

    fun setCreateReceipt(value: Boolean) {
        _state.update {
            it.copy(
                createReceipt = value
            )
        }
    }

    fun setSendFilesToClient(value: Boolean) {
        _state.update {
            it.copy(
                sendReportEmail = value
            )
        }
    }

    fun clearStates() {
        paymentMethod.edit { replace(0, length, "") }
    }

    fun setSaveDirectory(uri: Uri) {
        saveDirectory.edit { replace(0, length, uri.toString()) }
        updateSettings()
    }

    @OptIn(ExperimentalTime::class)
    fun generatePdfReceipt(
        context: Context, massageId: Long
    ): Result<Uri> {
        return runCatching {
            val resolver = context.contentResolver

            // Get the directory the user selected previously
            val directory = DocumentFile.fromTreeUri(context, saveDirectory.text.toString().toUri())
                ?: throw IOException("Could not access selected directory")

            if (!directory.canWrite()) {
                throw IOException("Selected directory is not writable")
            }

            // Create the PDF in memory
            val pdfDocument = PdfDocument()

            try {
                val pageInfo = PdfDocument.PageInfo.Builder(
                    595 * 2,  // A4-ish width
                    842 * 2,  // A4-ish height
                    1
                ).create()

                val page = pdfDocument.startPage(pageInfo)

                val paint = Paint().apply {
                    typeface = Typeface.MONOSPACE
                    textSize = 24f
                    isAntiAlias = true
                }

                val textPaint = TextPaint(paint)

                val marginLeft = 80f
                val marginRight = 80f
                val marginTop = 80f
                val marginBottom = 80f

                val pageWidth = pageInfo.pageWidth.toFloat()
                val pageHeight = pageInfo.pageHeight.toFloat()

                val textWidth = pageWidth - marginLeft - marginRight

                var currentY = marginTop

                fun wrapText(text: String) {
                    if (text.isBlank()) {
                        currentY += textPaint.textSize
                        return
                    }

                    var start = 0

                    while (start < text.length) {

                        // Layout the remaining text so Android determines where
                        // the lines should wrap.
                        val layout = StaticLayout.Builder.obtain(
                            text, start, text.length, textPaint, textWidth.toInt()
                        ).setAlignment(Layout.Alignment.ALIGN_NORMAL).setIncludePad(false).build()

                        // How much vertical space is available on this page?
                        val availableHeight = pageHeight - marginBottom - currentY

                        // Find the last line that fits.
                        var lastLine = -1

                        for (line in 0 until layout.lineCount) {
                            if (layout.getLineBottom(line) <= availableHeight) {
                                lastLine = line
                            } else {
                                break
                            }
                        }

                        // Character index immediately after the last line that fits.
                        val end = layout.getLineEnd(lastLine)

                        // Draw only the part that fits on this page.
                        val visibleLayout = StaticLayout.Builder.obtain(
                            text, start, end, textPaint, textWidth.toInt()
                        ).setAlignment(Layout.Alignment.ALIGN_NORMAL).setIncludePad(false).build()

                        page.canvas.save()

                        page.canvas.translate(
                            marginLeft, currentY
                        )

                        visibleLayout.draw(page.canvas)

                        page.canvas.restore()

                        // Move down by the height we actually drew.
                        currentY += visibleLayout.height

                        start = end
                    }
                }

                fun addText(label: String, textContent: String) {
                    page.canvas.drawText(
                        label + textContent, marginLeft, currentY, paint
                    )
                    currentY += 40f

                }

                addText("", "KUITTI")

                addText("", userName.text.toString())
                page.canvas.drawText(
                    "Kuitti nro: $massageId", 700f, currentY - 50f, paint
                )

                addText("Y-tunnus: ", userYCode.text.toString())
                page.canvas.drawText(
                    "Päivämäärä: ${df.format(Clock.System.now().toEpochMilliseconds())}",
                    700f,
                    currentY - 50f,
                    paint
                )

                addText("puh: ", userPhoneNum.text.toString())
                addText("", userEmail.text.toString())

                currentY += 50f
                page.canvas.drawLine(marginLeft, currentY, 1110f, currentY + 4f, paint)
                currentY += 32f

                page.canvas.drawText(
                    "Palvelu", marginLeft, currentY, paint
                )

                page.canvas.drawText(
                    "Määrä", marginLeft + 400, currentY, paint
                )

                page.canvas.drawText(
                    "Yksikköhinta", marginLeft + 257 + 300, currentY, paint
                )

                page.canvas.drawText(
                    "Summa", marginLeft + 257 * 2 + 300, currentY, paint
                )

                currentY += 10f

                page.canvas.drawLine(80f, currentY, 1110f, currentY + 4f, paint)
                currentY += 32f

                page.canvas.drawText(
                    "Koirahieronta 60 min", marginLeft, currentY, paint
                )

                page.canvas.drawText(
                    "1", marginLeft + 400, currentY, paint
                )

                val unitPriceText = unitPrice.text.toString() + " €"

                page.canvas.drawText(
                    unitPriceText, marginLeft + 257 + 300, currentY, paint
                )

                page.canvas.drawText(
                    unitPriceText, marginLeft + 257 * 2 + 300, currentY, paint
                )

                currentY += 50f
                page.canvas.drawLine(80f, currentY, 1110f, currentY + 4f, paint)
                currentY += 100f

                addText("Yhteensä: ", unitPriceText)
                addText("Maksutapa: ", paymentMethod.text.toString())
                addText("", "Lisätiedot: ")
                currentY -= 30f
                wrapText(additionalInfo.text.toString())

                currentY += 50f

                addText("", "Jatko-ohjeet: ")
                currentY -= 30f
                wrapText(instructions.text.toString())

                pdfDocument.finishPage(page)

                val fileName = "hieronta_kuitti_${massageId}_${
                    df.format(
                        Clock.System.now().toEpochMilliseconds()
                    )
                }"

                // Create the file directly in the selected directory
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

    fun sendReportEmail(
        context: Context, clientEmail: String, attachments: ArrayList<Uri?>
    ) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "application/pdf"

            putExtra(Intent.EXTRA_EMAIL, arrayOf(clientEmail))
            putExtra(Intent.EXTRA_SUBJECT, emailSubject.text.toString())
            putExtra(Intent.EXTRA_TEXT, emailBody.text.toString())

            putParcelableArrayListExtra(
                Intent.EXTRA_STREAM, attachments
            )

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val clipData = ClipData.newUri(
                context.contentResolver, "PDF attachments", attachments[0]
            )

            for (uri in attachments.drop(1)) {
                clipData.addItem(ClipData.Item(uri))
            }

            setClipData(clipData)
        }

        context.startActivity(
            Intent.createChooser(intent, "Lähetä sähköposti")
        )
    }
}