package com.example.medtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MedicationPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String, // code DataMatrix attendu
    val scheduledTime: Long // heure prévue (timestamp)
) 