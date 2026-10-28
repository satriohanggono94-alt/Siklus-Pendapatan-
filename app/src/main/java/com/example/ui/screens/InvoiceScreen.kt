package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvoiceWithDetails
import com.example.ui.components.CycleStatusBadge
import com.example.ui.components.formatDateShort
import com.example.ui.components.formatRupiah
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyPrimary

@Composable
fun InvoiceScreen(
    invoices: List<InvoiceWithDetails>,
    totalReceivables: Double,
    onInvoiceClick: (InvoiceWithDetails) -> Unit,
    onPayInvoiceClick: (InvoiceWithDetails) -> Unit
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filters = listOf("Semua", "Belum Lunas", "Sebagian", "Lunas")

    val filteredInvoices = remember(invoices, selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> invoices.filter { it.invoice.status == "UNPAID" }
            2 -> invoices.filter { it.invoice.status == "PARTIALLY_PAID" }
            3 -> invoices.filter { it.invoice.status == "PAID" }
            else -> invoices
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Module Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = "3.0 Pencatatan Faktur & Piutang Usaha",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = "Penerbitan faktur penjualan berdasarkan Bill of Lading, posting otomatis ke Sistem Buku Besar (GL).",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Summary Bar Piutang
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Piutang Belum Tertagih", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = formatRupiah(totalReceivables),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (totalReceivables > 0) CrimsonDanger else EmeraldSuccess
                    )
                }

                Text(
                    text = "${invoices.count { it.invoice.status != "PAID" }} Faktur Terbuka",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyPrimary
                )
            }
        }

        // Filter Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedFilterIndex,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            filters.forEachIndexed { index, title ->
                Tab(
                    selected = selectedFilterIndex == index,
                    onClick = { selectedFilterIndex = index },
                    text = {
                        val count = when (index) {
                            1 -> invoices.count { it.invoice.status == "UNPAID" }
                            2 -> invoices.count { it.invoice.status == "PARTIALLY_PAID" }
                            3 -> invoices.count { it.invoice.status == "PAID" }
                            else -> invoices.size
                        }
                        Text("$title ($count)", fontSize = 12.sp, fontWeight = if (selectedFilterIndex == index) FontWeight.Bold else FontWeight.Normal)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredInvoices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tidak ada faktur dalam status ini",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredInvoices, key = { it.invoice.id }) { invoiceDetail ->
                    val invoice = invoiceDetail.invoice

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onInvoiceClick(invoiceDetail) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = invoice.invoiceNumber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = NavyPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CycleStatusBadge(status = invoice.status)
                                }

                                Text(
                                    text = formatRupiah(invoice.totalAmount),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = NavyPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = invoice.customerName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Tgl: ${formatDateShort(invoice.invoiceDate)} | Termin: ${invoice.paymentTerms}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "Jatuh Tempo: ${formatDateShort(invoice.dueDate)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (invoice.balanceDue > 0) CrimsonDanger else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (invoice.balanceDue > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Terbayar: ${formatRupiah(invoice.paidAmount)}",
                                        fontSize = 11.sp,
                                        color = EmeraldSuccess
                                    )
                                    Text(
                                        text = "Sisa Piutang: ${formatRupiah(invoice.balanceDue)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CrimsonDanger
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { onInvoiceClick(invoiceDetail) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Faktur Lengkap", fontSize = 11.sp)
                                }

                                if (invoice.status != "PAID") {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { onPayInvoiceClick(invoiceDetail) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                                    ) {
                                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Terima Pembayaran", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
