package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CashTransaction
import com.example.data.model.InvoiceWithDetails
import com.example.data.model.SalesOrderWithItems
import com.example.data.model.Shipment
import com.example.ui.components.CycleFlowBar
import com.example.ui.components.CycleStatusBadge
import com.example.ui.components.MetricCard
import com.example.ui.components.FinancialMetricsDashboardCard
import com.example.ui.components.DailyRevenuePoint
import com.example.ui.components.DailyRevenueTrendChart
import com.example.ui.components.formatDate
import com.example.ui.components.formatRupiah
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AmberWarningLight
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueAccentLight
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.CrimsonDangerLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EmeraldSuccessLight
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary

@Composable
fun DashboardScreen(
    cashBalance: Double,
    totalSales: Double,
    totalReceivables: Double,
    totalCommission: Double,
    orders: List<SalesOrderWithItems>,
    shipments: List<Shipment>,
    invoices: List<InvoiceWithDetails>,
    cashTransactions: List<CashTransaction>,
    dailyRevenueTrends: List<DailyRevenuePoint> = emptyList(),
    onNavigateToTab: (Int) -> Unit,
    onOpenNewOrder: () -> Unit,
    onOpenRecordCash: () -> Unit,
    onOpenDiagram: () -> Unit,
    onInvoiceClick: (InvoiceWithDetails) -> Unit
) {
    val pendingShipmentsCount = shipments.count { !it.isVerified }
    val unpaidInvoicesCount = invoices.count { it.invoice.status != "PAID" }
    val cashReceivedCount = cashTransactions.count { it.sourceModule == "PENERIMAAN_KAS" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card: Saldo Kas Real-Time & Real-Time Sync Indicator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NavyPrimary, NavyLight, Color(0xFF103B66))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SINKRONISASI REAL-TIME AKTIF",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccessLight,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable { onNavigateToTab(4) } // Cash Ledger Tab
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Buku Kas",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Saldo Kas Tersedia (Bank & Kas)",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = formatRupiah(cashBalance),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Rekonsiliasi Bank BCA & Mandiri otomatis dengan mutasi piutang",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Main Material 3 Financial Metrics Dashboard Card
        item {
            FinancialMetricsDashboardCard(
                totalSales = totalSales,
                totalSalesCount = orders.size,
                pendingReceipts = totalReceivables,
                pendingReceiptsCount = unpaidInvoicesCount,
                cashBalance = cashBalance,
                pendingShipmentsCount = pendingShipmentsCount,
                onSalesClick = { onNavigateToTab(1) },
                onPendingReceiptsClick = { onNavigateToTab(3) },
                onCashClick = { onNavigateToTab(4) }
            )
        }

        // Recharts-style Line Chart for 7-Day Revenue Trends
        item {
            DailyRevenueTrendChart(dataPoints = dailyRevenueTrends)
        }

        // Cycle Flow Bar (Interactive DFD Level 0 Romney & Steinbart)
        item {
            CycleFlowBar(
                orderCount = orders.size,
                pendingShipmentCount = pendingShipmentsCount,
                unpaidInvoiceCount = unpaidInvoicesCount,
                cashReceivedCount = cashReceivedCount,
                onStepClick = { step ->
                    when (step) {
                        1 -> onNavigateToTab(1) // Orders
                        2 -> onNavigateToTab(2) // Shipping
                        3 -> onNavigateToTab(3) // Invoices
                        4 -> onNavigateToTab(4) // Cash / Ledger
                    }
                }
            )
        }

        // 4 Key Financial Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Total Penjualan",
                        value = formatRupiah(totalSales),
                        subtext = "${orders.size} Pesanan",
                        icon = Icons.Default.ShoppingCart,
                        iconTint = BlueAccent,
                        iconBg = BlueAccentLight,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(1) }
                    )

                    MetricCard(
                        title = "Piutang Berjalan",
                        value = formatRupiah(totalReceivables),
                        subtext = "$unpaidInvoicesCount Belum Lunas",
                        icon = Icons.Default.Receipt,
                        iconTint = CrimsonDanger,
                        iconBg = CrimsonDangerLight,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(3) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Siap Dikirim",
                        value = "$pendingShipmentsCount Pesanan",
                        subtext = "Gudang & Kurir",
                        icon = Icons.Default.LocalShipping,
                        iconTint = AmberWarning,
                        iconBg = AmberWarningLight,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(2) }
                    )

                    MetricCard(
                        title = "Komisi Sales (SDM)",
                        value = formatRupiah(totalCommission),
                        subtext = "3% Omzet",
                        icon = Icons.Default.MonetizationOn,
                        iconTint = EmeraldSuccess,
                        iconBg = EmeraldSuccessLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Aksi Operasional Cepat",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenNewOrder,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Entri Pesanan", fontSize = 11.sp)
                        }

                        Button(
                            onClick = onOpenRecordCash,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Terima Kas", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onOpenDiagram,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buka Bagan Alir Asli (Figur 12-2 & 12-3)", fontSize = 11.sp)
                    }
                }
            }
        }

        // Recent Invoices / Activity Stream Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Faktur Penjualan Terbaru",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Lihat Semua (${invoices.size})",
                    fontSize = 12.sp,
                    color = BlueAccent,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToTab(3) }
                )
            }
        }

        // Recent Invoices List
        items(invoices.take(3)) { invoiceDetail ->
            val invoice = invoiceDetail.invoice
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onInvoiceClick(invoiceDetail) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = invoice.invoiceNumber,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            CycleStatusBadge(status = invoice.status)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = invoice.customerName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Jatuh Tempo: ${formatDate(invoice.dueDate)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatRupiah(invoice.totalAmount),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )

                        if (invoice.balanceDue > 0) {
                            Text(
                                text = "Sisa: ${formatRupiah(invoice.balanceDue)}",
                                fontSize = 11.sp,
                                color = CrimsonDanger,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "Lunas",
                                fontSize = 11.sp,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
