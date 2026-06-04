package com.daniel.boardgametracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun BoardGameBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val bgColor = MaterialTheme.colorScheme.background
    val hexColor = Color.White.copy(alpha = 0.045f)

    Box(modifier = modifier.background(bgColor)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val hexRadius = 50f * density
            val strokePx = 0.9f * density
            drawHexGrid(hexColor, hexRadius, strokePx)
        }
        // Surface sets LocalContentColor so Text without an explicit color
        // inherits onBackground (white in dark theme) rather than the system default.
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Box { content() }
        }
    }
}

private fun DrawScope.drawHexGrid(color: Color, hexRadius: Float, strokeWidth: Float) {
    val hexH = hexRadius * sqrt(3f)
    val colStep = hexRadius * 1.5f
    val cols = (size.width / colStep).toInt() + 2
    val rows = (size.height / hexH).toInt() + 2

    for (col in -1..cols) {
        for (row in -1..rows) {
            val cx = col * colStep
            val cy = row * hexH + if (col % 2 != 0) hexH * 0.5f else 0f
            drawHex(cx, cy, hexRadius, color, strokeWidth)
        }
    }
}

private fun DrawScope.drawHex(cx: Float, cy: Float, r: Float, color: Color, strokeWidth: Float) {
    val path = Path()
    for (i in 0 until 6) {
        val angle = (PI / 3.0 * i).toFloat()
        val x = cx + r * cos(angle)
        val y = cy + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = color, style = Stroke(width = strokeWidth))
}
