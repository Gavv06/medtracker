package com.example.medtracker

import androidx.room.*

@Dao
interface MedicationIntakeDao {
    @Insert
    suspend fun insert(intake: MedicationIntake)

    @Query("SELECT * FROM MedicationIntake ORDER BY dateTime DESC")
    suspend fun getAll(): List<MedicationIntake>
} 