package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AmberWarningLight
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueAccentLight
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.CrimsonDangerLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EmeraldSuccessLight
import com.example.ui.theme.NavyPrimary

@Composable
fun CycleStatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "DRAFT" -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), "Draft")
        "APPROVED" -> Triple(BlueAccentLight, BlueAccent, "Disetujui")
        "BACKORDER" -> Triple(AmberWarningLight, AmberWarning, "Back Order")
        "SHIPPED" -> Triple(Color(0xFFEDE9FE), Color(0xFF7C3AED), "Terkirim")
        "DELIVERED" -> Triple(EmeraldSuccessLight, EmeraldSuccess, "Diterima")
        "COMPLETED" -> Triple(EmeraldSuccessLight, EmeraldSuccess, "Selesai")
        "UNPAID" -> Triple(CrimsonDangerLight, CrimsonDanger, "Belum Lunas")
        "PARTIALLY_PAID" -> Triple(AmberWarningLight, AmberWarning, "Sebagian")
        "PAID" -> Triple(EmeraldSuccessLight, EmeraldSuccess, "Lunas")
        "PENDING" -> Triple(AmberWarningLight, AmberWarning, "Menunggu")
        "VERIFIED" -> Triple(EmeraldSuccessLight, EmeraldSuccess, "Terverifikasi")
        else -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = subtext,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CycleFlowBar(
    orderCount: Int,
    pendingShipmentCount: Int,
    unpaidInvoiceCount: Int,
    cashReceivedCount: Int,
    onStepClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Alur Siklus Pendapatan (DFD Level 0)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sesuai Figur 12-2 & 12-3 Romney & Steinbart",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Step 1.0
                FlowStepItem(
                    stepNumber = "1.0",
                    title = "Entri Pesanan",
                    count = orderCount,
                    icon = Icons.Default.ShoppingCart,
                    activeColor = NavyPrimary,
                    onClick = { onStepClick(1) },
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.outline
                )

                // Step 2.0
                FlowStepItem(
                    stepNumber = "2.0",
                    title = "Pengiriman",
                    count = pendingShipmentCount,
                    icon = Icons.Default.LocalShipping,
                    activeColor = BlueAccent,
                    isAlert = pendingShipmentCount > 0,
                    onClick = { onStepClick(2) },
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.outline
                )

                // Step 3.0
                FlowStepItem(
                    stepNumber = "3.0",
                    title = "Penagihan",
                    count = unpaidInvoiceCount,
                    icon = Icons.Default.Receipt,
                    activeColor = AmberWarning,
                    isAlert = unpaidInvoiceCount > 0,
                    onClick = { onStepClick(3) },
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.outline
                )

                // Step 4.0
                FlowStepItem(
                    stepNumber = "4.0",
                    title = "Penerimaan Kas",
                    count = cashReceivedCount,
                    icon = Icons.Default.Payments,
                    activeColor = EmeraldSuccess,
                    onClick = { onStepClick(4) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FlowStepItem(
    stepNumber: String,
    title: String,
    count: Int,
    icon: ImageVector,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAlert: Boolean = false
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(activeColor.copy(alpha = 0.12f))
                .border(
                    width = if (isAlert) 1.5.dp else 1.dp,
                    color = if (isAlert) AmberWarning else activeColor.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = activeColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stepNumber,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = activeColor
        )

        Text(
            text = title,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )

        Text(
            text = "$count item",
            fontSize = 9.sp,
            color = if (isAlert) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isAlert) FontWeight.Bold else FontWeight.Normal
        )
    }
}
