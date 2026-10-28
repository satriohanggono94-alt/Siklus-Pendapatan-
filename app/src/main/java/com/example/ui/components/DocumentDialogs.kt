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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CashReceipt
import com.example.data.model.Customer
import com.example.data.model.InventoryItem
import com.example.data.model.InvoiceWithDetails
import com.example.data.model.SalesOrderWithItems
import com.example.data.model.Shipment
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueAccentLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.EmeraldSuccessLight
import com.example.ui.theme.NavyPrimary

// Company Header Component for Accounting Documents
@Composable
fun CompanyDocumentHeader(documentTitle: String, documentNumber: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PT NIAGA ARTHA NUSANTARA",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = "Distribusi & Solusi Bisnis Terpadu",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "NPWP: 01.889.324.7-034.000 | Tlp: (021) 5892-1100",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = documentTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyPrimary
                )
                Text(
                    text = documentNumber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    color = BlueAccent
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

// 1. Invoice Detail Dialog (Faktur Penjualan)
@Composable
fun InvoiceDetailDialog(
    invoiceDetail: InvoiceWithDetails,
    onDismiss: () -> Unit,
    onPayClick: (InvoiceWithDetails) -> Unit
) {
    val invoice = invoiceDetail.invoice
    val order = invoiceDetail.salesOrder

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                CompanyDocumentHeader(
                    documentTitle = "FAKTUR PENJUALAN",
                    documentNumber = invoice.invoiceNumber
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Pelanggan & Info Faktur
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Kepada Yth:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(invoice.customerName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Termin: ${invoice.paymentTerms}", fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Tgl Faktur: ${formatDateShort(invoice.invoiceDate)}", fontSize = 11.sp)
                        Text("Jatuh Tempo: ${formatDateShort(invoice.dueDate)}", fontSize = 11.sp, color = BlueAccent, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        CycleStatusBadge(status = invoice.status)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown Nilai Finansial
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal Penjualan", fontSize = 12.sp)
                            Text(formatRupiah(invoice.subtotal), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("PPN 11%", fontSize = 12.sp)
                            Text(formatRupiah(invoice.taxAmount), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Tagihan", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(formatRupiah(invoice.totalAmount), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Sudah Dibayar", fontSize = 12.sp, color = EmeraldSuccess)
                            Text(formatRupiah(invoice.paidAmount), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EmeraldSuccess)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Sisa Piutang (Saldo Tagihan)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(formatRupiah(invoice.balanceDue), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (invoice.balanceDue > 0) Color(0xFFDC2626) else EmeraldSuccess)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Info Akuntansi Sistem Buku Besar
                Text(
                    text = "Catatan Akuntansi: Faktur telah tercatat otomatis di Sistem Buku Besar (General Ledger): Debit Piutang Usaha Rp ${formatRupiah(invoice.totalAmount)}, Kredit Pendapatan Penjualan.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Tutup")
                    }
                    if (invoice.status != "PAID") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onDismiss()
                                onPayClick(invoiceDetail)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Terima Pembayaran")
                        }
                    }
                }
            }
        }
    }
}

// 2. Bill of Lading & Packing Slip Dialog (2.0 Pengiriman)
@Composable
fun BillOfLadingDialog(
    shipment: Shipment,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                CompanyDocumentHeader(
                    documentTitle = "BILL OF LADING (SURAT JALAN)",
                    documentNumber = shipment.billOfLadingNumber
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Slip Pengepakan: ${shipment.packingSlipNumber}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BlueAccent)
                Text("Nomor Pesanan Penjualan: ${shipment.orderNumber}", fontSize = 12.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Penerima / Tujuan:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(shipment.customerName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(shipment.destinationAddress, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Detail Ekspedisi / Kurir:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Kurir: ${shipment.courierName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("No. Resi / Kendaraan: ${shipment.trackingNumber}", fontSize = 12.sp)
                        Text("Tgl Pengiriman: ${formatDate(shipment.shipDate)}", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bagian Verifikasi Fisik Barang
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (shipment.isVerified) EmeraldSuccessLight else BlueAccentLight
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (shipment.isVerified) Icons.Default.Check else Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = if (shipment.isVerified) EmeraldSuccess else BlueAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (shipment.isVerified) "Pengiriman Telah Diverifikasi" else "Menunggu Verifikasi Gudang",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (shipment.isVerified) EmeraldSuccess else BlueAccent
                            )
                            Text(
                                text = if (shipment.isVerified) "Diverifikasi oleh: ${shipment.verifiedBy}" else "Periksa fisik barang sebelum serah terima kurir",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}

// 3. Cash Receipt Detail Dialog (4.0 Penerimaan Kas)
@Composable
fun CashReceiptDetailDialog(
    receipt: CashReceipt,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                CompanyDocumentHeader(
                    documentTitle = "TANDA TERIMA KAS",
                    documentNumber = receipt.receiptNumber
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Telah diterima dari:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(receipt.customerName, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSuccessLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Jumlah Pembayaran:", fontSize = 12.sp, color = EmeraldSuccess)
                        Text(
                            text = formatRupiah(receipt.amountPaid),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldSuccess
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Metode: ${receipt.paymentMethod.replace("_", " ")} | Ref: ${receipt.referenceNumber}",
                            fontSize = 11.sp,
                            color = Color(0xFF064E3B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column {
                    Text("Untuk Pembayaran Faktur: ${receipt.invoiceNumber}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Akun Bank Penerima: ${receipt.destinationBank}", fontSize = 12.sp)
                    Text("Nomor Bukti Setor Bank: ${receipt.bankDepositNumber}", fontSize = 12.sp)
                    Text("Waktu Transaksi: ${formatDate(receipt.receiptDate)}", fontSize = 12.sp)
                    if (receipt.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Catatan: ${receipt.notes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Konfirmasi Bank & Buku Besar: Kas telah disetor dan divalidasi ke rekening bank perusahaan. Saldo kas berjalan telah diperbarui otomatis.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}

// 4. Modal Form: Buat Pesanan Penjualan Baru (1.0 Entri Pesanan Penjualan)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSalesOrderDialog(
    customers: List<Customer>,
    inventoryItems: List<InventoryItem>,
    onDismiss: () -> Unit,
    onSubmit: (Customer, List<Pair<InventoryItem, Int>>, String, String) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }
    var salespersonName by remember { mutableStateOf("Budi Hartono") }
    var notes by remember { mutableStateOf("") }

    // Selected items map itemId -> qty
    val selectedItemQtys = remember { mutableStateListOf<Pair<InventoryItem, Int>>() }
    var itemToAdd by remember { mutableStateOf(inventoryItems.firstOrNull()) }
    var qtyToAddText by remember { mutableStateOf("1") }

    // Calculations
    val subtotal = selectedItemQtys.sumOf { it.first.unitPrice * it.second }
    val tax = subtotal * 0.11
    val total = subtotal + tax
    val commission = total * 0.03

    var customerDropdownExpanded by remember { mutableStateOf(false) }
    var itemDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Entri Pesanan Penjualan (1.0)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Text(
                    text = "Menerima pesanan pelanggan, cek ketersediaan stok, dan hitung komisi penjualan.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Pilih Pelanggan
                Text("Pilih Pelanggan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = customerDropdownExpanded,
                    onExpandedChange = { customerDropdownExpanded = !customerDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCustomer?.let { "${it.name} (${it.customerCode})" } ?: "Pilih Pelanggan",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = customerDropdownExpanded,
                        onDismissRequest = { customerDropdownExpanded = false }
                    ) {
                        customers.forEach { cust ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(cust.name, fontWeight = FontWeight.Bold)
                                        Text("Limit: ${formatRupiah(cust.creditLimit)} | Piutang: ${formatRupiah(cust.currentBalance)}", fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    selectedCustomer = cust
                                    customerDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tambah Item Barang
                Text("Pilih Barang Persediaan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    ExposedDropdownMenuBox(
                        expanded = itemDropdownExpanded,
                        onExpandedChange = { itemDropdownExpanded = !itemDropdownExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = itemToAdd?.let { "${it.name} (Stok: ${it.stockQuantity})" } ?: "Pilih Barang",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = itemDropdownExpanded,
                            onDismissRequest = { itemDropdownExpanded = false }
                        ) {
                            inventoryItems.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(item.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            Text("SKU: ${item.sku} | Stok: ${item.stockQuantity} ${item.unit} | ${formatRupiah(item.unitPrice)}", fontSize = 10.sp)
                                        }
                                    },
                                    onClick = {
                                        itemToAdd = item
                                        itemDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    OutlinedTextField(
                        value = qtyToAddText,
                        onValueChange = { qtyToAddText = it.filter { char -> char.isDigit() } },
                        label = { Text("Qty", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(70.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = {
                            val qty = qtyToAddText.toIntOrNull() ?: 1
                            itemToAdd?.let { item ->
                                if (qty > 0) {
                                    selectedItemQtys.add(Pair(item, qty))
                                    qtyToAddText = "1"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // List Items yang Ditambahkan
                if (selectedItemQtys.isNotEmpty()) {
                    Text("Daftar Item Pesanan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    selectedItemQtys.forEachIndexed { index, pair ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(pair.first.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("${pair.second} x ${formatRupiah(pair.first.unitPrice)} = ${formatRupiah(pair.first.unitPrice * pair.second)}", fontSize = 11.sp)
                                }
                                IconButton(
                                    onClick = { selectedItemQtys.removeAt(index) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Wiraniaga & Catatan
                OutlinedTextField(
                    value = salespersonName,
                    onValueChange = { salespersonName = it },
                    label = { Text("Nama Wiraniaga / Sales") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Pesanan") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Ringkasan Keuangan Pesanan
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", fontSize = 12.sp)
                            Text(formatRupiah(subtotal), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("PPN 11%", fontSize = 12.sp)
                            Text(formatRupiah(tax), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Pesanan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(formatRupiah(total), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Komisi Sales (3%) -> SDM", fontSize = 11.sp, color = BlueAccent)
                            Text(formatRupiah(commission), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BlueAccent)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val cust = selectedCustomer
                            if (cust != null && selectedItemQtys.isNotEmpty()) {
                                onSubmit(cust, selectedItemQtys, salespersonName, notes)
                                onDismiss()
                            }
                        },
                        enabled = selectedCustomer != null && selectedItemQtys.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text("Simpan Pesanan")
                    }
                }
            }
        }
    }
}

// 5. Modal Form: Verifikasi Pengiriman (2.0 Pengiriman)
@Composable
fun VerifyShipmentDialog(
    shipment: Shipment,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String, String, String) -> Unit
) {
    var verifierName by remember { mutableStateOf("Andi Wijaya (Supervisor Gudang)") }
    var courierName by remember { mutableStateOf(shipment.courierName.ifBlank { "JNE Express Logistik" }) }
    var trackingNumber by remember { mutableStateOf(shipment.trackingNumber.ifBlank { "JNE-${(100000..999999).random()}" }) }
    var notes by remember { mutableStateOf("Fisik barang sesuai pesanan penjualan, packing aman.") }
    var itemCheckConfirmed by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Verifikasi Pengiriman (2.0)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(shipment.orderNumber, fontSize = 12.sp, color = BlueAccent)
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "Lakukan verifikasi fisik barang di gudang sebelum menerbitkan Slip Pengepakan & Bill of Lading.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = verifierName,
                    onValueChange = { verifierName = it },
                    label = { Text("Petugas Pemeriksa Gudang") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = courierName,
                    onValueChange = { courierName = it },
                    label = { Text("Kurir / Ekspedisi") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = trackingNumber,
                    onValueChange = { trackingNumber = it },
                    label = { Text("Nomor Resi / No. Pol Truk") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Pengiriman") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = itemCheckConfirmed,
                        onCheckedChange = { itemCheckConfirmed = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Saya mengonfirmasi bahwa kuantitas barang fisik telah dicocokkan dengan Pesanan Penjualan dan siap diserahkan ke kurir.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(shipment.id, verifierName, courierName, trackingNumber, notes)
                    onDismiss()
                },
                enabled = itemCheckConfirmed && verifierName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
            ) {
                Text("Verifikasi & Terbitkan Dokumen")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

// 6. Modal Form: Catat Penerimaan Kas (4.0 Penerimaan Kas)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordCashReceiptDialog(
    unpaidInvoices: List<InvoiceWithDetails>,
    preselectedInvoiceId: Long? = null,
    onDismiss: () -> Unit,
    onSubmit: (Long, Double, String, String, String, String) -> Unit
) {
    var selectedInvoiceWithDetail by remember {
        mutableStateOf(
            if (preselectedInvoiceId != null) {
                unpaidInvoices.find { it.invoice.id == preselectedInvoiceId } ?: unpaidInvoices.firstOrNull()
            } else {
                unpaidInvoices.firstOrNull()
            }
        )
    }

    var invoiceDropdownExpanded by remember { mutableStateOf(false) }
    var amountPaidText by remember {
        mutableStateOf(
            selectedInvoiceWithDetail?.invoice?.balanceDue?.toLong()?.toString() ?: ""
        )
    }
    var paymentMethod by remember { mutableStateOf("BANK_TRANSFER") }
    var destinationBank by remember { mutableStateOf("BCA Giro Operasional (800-112-9900)") }
    var referenceNumber by remember { mutableStateOf("TRF-BCA-${(100000..999999).random()}") }
    var notes by remember { mutableStateOf("Pembayaran faktur penjualan via transfer bank") }

    val paymentMethods = listOf(
        "BANK_TRANSFER" to "Transfer Bank",
        "CASH" to "Kas Tunai / Kasir",
        "CHECK" to "Cek / Bilyet Giro",
        "QRIS" to "QRIS Dinamis"
    )

    val bankAccounts = listOf(
        "BCA Giro Operasional (800-112-9900)",
        "Mandiri Penjualan Bisnis (123-00-98210-1)",
        "Kas Kasir Perusahaan"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Catat Penerimaan Kas (4.0)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "Mencatat pelunasan faktur dari pelanggan dan memperbarui saldo kas real-time.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Pilih Faktur
                Text("Pilih Faktur Tagihan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = invoiceDropdownExpanded,
                    onExpandedChange = { invoiceDropdownExpanded = !invoiceDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedInvoiceWithDetail?.let {
                            "${it.invoice.invoiceNumber} - ${it.invoice.customerName} (Sisa: ${formatRupiah(it.invoice.balanceDue)})"
                        } ?: "Pilih Faktur Tagihan",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = invoiceDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = invoiceDropdownExpanded,
                        onDismissRequest = { invoiceDropdownExpanded = false }
                    ) {
                        unpaidInvoices.forEach { invDetail ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(invDetail.invoice.invoiceNumber, fontWeight = FontWeight.Bold)
                                        Text("${invDetail.invoice.customerName} | Sisa: ${formatRupiah(invDetail.invoice.balanceDue)}", fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    selectedInvoiceWithDetail = invDetail
                                    amountPaidText = invDetail.invoice.balanceDue.toLong().toString()
                                    invoiceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountPaidText,
                    onValueChange = { amountPaidText = it.filter { char -> char.isDigit() } },
                    label = { Text("Nominal Pembayaran (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Metode Pembayaran:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    paymentMethods.forEach { (code, label) ->
                        val isSelected = paymentMethod == code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { paymentMethod = code }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = destinationBank,
                    onValueChange = { destinationBank = it },
                    label = { Text("Rekening Bank / Kas Tujuan") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = referenceNumber,
                    onValueChange = { referenceNumber = it },
                    label = { Text("No. Referensi / Bukti Setor") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Keterangan") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val inv = selectedInvoiceWithDetail?.invoice
                    val amount = amountPaidText.toDoubleOrNull() ?: 0.0
                    if (inv != null && amount > 0.0) {
                        onSubmit(inv.id, amount, paymentMethod, destinationBank, referenceNumber, notes)
                        onDismiss()
                    }
                },
                enabled = selectedInvoiceWithDetail != null && (amountPaidText.toDoubleOrNull() ?: 0.0) > 0.0,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
            ) {
                Text("Konfirmasi & Setor Kas")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
