package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CashReceipt
import com.example.data.model.CashTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CashDao {
    // Penerimaan Kas
    @Query("SELECT * FROM cash_receipts ORDER BY receiptDate DESC")
    fun getAllCashReceipts(): Flow<List<CashReceipt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashReceipt(receipt: CashReceipt): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashReceipts(receipts: List<CashReceipt>)

    // Buku Kas & Mutasi Real-Time
    @Query("SELECT * FROM cash_transactions ORDER BY date DESC, id DESC")
    fun getAllCashTransactions(): Flow<List<CashTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CashTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<CashTransaction>)

    @Query("SELECT runningBalance FROM cash_transactions ORDER BY id DESC LIMIT 1")
    fun getLatestCashBalanceFlow(): Flow<Double?>

    @Query("SELECT runningBalance FROM cash_transactions ORDER BY id DESC LIMIT 1")
    suspend fun getLatestCashBalance(): Double?

    @Query("SELECT COALESCE(SUM(amountPaid), 0.0) FROM cash_receipts")
    fun getTotalCashReceived(): Flow<Double>
}
