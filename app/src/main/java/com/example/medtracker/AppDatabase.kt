package com.example.medtracker

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MedicationIntake::class, MedicationPlan::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationIntakeDao(): MedicationIntakeDao
    abstract fun medicationPlanDao(): MedicationPlanDao
}
