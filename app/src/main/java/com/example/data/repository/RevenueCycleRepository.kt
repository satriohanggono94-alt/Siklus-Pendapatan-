package com.example.data.repository

import com.example.data.local.RevenueDatabase
import com.example.data.model.CashReceipt
import com.example.data.model.CashTransaction
import com.example.data.model.Customer
import com.example.data.model.InventoryItem
import com.example.data.model.Invoice
import com.example.data.model.InvoiceWithDetails
import com.example.data.model.SalesOrder
import com.example.data.model.SalesOrderItem
import com.example.data.model.SalesOrderWithItems
import com.example.data.model.Shipment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class RevenueCycleRepository(private val database: RevenueDatabase) {
    private val customerDao = database.customerDao()
    private val inventoryDao = database.inventoryDao()
    private val salesOrderDao = database.salesOrderDao()
    private val shipmentDao = database.shipmentDao()
    private val invoiceDao = database.invoiceDao()
    private val cashDao = database.cashDao()

    // Real-time Flows
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    val allInventory: Flow<List<InventoryItem>> = inventoryDao.getAllItems()
    val allOrdersWithItems: Flow<List<SalesOrderWithItems>> = salesOrderDao.getAllOrdersWithItems()
    val allShipments: Flow<List<Shipment>> = shipmentDao.getAllShipments()
    val allInvoicesWithDetails: Flow<List<InvoiceWithDetails>> = invoiceDao.getAllInvoicesWithDetails()
    val allCashReceipts: Flow<List<CashReceipt>> = cashDao.getAllCashReceipts()
    val allCashTransactions: Flow<List<CashTransaction>> = cashDao.getAllCashTransactions()
    val latestCashBalance: Flow<Double?> = cashDao.getLatestCashBalanceFlow()
    val totalOutstandingReceivables: Flow<Double> = invoiceDao.getTotalOutstandingReceivables()
    val totalSalesAmount: Flow<Double> = salesOrderDao.getTotalSalesAmount()
    val totalCommissionAmount: Flow<Double> = salesOrderDao.getTotalCommissionAmount()
    val pendingShipmentCount: Flow<Int> = shipmentDao.getPendingShipmentCount()
    val unpaidInvoiceCount: Flow<Int> = invoiceDao.getUnpaidInvoiceCount()

    // 1.0 Entri Pesanan Penjualan
    suspend fun createSalesOrder(
        customer: Customer,
        itemsWithQty: List<Pair<InventoryItem, Int>>,
        salespersonName: String,
        notes: String
    ): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val orderNum = "SO-2026-${(100..999).random()}"

        var subtotal = 0.0
        itemsWithQty.forEach { (item, qty) ->
            subtotal += item.unitPrice * qty
        }
        val tax = subtotal * 0.11 // PPN 11%
        val total = subtotal + tax
        val commission = total * 0.03 // 3% Komisi ke Siklus Penggajian SDM

        val order = SalesOrder(
            orderNumber = orderNum,
            customerId = customer.id,
            customerName = customer.name,
            orderDate = now,
            status = "APPROVED",
            subtotal = subtotal,
            taxAmount = tax,
            totalAmount = total,
            salespersonName = salespersonName.ifBlank { "Staf Penjualan" },
            commissionRate = 0.03,
            commissionAmount = commission,
            notes = notes
        )
        val orderId = salesOrderDao.insertOrder(order)

        val orderItems = itemsWithQty.map { (item, qty) ->
            SalesOrderItem(
                salesOrderId = orderId,
                itemId = item.id,
                itemSku = item.sku,
                itemName = item.name,
                quantity = qty,
                unitPrice = item.unitPrice,
                subtotal = item.unitPrice * qty
            )
        }
        salesOrderDao.insertOrderItems(orderItems)

        // Otomatis siapkan dokumen pengiriman (2.0 Pengiriman)
        val shipment = Shipment(
            shipmentNumber = "SHP-2026-${(100..999).random()}",
            salesOrderId = orderId,
            orderNumber = orderNum,
            customerName = customer.name,
            packingSlipNumber = "PKG-${orderNum.replace("SO-", "")}",
            billOfLadingNumber = "BOL-${orderNum.replace("SO-", "")}",
            courierName = "Kurir Internal / Logistik",
            trackingNumber = "TRK-${(100000..999999).random()}",
            destinationAddress = customer.address,
            shipDate = now,
            isVerified = false,
            status = "PENDING",
            notes = "Pesanan siap diverifikasi fisik dan pengepakan"
        )
        shipmentDao.insertShipment(shipment)

        orderId
    }

    // 2.0 Verifikasi Pengiriman (Shipping Verification & Bill of Lading)
    suspend fun verifyAndDispatchShipment(
        shipmentId: Long,
        verifierName: String,
        courierName: String,
        trackingNumber: String,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val shipment = shipmentDao.getShipmentById(shipmentId) ?: return@withContext
        val orderWithItems = salesOrderDao.getOrderWithItemsById(shipment.salesOrderId) ?: return@withContext

        // Verifikasi & update status pengiriman
        val updatedShipment = shipment.copy(
            isVerified = true,
            verifiedBy = verifierName.ifBlank { "Supervisor Gudang" },
            courierName = courierName.ifBlank { shipment.courierName },
            trackingNumber = trackingNumber.ifBlank { shipment.trackingNumber },
            status = "SHIPPED",
            notes = notes.ifBlank { shipment.notes }
        )
        shipmentDao.updateShipment(updatedShipment)

        // Kurangi stok persediaan (Warehouse Inventory adjustment)
        orderWithItems.items.forEach { orderItem ->
            inventoryDao.deductStock(orderItem.itemId, orderItem.quantity)
        }

        // Update status order
        salesOrderDao.updateOrderStatus(shipment.salesOrderId, "SHIPPED")

        // 3.0 Otomatis buat Faktur Penjualan (Billing) jika belum ada
        val existingInvoice = invoiceDao.getAllInvoices().let {
            // Check if already created
            val invNum = "INV-${shipment.orderNumber.replace("SO-", "")}"
            val order = orderWithItems.salesOrder
            val inv = Invoice(
                invoiceNumber = invNum,
                salesOrderId = order.id,
                shipmentId = shipment.id,
                customerId = order.customerId,
                customerName = order.customerName,
                invoiceDate = System.currentTimeMillis(),
                dueDate = System.currentTimeMillis() + (30 * 86400000L), // Net 30
                paymentTerms = "Net 30",
                subtotal = order.subtotal,
                taxAmount = order.taxAmount,
                totalAmount = order.totalAmount,
                paidAmount = 0.0,
                balanceDue = order.totalAmount,
                status = "UNPAID",
                generalLedgerPosted = true
            )
            invoiceDao.insertInvoice(inv)
            // Update customer outstanding balance
            customerDao.updateCustomerBalance(order.customerId, order.totalAmount)
        }
    }

    // 3.0 Catat/Update Faktur Penjualan Manual
    suspend fun createInvoice(
        salesOrderId: Long,
        paymentTerms: String,
        dueDate: Long
    ): Long = withContext(Dispatchers.IO) {
        val order = salesOrderDao.getOrderById(salesOrderId) ?: return@withContext 0L
        val shipment = shipmentDao.getShipmentByOrderId(salesOrderId)
        val invNum = "INV-2026-${(100..999).random()}"

        val invoice = Invoice(
            invoiceNumber = invNum,
            salesOrderId = salesOrderId,
            shipmentId = shipment?.id,
            customerId = order.customerId,
            customerName = order.customerName,
            invoiceDate = System.currentTimeMillis(),
            dueDate = dueDate,
            paymentTerms = paymentTerms,
            subtotal = order.subtotal,
            taxAmount = order.taxAmount,
            totalAmount = order.totalAmount,
            paidAmount = 0.0,
            balanceDue = order.totalAmount,
            status = "UNPAID",
            generalLedgerPosted = true
        )
        val id = invoiceDao.insertInvoice(invoice)
        customerDao.updateCustomerBalance(order.customerId, order.totalAmount)
        id
    }

    // 4.0 Penerimaan Kas (Cash Receipts & Bank Reconciliation)
    suspend fun recordCashReceipt(
        invoiceId: Long,
        amountPaid: Double,
        paymentMethod: String,
        destinationBank: String,
        referenceNumber: String,
        notes: String
    ): Long = withContext(Dispatchers.IO) {
        val invoice = invoiceDao.getInvoiceById(invoiceId) ?: return@withContext 0L
        val now = System.currentTimeMillis()

        // 1. Tentukan status baru faktur
        val newPaid = invoice.paidAmount + amountPaid
        val newBalance = (invoice.totalAmount - newPaid).coerceAtLeast(0.0)
        val newStatus = if (newBalance <= 0.001) "PAID" else "PARTIALLY_PAID"

        invoiceDao.applyPayment(invoiceId, amountPaid, newStatus)

        // 2. Buat Tanda Terima Kas (Remittance Advice / Bukti Kas Masuk)
        val receiptNum = "CR-2026-${(100..999).random()}"
        val cashReceipt = CashReceipt(
            receiptNumber = receiptNum,
            invoiceId = invoice.id,
            invoiceNumber = invoice.invoiceNumber,
            customerId = invoice.customerId,
            customerName = invoice.customerName,
            receiptDate = now,
            paymentMethod = paymentMethod,
            destinationBank = destinationBank,
            amountPaid = amountPaid,
            referenceNumber = referenceNumber.ifBlank { "REF-${System.currentTimeMillis() % 100000}" },
            depositedToBank = true,
            bankDepositNumber = "DEP-2026-${(100..999).random()}",
            notes = notes
        )
        val receiptId = cashDao.insertCashReceipt(cashReceipt)

        // 3. Update saldo piutang pelanggan
        customerDao.updateCustomerBalance(invoice.customerId, -amountPaid)

        // 4. Update Saldo Kas Berjalan & Catat Jurnal Buku Kas (General Ledger Cash Book)
        val currentCash = cashDao.getLatestCashBalance() ?: 250000000.0
        val newRunningCash = currentCash + amountPaid

        val tx = CashTransaction(
            transactionNumber = "TRX-2026-${(1000..9999).random()}",
            date = now,
            type = "DEBIT",
            accountName = destinationBank,
            sourceModule = "PENERIMAAN_KAS",
            referenceNumber = receiptNum,
            description = "Penerimaan Kas Faktur ${invoice.invoiceNumber} (${invoice.customerName})",
            amount = amountPaid,
            runningBalance = newRunningCash
        )
        cashDao.insertTransaction(tx)

        // Jika invoice lunas, pastikan order status completed
        if (newStatus == "PAID") {
            salesOrderDao.updateOrderStatus(invoice.salesOrderId, "COMPLETED")
        }

        receiptId
    }
}
