package com.example.medtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MedicationIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val dateTime: Long
) 