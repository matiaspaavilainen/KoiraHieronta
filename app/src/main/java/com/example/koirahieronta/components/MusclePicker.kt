package com.example.koirahieronta.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.RotateLeft
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import androidx.ink.authoring.compose.InProgressStrokes
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke
import com.example.koirahieronta.R
import com.example.koirahieronta.viewmodels.MassageViewModel

@Composable
fun MusclePicker(
    massageViewModel: MassageViewModel,
    finishedStrokesRight: MutableState<List<Stroke>>,
    finishedStrokesLeft: MutableState<List<Stroke>>,
    drawingMode: MutableState<Boolean> = mutableStateOf(false),
    viewingMode: Boolean = false
) {
    val mirrored = remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .padding(0.dp, 8.dp, 0.dp, 4.dp)
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column {
            Text(
                if (mirrored.value) "Oikea sivu" else "Vasen sivu",
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            InkImageCanvas(
                finishedStrokesRight, finishedStrokesLeft, drawingMode, mirrored, massageViewModel
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                AnimatedVisibility(drawingMode.value) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        Button(
                            onClick = {
                                finishedStrokesRight.value = emptyList()
                                finishedStrokesLeft.value = emptyList()
                            },
                            Modifier.size(40.dp),
                            contentPadding = PaddingValues(0.dp),
                            shape = CircleShape,
                        ) {
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = "Delete all drawings",
                            )
                        }

                        Button(
                            onClick = {
                                if (mirrored.value) {
                                    finishedStrokesRight.value =
                                        finishedStrokesRight.value.dropLast(1)
                                } else {
                                    finishedStrokesLeft.value =
                                        finishedStrokesLeft.value.dropLast(1)
                                }
                            },
                            Modifier.size(40.dp),
                            contentPadding = PaddingValues(0.dp),
                            shape = CircleShape,
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.Undo,
                                contentDescription = "Undo",
                            )
                        }
                    }
                }

                if (!viewingMode) {
                    Button(
                        onClick = { drawingMode.value = !drawingMode.value },
                        Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = CircleShape,
                    ) {
                        if (drawingMode.value) {
                            Icon(Icons.Rounded.EditOff, contentDescription = "Exit drawing")
                        } else {
                            Icon(Icons.Rounded.Edit, contentDescription = "Drawing mode")
                        }
                    }
                }

                Button(
                    onClick = {
                        mirrored.value = !mirrored.value
                    },
                    Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = CircleShape,
                ) {
                    if (mirrored.value) {
                        Icon(
                            Icons.AutoMirrored.Rounded.RotateLeft,
                            contentDescription = "Flip image",
                        )
                    } else {
                        Icon(
                            Icons.AutoMirrored.Rounded.RotateRight,
                            contentDescription = "Flip image",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InkImageCanvas(
    finishedStrokesRight: MutableState<List<Stroke>>,
    finishedStrokesLeft: MutableState<List<Stroke>>,
    drawingMode: MutableState<Boolean> = mutableStateOf(false),
    mirrored: MutableState<Boolean>,
    massageViewModel: MassageViewModel
) {
    val brush = remember {
        DrawingBrush.brush
    }

    val renderer = remember {
        CanvasStrokeRenderer.create()
    }

    val finishedStrokes = if (mirrored.value) {
        finishedStrokesRight
    } else {
        finishedStrokesLeft
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1418f / 886f)
    ) {
        Image(
            painter = painterResource(R.drawable.koira_lihakset),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = if (mirrored.value) -1f else 1f
                },
            contentScale = ContentScale.FillBounds
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .onSizeChanged {
                    massageViewModel.setCanvasSize(it)
                }) {
            finishedStrokes.value.forEach { stroke ->
                renderer.draw(
                    stroke = stroke,
                    canvas = drawContext.canvas.nativeCanvas,
                    strokeToScreenTransform = Matrix()
                )
            }
        }

        if (drawingMode.value) {
            InProgressStrokes(
                defaultBrush = brush,
                onStrokesFinished = { strokes ->
                    finishedStrokes.value += strokes
                },
            )
        }
    }
}

fun createBitmapFromStrokes(
    context: Context, finishedStrokes: List<Stroke>, mirrored: Boolean, canvasSize: IntSize
): Bitmap {

    val options = BitmapFactory.Options().apply {
        inScaled = false
    }

    var sourceBitmap = BitmapFactory.decodeResource(
        context.resources, R.drawable.koira_lihakset, options
    )

    sourceBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)

    // Make the background exactly the same size as the Compose canvas
    var bitmap = Bitmap.createScaledBitmap(
        sourceBitmap, canvasSize.width, canvasSize.height, true
    )

    if (mirrored) {
        bitmap = bitmap.scale(-bitmap.width, bitmap.height)
    }

    val canvas = android.graphics.Canvas(bitmap)
    val renderer = CanvasStrokeRenderer.create()

    finishedStrokes.forEach { stroke ->
        renderer.draw(
            stroke = stroke, canvas = canvas, strokeToScreenTransform = Matrix()
        )
    }

    // Now convert the complete result to PDF resolution
    return bitmap.scale(1418, 886)
}
