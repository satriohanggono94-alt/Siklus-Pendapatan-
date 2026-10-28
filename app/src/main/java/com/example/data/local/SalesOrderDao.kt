package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.SalesOrder
import com.example.data.model.SalesOrderItem
import com.example.data.model.SalesOrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesOrderDao {
    @Transaction
    @Query("SELECT * FROM sales_orders ORDER BY orderDate DESC")
    fun getAllOrdersWithItems(): Flow<List<SalesOrderWithItems>>

    @Transaction
    @Query("SELECT * FROM sales_orders WHERE id = :orderId")
    suspend fun getOrderWithItemsById(orderId: Long): SalesOrderWithItems?

    @Query("SELECT * FROM sales_orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Long): SalesOrder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: SalesOrder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<SalesOrderItem>)

    @Update
    suspend fun updateOrder(order: SalesOrder)

    @Query("UPDATE sales_orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)

    @Query("SELECT COUNT(*) FROM sales_orders WHERE status = 'APPROVED'")
    fun getApprovedOrderCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM sales_orders WHERE status = 'SHIPPED'")
    fun getShippedOrderCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM sales_orders WHERE status != 'CANCELLED'")
    fun getTotalSalesAmount(): Flow<Double>

    @Query("SELECT COALESCE(SUM(commissionAmount), 0.0) FROM sales_orders WHERE status != 'CANCELLED'")
    fun getTotalCommissionAmount(): Flow<Double>
}
