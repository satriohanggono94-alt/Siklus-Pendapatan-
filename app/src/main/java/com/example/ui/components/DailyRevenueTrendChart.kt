package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EmeraldSuccessLight
import com.example.ui.theme.NavyPrimary

data class DailyRevenuePoint(
    val dayName: String,   // "Rab", "Kam", "Jum", etc.
    val dateLabel: String, // "22 Okt"
    val revenue: Double
)

/**
 * Recharts-style Native Compose Line Chart Component
 * Visualizes daily revenue trends over the last 7 days with cubic Bézier curve,
 * gradient fill, Cartesian grid lines, and interactive tap tooltip.
 */
@Composable
fun DailyRevenueTrendChart(
    dataPoints: List<DailyRevenuePoint>,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    val maxRevenue = remember(dataPoints) {
        (dataPoints.maxOfOrNull { it.revenue } ?: 10000000.0).coerceAtLeast(1000000.0)
    }
    val minRevenue = remember(dataPoints) {
        (dataPoints.minOfOrNull { it.revenue } ?: 0.0).coerceAtLeast(0.0)
    }
    val total7Days = remember(dataPoints) {
        dataPoints.sumOf { it.revenue }
    }
    val avgDaily = remember(dataPoints) {
        total7Days / dataPoints.size
    }

    var selectedPointIndex by remember {
        mutableStateOf<Int?>(dataPoints.size - 1) // default to today / last point
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header (Recharts Legend & Title)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BlueAccent.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Trend Pendapatan",
                            tint = BlueAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tren Pendapatan 7 Hari Terakhir",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "Visualisasi Harian (Gaya Recharts LineChart)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = EmeraldSuccessLight,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Total: ${formatRupiah(total7Days)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recharts-style Tooltip Card (Active data point detail)
            selectedPointIndex?.let { index ->
                if (index in dataPoints.indices) {
                    val pt = dataPoints[index]
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(BlueAccent)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${pt.dayName}, ${pt.dateLabel}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NavyPrimary
                                )
                            }
                            Text(
                                text = "Pendapatan: ${formatRupiah(pt.revenue)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlueAccent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Native Jetpack Compose Line Chart Canvas (Recharts-style Smooth Bezier Curve)
            val chartLineColor = BlueAccent
            val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .pointerInput(dataPoints) {
                            detectTapGestures { offset ->
                                val pointSpacing = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                                val tappedIndex = (offset.x / pointSpacing).toInt().coerceIn(0, dataPoints.size - 1)
                                selectedPointIndex = tappedIndex
                            }
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val bottomPadding = 30f // space for X-axis
                    val topPadding = 20f
                    val availableHeight = canvasHeight - topPadding - bottomPadding
                    val stepX = canvasWidth / (dataPoints.size - 1).coerceAtLeast(1)

                    // Draw 4 Horizontal Cartesian Grid Lines (Recharts style)
                    val gridLinesCount = 3
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    for (i in 0..gridLinesCount) {
                        val y = topPadding + (availableHeight / gridLinesCount) * i
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = dashEffect
                        )
                    }

                    // Compute Coordinates
                    val points = dataPoints.mapIndexed { index, point ->
                        val x = index * stepX
                        val range = (maxRevenue - minRevenue).coerceAtLeast(1.0)
                        val normalized = ((point.revenue - minRevenue) / range).toFloat()
                        val y = topPadding + availableHeight * (1f - normalized)
                        Offset(x, y)
                    }

                    // Build Smooth Cubic Bézier Curve Path
                    val linePath = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points[0].x, points[0].y)
                            for (i in 0 until points.size - 1) {
                                val p0 = points[i]
                                val p1 = points[i + 1]
                                val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                                val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)
                                cubicTo(
                                    controlPoint1.x, controlPoint1.y,
                                    controlPoint2.x, controlPoint2.y,
                                    p1.x, p1.y
                                )
                            }
                        }
                    }

                    // Build Gradient Area Fill Path (Recharts AreaChart effect)
                    val fillPath = Path().apply {
                        addPath(linePath)
                        lineTo(points.last().x, topPadding + availableHeight)
                        lineTo(points.first().x, topPadding + availableHeight)
                        close()
                    }

                    // Draw Gradient Fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                chartLineColor.copy(alpha = 0.35f),
                                chartLineColor.copy(alpha = 0.02f)
                            ),
                            startY = topPadding,
                            endY = topPadding + availableHeight
                        )
                    )

                    // Draw Smooth Line Stroke
                    drawPath(
                        path = linePath,
                        color = chartLineColor,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Draw Data Dots (Recharts Dot & Active Dot)
                    points.forEachIndexed { index, offset ->
                        val isSelected = index == selectedPointIndex
                        if (isSelected) {
                            // Active outer glow / pulse ring
                            drawCircle(
                                color = chartLineColor.copy(alpha = 0.25f),
                                radius = 9.dp.toPx(),
                                center = offset
                            )
                            // White border
                            drawCircle(
                                color = Color.White,
                                radius = 6.dp.toPx(),
                                center = offset
                            )
                            // Solid core
                            drawCircle(
                                color = chartLineColor,
                                radius = 4.dp.toPx(),
                                center = offset
                            )
                        } else {
                            // Normal dot
                            drawCircle(
                                color = Color.White,
                                radius = 4.5.dp.toPx(),
                                center = offset
                            )
                            drawCircle(
                                color = chartLineColor,
                                radius = 3.dp.toPx(),
                                center = offset
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // X-Axis Day Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dataPoints.forEachIndexed { index, pt ->
                    val isSelected = index == selectedPointIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(36.dp)
                    ) {
                        Text(
                            text = pt.dayName,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BlueAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = pt.dateLabel.split(" ").firstOrNull() ?: "",
                            fontSize = 9.sp,
                            color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Insight Footnote
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ketuk titik grafik untuk melihat rincian per hari",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Rata-rata: ${formatRupiah(avgDaily)}/hari",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyPrimary
                )
            }
        }
    }
}
