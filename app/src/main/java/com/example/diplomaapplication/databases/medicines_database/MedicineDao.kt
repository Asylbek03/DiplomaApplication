package com.example.diplomaapplication.databases.medicines_database

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MedicineDao {

    @Insert
    suspend fun insertMedicine(medicine: Medicine)

    @Update
    suspend fun updateMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)

    @Query("SELECT * FROM medicines")
    fun getAllMedicines() : LiveData<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE time BETWEEN :startDate AND :endDate")
    fun getMedicinesForPeriod(startDate: Long, endDate: Long): LiveData<List<Medicine>>

    @Query("DELETE FROM sqlite_sequence WHERE name = 'medicines'")
    suspend fun deletePrimaryKeyIndex()

    @Query("SELECT * FROM medicines WHERE time BETWEEN :startTime AND :endTime")
    fun getMedicinesInTimeRange(startTime: Long, endTime: Long): LiveData<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE id = :medicineId")
    suspend fun getMedicineById(medicineId: Int): Medicine?

    @Query("UPDATE medicines SET isTaken = :isTaken WHERE id = :medicineId")
    suspend fun updateMedicineIsTaken(medicineId: Int, isTaken: Boolean)

    @Query("UPDATE medicines SET isTaken = :isTaken WHERE id = :medicineId")
    suspend fun updateIsTaken(medicineId: Int, isTaken: Boolean)



    @Query("UPDATE medicines SET isTaken = :isTaken WHERE id = :medicineId")
    suspend fun updateMedicineTakenStatus(medicineId: Int, isTaken: Boolean)


    @Query("SELECT id FROM medicines WHERE name = :medicineName AND time <= :currentTimeMillis ORDER BY time DESC LIMIT 1")
    suspend fun getMedicineIdByName(medicineName: String, currentTimeMillis: Long = System.currentTimeMillis()): Int?




    @Dao
    interface DatabaseDao {
        @Query("DELETE FROM sqlite_sequence")
        fun clearPrimaryKeyIndex()
    }
}