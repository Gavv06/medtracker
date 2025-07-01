package com.example.medtracker

import androidx.room.*

@Dao
interface MedicationPlanDao {
    @Insert
    suspend fun insert(plan: MedicationPlan)

    @Query("SELECT * FROM MedicationPlan ORDER BY scheduledTime ASC")
    suspend fun getAll(): List<MedicationPlan>

    @Query("DELETE FROM MedicationPlan")
    suspend fun clearAll()
} 