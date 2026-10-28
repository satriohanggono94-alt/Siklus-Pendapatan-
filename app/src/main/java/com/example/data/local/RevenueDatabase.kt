package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CashReceipt
import com.example.data.model.CashTransaction
import com.example.data.model.Customer
import com.example.data.model.InventoryItem
import com.example.data.model.Invoice
import com.example.data.model.SalesOrder
import com.example.data.model.SalesOrderItem
import com.example.data.model.Shipment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Customer::class,
        InventoryItem::class,
        SalesOrder::class,
        SalesOrderItem::class,
        Shipment::class,
        Invoice::class,
        CashReceipt::class,
        CashTransaction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RevenueDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun salesOrderDao(): SalesOrderDao
    abstract fun shipmentDao(): ShipmentDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun cashDao(): CashDao

    companion object {
        @Volatile
        private var INSTANCE: RevenueDatabase? = null

        fun getDatabase(context: Context): RevenueDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RevenueDatabase::class.java,
                    "revenue_cycle_db"
                )
                    .addCallback(RevenueDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class RevenueDatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: RevenueDatabase) {
            val customerDao = db.customerDao()
            val inventoryDao = db.inventoryDao()
            val salesOrderDao = db.salesOrderDao()
            val shipmentDao = db.shipmentDao()
            val invoiceDao = db.invoiceDao()
            val cashDao = db.cashDao()

            val now = System.currentTimeMillis()
            val oneDay = 86400000L

            // 1. Initial Customers
            val c1 = Customer(
                customerCode = "CUST-001",
                name = "PT Maju Bersama",
                company = "PT Maju Bersama Logistik",
                email = "procurement@majubersama.co.id",
                phone = "021-5582910",
                address = "Kawasan Industri MM2100 Blok C-4, Cikarang Barat",
                creditLimit = 150000000.0,
                currentBalance = 38850000.0
            )
            val c2 = Customer(
                customerCode = "CUST-002",
                name = "CV Sumber Rejeki",
                company = "CV Sumber Rejeki Retail",
                email = "sumber.rejeki@retail.com",
                phone = "031-8921044",
                address = "Jl. Basuki Rahmat No. 88, Surabaya",
                creditLimit = 80000000.0,
                currentBalance = 16650000.0
            )
            val c3 = Customer(
                customerCode = "CUST-003",
                name = "PT Digital Solusindo",
                company = "PT Digital Solusindo Pratama",
                email = "finance@digitalsolusindo.id",
                phone = "021-7290112",
                address = "Gedung Cyber 2 Tower Lt. 15, Kuningan, Jakarta Selatan",
                creditLimit = 200000000.0,
                currentBalance = 0.0
            )
            val c1Id = customerDao.insertCustomer(c1)
            val c2Id = customerDao.insertCustomer(c2)
            val c3Id = customerDao.insertCustomer(c3)

            // 2. Initial Inventory Items
            val item1 = InventoryItem(
                sku = "PRD-LAP-01",
                name = "Laptop Asus ExpertBook Core i5 16GB",
                category = "Komputer & Laptop",
                unit = "Unit",
                unitPrice = 12500000.0,
                costPrice = 10200000.0,
                stockQuantity = 45,
                reorderLevel = 10
            )
            val item2 = InventoryItem(
                sku = "PRD-PRN-02",
                name = "Printer Epson EcoTank L3210 All-in-One",
                category = "Perangkat Kantor",
                unit = "Unit",
                unitPrice = 2850000.0,
                costPrice = 2300000.0,
                stockQuantity = 28,
                reorderLevel = 5
            )
            val item3 = InventoryItem(
                sku = "PRD-MNT-03",
                name = "Monitor Dell UltraSharp 24\" FHD IPS",
                category = "Display & Monitor",
                unit = "Unit",
                unitPrice = 3200000.0,
                costPrice = 2650000.0,
                stockQuantity = 35,
                reorderLevel = 8
            )
            val item4 = InventoryItem(
                sku = "PRD-SCN-04",
                name = "Wireless Barcode Scanner 2D Zebra",
                category = "Barcode & POS",
                unit = "Pcs",
                unitPrice = 1450000.0,
                costPrice = 1100000.0,
                stockQuantity = 60,
                reorderLevel = 12
            )
            val item1Id = inventoryDao.insertItem(item1)
            val item2Id = inventoryDao.insertItem(item2)
            val item3Id = inventoryDao.insertItem(item3)
            val item4Id = inventoryDao.insertItem(item4)

            // 3. Initial Cash Ledger Balance (Kas Awal Perusahaan di Bank)
            var currentBalance = 250000000.0 // Rp 250.000.000
            val initTx = CashTransaction(
                transactionNumber = "TRX-2026-INIT",
                date = now - (10 * oneDay),
                type = "DEBIT",
                accountName = "BCA Rekening Giro Operasional",
                sourceModule = "SALDO_AWAL",
                referenceNumber = "REF-SALDO-AWAL",
                description = "Saldo Awal Buku Kas Perusahaan (Bank BCA)",
                amount = currentBalance,
                runningBalance = currentBalance
            )
            cashDao.insertTransaction(initTx)

            // 4. Sample Order 1: Completed Full Cycle (SO -> Ship -> Invoice -> Paid)
            val so1Subtotal = 35000000.0
            val so1Tax = so1Subtotal * 0.11
            val so1Total = so1Subtotal + so1Tax
            val so1Comm = so1Total * 0.03
            val so1 = SalesOrder(
                orderNumber = "SO-2026-001",
                customerId = c3Id,
                customerName = "PT Digital Solusindo",
                orderDate = now - (6 * oneDay),
                status = "COMPLETED",
                subtotal = so1Subtotal,
                taxAmount = so1Tax,
                totalAmount = so1Total,
                salespersonName = "Budi Hartono",
                commissionRate = 0.03,
                commissionAmount = so1Comm,
                notes = "Pesanan pengadaan workstation kantor cabang"
            )
            val so1Id = salesOrderDao.insertOrder(so1)
            salesOrderDao.insertOrderItems(
                listOf(
                    SalesOrderItem(
                        salesOrderId = so1Id,
                        itemId = item1Id,
                        itemSku = "PRD-LAP-01",
                        itemName = "Laptop Asus ExpertBook Core i5 16GB",
                        quantity = 2,
                        unitPrice = 12500000.0,
                        subtotal = 25000000.0
                    ),
                    SalesOrderItem(
                        salesOrderId = so1Id,
                        itemId = item3Id,
                        itemSku = "PRD-MNT-03",
                        itemName = "Monitor Dell UltraSharp 24\" FHD IPS",
                        quantity = 2,
                        unitPrice = 3200000.0,
                        subtotal = 6400000.0
                    ),
                    SalesOrderItem(
                        salesOrderId = so1Id,
                        itemId = item4Id,
                        itemSku = "PRD-SCN-04",
                        itemName = "Wireless Barcode Scanner 2D Zebra",
                        quantity = 2,
                        unitPrice = 1800000.0,
                        subtotal = 3600000.0
                    )
                )
            )

            // Shipment for Order 1
            val shp1 = Shipment(
                shipmentNumber = "SHP-2026-001",
                salesOrderId = so1Id,
                orderNumber = "SO-2026-001",
                customerName = "PT Digital Solusindo",
                packingSlipNumber = "PKG-2026-001",
                billOfLadingNumber = "BOL-2026-001",
                courierName = "JNE Express Logistik",
                trackingNumber = "JNE-88291039",
                destinationAddress = "Gedung Cyber 2 Lt. 15, Kuningan, Jakarta",
                shipDate = now - (5 * oneDay),
                isVerified = true,
                verifiedBy = "Andi Wijaya (Supervisor Gudang)",
                status = "DELIVERED",
                notes = "Barang telah diterima lengkap dengan tanda tangan penerima"
            )
            val shp1Id = shipmentDao.insertShipment(shp1)

            // Invoice for Order 1
            val inv1 = Invoice(
                invoiceNumber = "INV-2026-001",
                salesOrderId = so1Id,
                shipmentId = shp1Id,
                customerId = c3Id,
                customerName = "PT Digital Solusindo",
                invoiceDate = now - (5 * oneDay),
                dueDate = now + (25 * oneDay),
                paymentTerms = "Net 30",
                subtotal = so1Subtotal,
                taxAmount = so1Tax,
                totalAmount = so1Total,
                paidAmount = so1Total,
                balanceDue = 0.0,
                status = "PAID",
                generalLedgerPosted = true
            )
            val inv1Id = invoiceDao.insertInvoice(inv1)

            // Cash Receipt for Invoice 1
            val cr1 = CashReceipt(
                receiptNumber = "CR-2026-001",
                invoiceId = inv1Id,
                invoiceNumber = "INV-2026-001",
                customerId = c3Id,
                customerName = "PT Digital Solusindo",
                receiptDate = now - (3 * oneDay),
                paymentMethod = "BANK_TRANSFER",
                destinationBank = "BCA Rekening Giro Operasional",
                amountPaid = so1Total,
                referenceNumber = "TRF-BCA-992104",
                depositedToBank = true,
                bankDepositNumber = "DEP-2026-001",
                notes = "Pelunasan penuh Faktur INV-2026-001 via transfer BCA"
            )
            cashDao.insertCashReceipt(cr1)

            // Update Cash balance
            currentBalance += so1Total
            val tx1 = CashTransaction(
                transactionNumber = "TRX-2026-001",
                date = now - (3 * oneDay),
                type = "DEBIT",
                accountName = "BCA Rekening Giro Operasional",
                sourceModule = "PENERIMAAN_KAS",
                referenceNumber = "CR-2026-001",
                description = "Penerimaan Pembayaran INV-2026-001 (PT Digital Solusindo)",
                amount = so1Total,
                runningBalance = currentBalance
            )
            cashDao.insertTransaction(tx1)

            // 5. Sample Order 2: Invoiced, awaiting payment (Unpaid)
            val so2Subtotal = 35000000.0
            val so2Tax = so2Subtotal * 0.11
            val so2Total = so2Subtotal + so2Tax
            val so2 = SalesOrder(
                orderNumber = "SO-2026-002",
                customerId = c1Id,
                customerName = "PT Maju Bersama",
                orderDate = now - (2 * oneDay),
                status = "SHIPPED",
                subtotal = so2Subtotal,
                taxAmount = so2Tax,
                totalAmount = so2Total,
                salespersonName = "Dewi Lestari",
                commissionRate = 0.03,
                commissionAmount = so2Total * 0.03,
                notes = "Pengadaan unit printer dan monitor kasir"
            )
            val so2Id = salesOrderDao.insertOrder(so2)
            salesOrderDao.insertOrderItems(
                listOf(
                    SalesOrderItem(
                        salesOrderId = so2Id,
                        itemId = item2Id,
                        itemSku = "PRD-PRN-02",
                        itemName = "Printer Epson EcoTank L3210 All-in-One",
                        quantity = 6,
                        unitPrice = 2850000.0,
                        subtotal = 17100000.0
                    ),
                    SalesOrderItem(
                        salesOrderId = so2Id,
                        itemId = item3Id,
                        itemSku = "PRD-MNT-03",
                        itemName = "Monitor Dell UltraSharp 24\" FHD IPS",
                        quantity = 5,
                        unitPrice = 3200000.0,
                        subtotal = 16000000.0
                    ),
                    SalesOrderItem(
                        salesOrderId = so2Id,
                        itemId = item4Id,
                        itemSku = "PRD-SCN-04",
                        itemName = "Wireless Barcode Scanner 2D Zebra",
                        quantity = 1,
                        unitPrice = 1900000.0,
                        subtotal = 1900000.0
                    )
                )
            )

            val shp2 = Shipment(
                shipmentNumber = "SHP-2026-002",
                salesOrderId = so2Id,
                orderNumber = "SO-2026-002",
                customerName = "PT Maju Bersama",
                packingSlipNumber = "PKG-2026-002",
                billOfLadingNumber = "BOL-2026-002",
                courierName = "Armada Truk Box Mandiri (B 9812 UI)",
                trackingNumber = "TRK-B9812UI",
                destinationAddress = "Kawasan Industri MM2100 Blok C-4, Cikarang",
                shipDate = now - (1 * oneDay),
                isVerified = true,
                verifiedBy = "Andi Wijaya (Supervisor Gudang)",
                status = "SHIPPED",
                notes = "Barang telah dimuat dan dikonfirmasi sopir kurir"
            )
            val shp2Id = shipmentDao.insertShipment(shp2)

            val inv2 = Invoice(
                invoiceNumber = "INV-2026-002",
                salesOrderId = so2Id,
                shipmentId = shp2Id,
                customerId = c1Id,
                customerName = "PT Maju Bersama",
                invoiceDate = now - (1 * oneDay),
                dueDate = now + (29 * oneDay),
                paymentTerms = "Net 30",
                subtotal = so2Subtotal,
                taxAmount = so2Tax,
                totalAmount = so2Total,
                paidAmount = 0.0,
                balanceDue = so2Total,
                status = "UNPAID",
                generalLedgerPosted = true
            )
            invoiceDao.insertInvoice(inv2)

            // 6. Sample Order 3: Ready for shipment verification (Awaiting 2.0 Pengiriman)
            val so3Subtotal = 15000000.0
            val so3Tax = so3Subtotal * 0.11
            val so3Total = so3Subtotal + so3Tax
            val so3 = SalesOrder(
                orderNumber = "SO-2026-003",
                customerId = c2Id,
                customerName = "CV Sumber Rejeki",
                orderDate = now - (12 * 3600000L),
                status = "APPROVED",
                subtotal = so3Subtotal,
                taxAmount = so3Tax,
                totalAmount = so3Total,
                salespersonName = "Budi Hartono",
                commissionRate = 0.03,
                commissionAmount = so3Total * 0.03,
                notes = "Pesanan disetujui, persediaan mencukupi, siap diverifikasi dan dikirim"
            )
            val so3Id = salesOrderDao.insertOrder(so3)
            salesOrderDao.insertOrderItems(
                listOf(
                    SalesOrderItem(
                        salesOrderId = so3Id,
                        itemId = item1Id,
                        itemSku = "PRD-LAP-01",
                        itemName = "Laptop Asus ExpertBook Core i5 16GB",
                        quantity = 1,
                        unitPrice = 12500000.0,
                        subtotal = 12500000.0
                    ),
                    SalesOrderItem(
                        salesOrderId = so3Id,
                        itemId = item2Id,
                        itemSku = "PRD-PRN-02",
                        itemName = "Printer Epson EcoTank L3210 All-in-One",
                        quantity = 1,
                        unitPrice = 2500000.0,
                        subtotal = 2500000.0
                    )
                )
            )

            // Pending shipment for Order 3
            val shp3 = Shipment(
                shipmentNumber = "SHP-2026-003",
                salesOrderId = so3Id,
                orderNumber = "SO-2026-003",
                customerName = "CV Sumber Rejeki",
                packingSlipNumber = "PKG-2026-003",
                billOfLadingNumber = "BOL-2026-003",
                courierName = "Kurir Kargo Wahana / SiCepat",
                trackingNumber = "SI-7719280",
                destinationAddress = "Jl. Basuki Rahmat No. 88, Surabaya",
                shipDate = now,
                isVerified = false,
                verifiedBy = "",
                status = "PENDING",
                notes = "Menunggu verifikasi fisik barang dan penandatanganan konosemen"
            )
            shipmentDao.insertShipment(shp3)
        }
    }
}
