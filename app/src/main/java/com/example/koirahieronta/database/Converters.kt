package com.example.koirahieronta.database

import androidx.ink.storage.decode
import androidx.ink.storage.encode
import androidx.ink.strokes.Stroke
import androidx.ink.strokes.StrokeInputBatch
import androidx.room3.ColumnTypeConverter
import com.example.koirahieronta.components.DrawingBrush
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

class Converters {
    @ColumnTypeConverter
    fun fromStrokes(strokes: List<Stroke>): ByteArray {
        val output = ByteArrayOutputStream()
        val data = DataOutputStream(output)

        data.writeInt(strokes.size)

        for (stroke in strokes) {
            val strokeOutput = ByteArrayOutputStream()

            stroke.inputs.encode(strokeOutput)

            val bytes = strokeOutput.toByteArray()
            data.writeInt(bytes.size)
            data.write(bytes)
        }

        return output.toByteArray()
    }

    @ColumnTypeConverter
    fun toStrokes(bytes: ByteArray): List<Stroke> {
        val data = DataInputStream(ByteArrayInputStream(bytes))
        val count = data.readInt()

        return buildList(count) {
            repeat(count) {
                val size = data.readInt()
                val strokeBytes = ByteArray(size)
                data.readFully(strokeBytes)

                val inputs = StrokeInputBatch.decode(
                    ByteArrayInputStream(strokeBytes)
                )
                add(Stroke(DrawingBrush.brush, inputs))
            }
        }
    }
}