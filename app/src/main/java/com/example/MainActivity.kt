package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CashReceipt
import com.example.data.model.InvoiceWithDetails
import com.example.data.model.Shipment
import com.example.ui.components.BillOfLadingDialog
import com.example.ui.components.CashReceiptDetailDialog
import com.example.ui.components.InvoiceDetailDialog
import com.example.ui.components.NewSalesOrderDialog
import com.example.ui.components.RecordCashReceiptDialog
import com.example.ui.components.VerifyShipmentDialog
import com.example.ui.screens.CashLedgerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DiagramViewerScreen
import com.example.ui.screens.InvoiceScreen
import com.example.ui.screens.SalesOrderScreen
import com.example.ui.screens.ShippingScreen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import com.example.data.local.OwnerAuthManager
import com.example.ui.components.ChangePasswordDialog
import com.example.ui.screens.OwnerLoginScreen
import com.example.ui.theme.CrimsonDanger
import com.example.ui.theme.EmeraldSuccessLight
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.RevenueCycleViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: RevenueCycleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RevenueCycleApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevenueCycleApp(viewModel: RevenueCycleViewModel) {
    val context = LocalContext.current
    val ownerAuthManager = remember { OwnerAuthManager(context) }
    val isUnlocked by ownerAuthManager.isUnlocked.collectAsStateWithLifecycle()
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    if (!isUnlocked) {
        OwnerLoginScreen(
            onUnlockSuccess = { enteredPass ->
                ownerAuthManager.verifyAndUnlock(enteredPass)
            }
        )
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog state holders
    var showNewOrderDialog by remember { mutableStateOf(false) }
    var shipmentToVerify by remember { mutableStateOf<Shipment?>(null) }
    var shipmentDocToView by remember { mutableStateOf<Shipment?>(null) }
    var invoiceDetailToView by remember { mutableStateOf<InvoiceWithDetails?>(null) }
    var invoiceToPay by remember { mutableStateOf<InvoiceWithDetails?>(null) }
    var receiptDetailToView by remember { mutableStateOf<CashReceipt?>(null) }
    var showDiagramScreen by remember { mutableStateOf(false) }
    var showGeneralCashDialog by remember { mutableStateOf(false) }

    // Collect Reactive State
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val inventory by viewModel.inventory.collectAsStateWithLifecycle()
    val orders by viewModel.salesOrders.collectAsStateWithLifecycle()
    val shipments by viewModel.shipments.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val cashReceipts by viewModel.cashReceipts.collectAsStateWithLifecycle()
    val cashTransactions by viewModel.cashTransactions.collectAsStateWithLifecycle()
    val cashBalance by viewModel.latestCashBalance.collectAsStateWithLifecycle()
    val totalReceivables by viewModel.totalReceivables.collectAsStateWithLifecycle()
    val totalSales by viewModel.totalSales.collectAsStateWithLifecycle()
    val totalCommission by viewModel.totalCommission.collectAsStateWithLifecycle()
    val pendingShipmentsCount by viewModel.pendingShipmentsCount.collectAsStateWithLifecycle()
    val unpaidInvoicesCount by viewModel.unpaidInvoicesCount.collectAsStateWithLifecycle()
    val dailyRevenueTrends by viewModel.dailyRevenueTrends.collectAsStateWithLifecycle()

    val notification by viewModel.notification.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(notification) {
        notification?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it.message)
                viewModel.dismissNotification()
            }
        }
    }

    val navigationItems = listOf(
        NavigationItem("Ikhtisar", Icons.Default.Dashboard, 0),
        NavigationItem("1.0 Pesanan", Icons.Default.ShoppingCart, orders.size),
        NavigationItem("2.0 Pengiriman", Icons.Default.LocalShipping, pendingShipmentsCount),
        NavigationItem("3.0 Faktur", Icons.Default.Receipt, unpaidInvoicesCount),
        NavigationItem("4.0 Kas & GL", Icons.Default.AccountBalance, 0)
    )

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PT NIAGA ARTHA NUSANTARA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlueAccent,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Siklus Pendapatan & Penerimaan Kas",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                },
                actions = {
                    // Status Badge Pemilik Aktif
                    Surface(
                        color = EmeraldSuccessLight,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSuccess)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pemilik", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                        }
                    }

                    // Tombol Ganti Sandi Pemilik
                    IconButton(onClick = { showChangePasswordDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = "Ubah Sandi",
                            tint = NavyPrimary
                        )
                    }

                    // Bagan Alir SIA
                    IconButton(onClick = { showDiagramScreen = !showDiagramScreen }) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Bagan Alir SIA",
                            tint = if (showDiagramScreen) BlueAccent else NavyPrimary
                        )
                    }

                    // Kunci Aplikasi
                    IconButton(onClick = { ownerAuthManager.lock() }) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Kunci Aplikasi",
                            tint = CrimsonDanger
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (!showDiagramScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    navigationItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                if (item.badgeCount > 0) {
                                    BadgedBox(badge = { Badge { Text("${item.badgeCount}") } }) {
                                        Icon(imageVector = item.icon, contentDescription = item.label)
                                    }
                                } else {
                                    Icon(imageVector = item.icon, contentDescription = item.label)
                                }
                            },
                            label = { Text(item.label, fontSize = 10.sp, maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NavyPrimary,
                                selectedTextColor = NavyPrimary,
                                indicatorColor = BlueAccent.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showDiagramScreen) {
                DiagramViewerScreen()
            } else {
                when (selectedTab) {
                    0 -> DashboardScreen(
                        cashBalance = cashBalance,
                        totalSales = totalSales,
                        totalReceivables = totalReceivables,
                        totalCommission = totalCommission,
                        orders = orders,
                        shipments = shipments,
                        invoices = invoices,
                        cashTransactions = cashTransactions,
                        dailyRevenueTrends = dailyRevenueTrends,
                        onNavigateToTab = { selectedTab = it },
                        onOpenNewOrder = { showNewOrderDialog = true },
                        onOpenRecordCash = { showGeneralCashDialog = true },
                        onOpenDiagram = { showDiagramScreen = true },
                        onInvoiceClick = { invoiceDetailToView = it }
                    )

                    1 -> SalesOrderScreen(
                        orders = orders,
                        onOpenNewOrder = { showNewOrderDialog = true }
                    )

                    2 -> ShippingScreen(
                        shipments = shipments,
                        onVerifyClick = { shipmentToVerify = it },
                        onViewDocumentClick = { shipmentDocToView = it }
                    )

                    3 -> InvoiceScreen(
                        invoices = invoices,
                        totalReceivables = totalReceivables,
                        onInvoiceClick = { invoiceDetailToView = it },
                        onPayInvoiceClick = { invoiceToPay = it }
                    )

                    4 -> CashLedgerScreen(
                        cashBalance = cashBalance,
                        transactions = cashTransactions,
                        cashReceipts = cashReceipts,
                        onOpenRecordCash = { showGeneralCashDialog = true },
                        onReceiptClick = { receiptDetailToView = it }
                    )
                }
            }

            // Floating banner if diagram mode is open
            if (showDiagramScreen) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { showDiagramScreen = false },
                        colors = CardDefaults.cardColors(containerColor = NavyPrimary)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kembali ke Sistem Operasional", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showNewOrderDialog) {
        NewSalesOrderDialog(
            customers = customers,
            inventoryItems = inventory,
            onDismiss = { showNewOrderDialog = false },
            onSubmit = { customer, items, salesperson, notes ->
                viewModel.createSalesOrder(customer, items, salesperson, notes)
            }
        )
    }

    shipmentToVerify?.let { shipment ->
        VerifyShipmentDialog(
            shipment = shipment,
            onDismiss = { shipmentToVerify = null },
            onConfirm = { id, verifier, courier, tracking, notes ->
                viewModel.verifyShipment(id, verifier, courier, tracking, notes)
            }
        )
    }

    shipmentDocToView?.let { shipment ->
        BillOfLadingDialog(
            shipment = shipment,
            onDismiss = { shipmentDocToView = null }
        )
    }

    invoiceDetailToView?.let { invoiceDetail ->
        InvoiceDetailDialog(
            invoiceDetail = invoiceDetail,
            onDismiss = { invoiceDetailToView = null },
            onPayClick = { invoiceToPay = it }
        )
    }

    // Record Cash Receipt modal
    if (invoiceToPay != null || selectedTab == 4 && false) {
        val unpaidInvoices = invoices.filter { it.invoice.status != "PAID" }
        RecordCashReceiptDialog(
            unpaidInvoices = unpaidInvoices,
            preselectedInvoiceId = invoiceToPay?.invoice?.id,
            onDismiss = { invoiceToPay = null },
            onSubmit = { invoiceId, amount, method, bank, ref, notes ->
                viewModel.recordCashReceipt(invoiceId, amount, method, bank, ref, notes)
            }
        )
    }
    // General Record Cash Receipt when triggered from action button
    // If invoiceToPay is null and opened via general button:
    // Handled by setting invoiceToPay to dummy or dedicated boolean
    if (invoiceToPay == null && showGeneralCashDialog) {
        val unpaidInvoices = invoices.filter { it.invoice.status != "PAID" }
        RecordCashReceiptDialog(
            unpaidInvoices = unpaidInvoices,
            preselectedInvoiceId = null,
            onDismiss = { showGeneralCashDialog = false },
            onSubmit = { invoiceId, amount, method, bank, ref, notes ->
                viewModel.recordCashReceipt(invoiceId, amount, method, bank, ref, notes)
            }
        )
    }

    receiptDetailToView?.let { receipt ->
        CashReceiptDetailDialog(
            receipt = receipt,
            onDismiss = { receiptDetailToView = null }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onSubmitChange = { oldPass, newPass ->
                val success = ownerAuthManager.changePassword(oldPass, newPass)
                if (success) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Sandi pemilik berhasil diperbarui!")
                    }
                }
                success
            }
        )
    }
}

data class NavigationItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeCount: Int = 0
)
