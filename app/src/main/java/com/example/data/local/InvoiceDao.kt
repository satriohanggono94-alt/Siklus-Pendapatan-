package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Invoice
import com.example.data.model.InvoiceWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY invoiceDate DESC")
    fun getAllInvoices(): Flow<List<Invoice>>

    @Transaction
    @Query("SELECT * FROM invoices ORDER BY invoiceDate DESC")
    fun getAllInvoicesWithDetails(): Flow<List<InvoiceWithDetails>>

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getInvoiceWithDetailsById(id: Long): InvoiceWithDetails?

    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getInvoiceById(id: Long): Invoice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<Invoice>)

    @Update
    suspend fun updateInvoice(invoice: Invoice)

    @Query("UPDATE invoices SET paidAmount = paidAmount + :amountPaid, balanceDue = balanceDue - :amountPaid, status = :newStatus WHERE id = :invoiceId")
    suspend fun applyPayment(invoiceId: Long, amountPaid: Double, newStatus: String)

    @Query("SELECT COALESCE(SUM(balanceDue), 0.0) FROM invoices WHERE status != 'PAID'")
    fun getTotalOutstandingReceivables(): Flow<Double>

    @Query("SELECT COUNT(*) FROM invoices WHERE status = 'UNPAID' OR status = 'PARTIALLY_PAID'")
    fun getUnpaidInvoiceCount(): Flow<Int>
}
