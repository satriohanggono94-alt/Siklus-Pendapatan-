package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyPrimary

@Composable
fun DiagramViewerScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Penjelasan Siklus (SIA)", "Bagan Alir Buku Asli")

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = "Bagan Alir Siklus Pendapatan",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = "Bab 12 Siklus Pendapatan: Penjualan & Penerimaan Kas (Marshall B. Romney & Paul J. Steinbart)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Arsitektur DFD Tingkat 0 & Entitas Terkait",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }

                // Step 1.0
                item {
                    DiagramStepCard(
                        step = "1.0 Entri Pesanan Penjualan",
                        subtitle = "Sales Order Entry",
                        icon = Icons.Default.ShoppingCart,
                        iconColor = NavyPrimary,
                        inputs = listOf("Permintaan dari Pelanggan", "Pesanan Pelanggan", "Informasi ketersediaan barang dari Persediaan"),
                        outputs = listOf("Pesanan Penjualan ke Pelanggan", "Pesanan Penjualan ke 2.0 Pengiriman", "Kebutuhan Produksi/Pembelian (Back Order)", "Komisi ke Siklus Manajemen SDM/Penggajian")
                    )
                }

                // Step 2.0
                item {
                    DiagramStepCard(
                        step = "2.0 Pengiriman Barang",
                        subtitle = "Shipping & Logistics",
                        icon = Icons.Default.LocalShipping,
                        iconColor = BlueAccent,
                        inputs = listOf("Pesanan Penjualan dari 1.0 Entri Pesanan", "Pengambilan fisik barang dari Persediaan"),
                        outputs = listOf("Slip Pengepakan (Packing Slip) ke Kurir & Pelanggan", "Bill of Lading (Konosemen) ke Kurir & Pelanggan", "Bill of Lading ke 3.0 Penagihan")
                    )
                }

                // Step 3.0
                item {
                    DiagramStepCard(
                        step = "3.0 Penagihan / Faktur",
                        subtitle = "Billing & Accounts Receivable",
                        icon = Icons.Default.Receipt,
                        iconColor = Color(0xFFD97706),
                        inputs = listOf("Bill of Lading dari 2.0 Pengiriman", "Data Pesanan Penjualan"),
                        outputs = listOf("Faktur Penjualan (Sales Invoice) ke Pelanggan", "Pencatatan Penjualan & Piutang ke Sistem Buku Besar dan Pelaporan")
                    )
                }

                // Step 4.0
                item {
                    DiagramStepCard(
                        step = "4.0 Penerimaan Kas",
                        subtitle = "Cash Receipts & Bank Reconciliation",
                        icon = Icons.Default.Payments,
                        iconColor = EmeraldSuccess,
                        inputs = listOf("Pembayaran dari Pelanggan (Transfer, Tunai, Cek)"),
                        outputs = listOf("Tanda Terima Kas (Kuitansi) ke Pelanggan", "Setoran Kas ke Bank", "Laporan Rekonsiliasi Bank", "Update Jurnal Kas ke Sistem Buku Besar (GL)")
                    )
                }

                // Connected Cycles
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Integrasi Antar Siklus Perusahaan:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• Bank: Menerima setoran kas dan mengirim laporan rekening koran", fontSize = 11.sp)
                            Text("• Kurir: Menandatangani Bill of Lading dan mengantarkan barang", fontSize = 11.sp)
                            Text("• Siklus Pengeluaran & Produksi: Menerima pesanan ulang (Back Order) saat stok menipis", fontSize = 11.sp)
                            Text("• Siklus SDM / Penggajian: Menerima perhitungan komisi penjualan wiraniaga", fontSize = 11.sp)
                            Text("• Sistem Buku Besar & Pelaporan: Menerima entri jurnal penjualan dan penerimaan kas secara real-time", fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            // Tab 1: Menampilkan gambar asli yang diunggah pengguna
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Lampiran Gambar Asli Buku Teks Romney & Steinbart",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = "FIGUR 12-2: Diagram Konteks & FIGUR 12-3: DFD Tingkat 0 Siklus Pendapatan",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            AsyncImage(
                                model = "file:///android_asset/diagram_siklus_pendapatan.jpg",
                                contentDescription = "Bagan Alir Siklus Pendapatan Romney & Steinbart",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(550.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiagramStepCard(
    step: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    inputs: List<String>,
    outputs: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = step, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Arus Masuk (Inputs):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BlueAccent)
            inputs.forEach { input ->
                Text("← $input", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text("Arus Keluar (Outputs):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EmeraldSuccess)
            outputs.forEach { output ->
                Text("→ $output", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
