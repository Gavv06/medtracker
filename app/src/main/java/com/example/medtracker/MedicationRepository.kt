package com.example.medtracker

import android.content.Context
import androidx.room.Room

class MedicationRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "medtracker-db"
    ).build()

    private val intakeDao = db.medicationIntakeDao()
    private val planDao = db.medicationPlanDao()

    suspend fun insertIntake(intake: MedicationIntake) = intakeDao.insert(intake)
    suspend fun getAllIntakes() = intakeDao.getAll()

    suspend fun insertPlan(plan: MedicationPlan) = planDao.insert(plan)
    suspend fun getAllPlans() = planDao.getAll()

    suspend fun clearAllPlans() = planDao.clearAll()
} 