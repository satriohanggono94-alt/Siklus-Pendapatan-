package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Shipment
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipmentDao {
    @Query("SELECT * FROM shipments ORDER BY shipDate DESC")
    fun getAllShipments(): Flow<List<Shipment>>

    @Query("SELECT * FROM shipments WHERE id = :id")
    suspend fun getShipmentById(id: Long): Shipment?

    @Query("SELECT * FROM shipments WHERE salesOrderId = :salesOrderId")
    suspend fun getShipmentByOrderId(salesOrderId: Long): Shipment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipment(shipment: Shipment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipments(shipments: List<Shipment>)

    @Update
    suspend fun updateShipment(shipment: Shipment)

    @Query("UPDATE shipments SET isVerified = 1, status = :status, verifiedBy = :verifiedBy WHERE id = :shipmentId")
    suspend fun markShipmentVerified(shipmentId: Long, status: String, verifiedBy: String)

    @Query("SELECT COUNT(*) FROM shipments WHERE status = 'PENDING'")
    fun getPendingShipmentCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM shipments WHERE status = 'DELIVERED' OR status = 'SHIPPED'")
    fun getCompletedShipmentCount(): Flow<Int>
}
