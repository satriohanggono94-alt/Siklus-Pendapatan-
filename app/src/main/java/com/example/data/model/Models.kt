package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Entitas Pelanggan (Customer) dalam Siklus Pendapatan
 */
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerCode: String,
    val name: String,
    val company: String,
    val email: String,
    val phone: String,
    val address: String,
    val creditLimit: Double,
    val currentBalance: Double = 0.0 // Saldo piutang berjalan
)

/**
 * Entitas Persediaan Barang (Inventory)
 */
@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sku: String,
    val name: String,
    val category: String,
    val unit: String, // Unit / Pcs / Box
    val unitPrice: Double, // Harga jual
    val costPrice: Double, // Harga pokok penjualan (HPP)
    val stockQuantity: Int, // Stok tersedia
    val reorderLevel: Int = 10
)

/**
 * 1.0 Entri Pesanan Penjualan (Sales Order)
 * Menampung informasi pesanan pelanggan, komisi wiraniaga, dan status persetujuan
 */
@Entity(
    tableName = "sales_orders",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId")]
)
data class SalesOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String, // e.g. SO-2026-001
    val customerId: Long,
    val customerName: String,
    val orderDate: Long, // timestamp
    val status: String, // "DRAFT", "APPROVED", "BACKORDER", "SHIPPED", "COMPLETED", "CANCELLED"
    val subtotal: Double,
    val taxAmount: Double, // PPN 11%
    val totalAmount: Double,
    val salespersonName: String,
    val commissionRate: Double = 0.03, // 3% komisi
    val commissionAmount: Double = 0.0,
    val notes: String = ""
)

/**
 * Rincian Barang dalam Pesanan Penjualan
 */
@Entity(
    tableName = "sales_order_items",
    foreignKeys = [
        ForeignKey(
            entity = SalesOrder::class,
            parentColumns = ["id"],
            childColumns = ["salesOrderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = InventoryItem::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("salesOrderId"), Index("itemId")]
)
data class SalesOrderItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val salesOrderId: Long,
    val itemId: Long,
    val itemSku: String,
    val itemName: String,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)

/**
 * 2.0 Pengiriman (Shipping & Delivery Verification)
 * Mencakup Slip Pengepakan (Packing Slip) & Konosemen / Bill of Lading
 */
@Entity(
    tableName = "shipments",
    foreignKeys = [
        ForeignKey(
            entity = SalesOrder::class,
            parentColumns = ["id"],
            childColumns = ["salesOrderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("salesOrderId")]
)
data class Shipment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val shipmentNumber: String, // e.g. SHP-2026-001
    val salesOrderId: Long,
    val orderNumber: String,
    val customerName: String,
    val packingSlipNumber: String, // Slip Pengepakan
    val billOfLadingNumber: String, // Bill of Lading (B/L)
    val courierName: String, // Kurir (JNE, Cargo Mandiri, Armada Internal)
    val trackingNumber: String,
    val destinationAddress: String,
    val shipDate: Long,
    val isVerified: Boolean = false, // Verifikasi Pengiriman
    val verifiedBy: String = "",
    val status: String = "PENDING", // "PENDING", "VERIFIED", "SHIPPED", "DELIVERED"
    val notes: String = ""
)

/**
 * 3.0 Penagihan (Billing & Invoicing)
 * Mencatat Faktur Penjualan resmi ke Pelanggan dan Piutang Usaha
 */
@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = SalesOrder::class,
            parentColumns = ["id"],
            childColumns = ["salesOrderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Shipment::class,
            parentColumns = ["id"],
            childColumns = ["shipmentId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("salesOrderId"), Index("shipmentId")]
)
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String, // e.g. INV-2026-001
    val salesOrderId: Long,
    val shipmentId: Long?,
    val customerId: Long,
    val customerName: String,
    val invoiceDate: Long,
    val dueDate: Long,
    val paymentTerms: String = "Net 30", // e.g. "Net 30", "COD", "Cash Advance"
    val subtotal: Double,
    val taxAmount: Double, // PPN 11%
    val totalAmount: Double,
    val paidAmount: Double = 0.0,
    val balanceDue: Double,
    val status: String = "UNPAID", // "UNPAID", "PARTIALLY_PAID", "PAID", "OVERDUE"
    val generalLedgerPosted: Boolean = true // Posting otomatis ke Sistem Buku Besar
)

/**
 * 4.0 Penerimaan Kas (Cash Receipts & Bank Reconciliation)
 * Mencatat Pembayaran dari Pelanggan, Kuitansi / Tanda Terima Kas, dan Setoran Bank
 */
@Entity(
    tableName = "cash_receipts",
    foreignKeys = [
        ForeignKey(
            entity = Invoice::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("invoiceId")]
)
data class CashReceipt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val receiptNumber: String, // e.g. CR-2026-001 (Tanda Terima Kas)
    val invoiceId: Long,
    val invoiceNumber: String,
    val customerId: Long,
    val customerName: String,
    val receiptDate: Long,
    val paymentMethod: String, // "BANK_TRANSFER", "CASH", "CHECK", "QRIS"
    val destinationBank: String, // "BCA Giro Operasional", "Mandiri Penjualan", "Kas Kasir"
    val amountPaid: Double,
    val referenceNumber: String, // No Referensi Bank / Bukti Setor
    val depositedToBank: Boolean = true,
    val bankDepositNumber: String = "",
    val notes: String = ""
)

/**
 * Mutasi Buku Kas & Jurnal Otomatis (General Ledger Cash & Revenue Log)
 * Pelaporan otomatis saldo kas secara real-time
 */
@Entity(tableName = "cash_transactions")
data class CashTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transactionNumber: String, // e.g. TRX-2026-001
    val date: Long,
    val type: String, // "DEBIT" (Kas Masuk / Penambahan Saldo), "CREDIT" (Kas Keluar)
    val accountName: String, // "Bank BCA - 8001239999", "Kas Tunai", "Bank Mandiri"
    val sourceModule: String, // "PENERIMAAN_KAS", "SETORAN_BANK", "SALDO_AWAL", "KOMISI_SALES"
    val referenceNumber: String, // No Faktur / No Tanda Terima Kas
    val description: String,
    val amount: Double,
    val runningBalance: Double // Saldo kas kumulatif otomatis
)

/**
 * Hubungan Pesanan Penjualan lengkap dengan rincian item
 */
data class SalesOrderWithItems(
    @Embedded val salesOrder: SalesOrder,
    @Relation(
        parentColumn = "id",
        entityColumn = "salesOrderId"
    )
    val items: List<SalesOrderItem>
)

/**
 * Data Faktur lengkap dengan detail pesanan dan pengiriman
 */
data class InvoiceWithDetails(
    @Embedded val invoice: Invoice,
    @Relation(
        parentColumn = "salesOrderId",
        entityColumn = "id"
    )
    val salesOrder: SalesOrder?,
    @Relation(
        parentColumn = "shipmentId",
        entityColumn = "id"
    )
    val shipment: Shipment?
)
