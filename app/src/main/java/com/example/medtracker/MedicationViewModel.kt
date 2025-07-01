package com.example.medtracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MedicationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MedicationRepository(application)

    private val _intakes = MutableLiveData<List<MedicationIntake>>()
    val intakes: LiveData<List<MedicationIntake>> = _intakes

    private val _plans = MutableLiveData<List<MedicationPlan>>()
    val plans: LiveData<List<MedicationPlan>> = _plans

    fun loadIntakes() {
        viewModelScope.launch {
            _intakes.postValue(repository.getAllIntakes())
        }
    }

    fun loadPlans() {
        viewModelScope.launch {
            _plans.postValue(repository.getAllPlans())
        }
    }

    fun addIntake(intake: MedicationIntake) {
        viewModelScope.launch {
            repository.insertIntake(intake)
            loadIntakes()
        }
    }

    fun addPlan(plan: MedicationPlan) {
        viewModelScope.launch {
            repository.insertPlan(plan)
            loadPlans()
        }
    }

    fun checkMissedIntakes(): List<MedicationPlan> {
        val plans = _plans.value ?: return emptyList()
        val intakes = _intakes.value ?: return emptyList()
        val tolerance = 30 * 60 * 1000 // 30 minutes en ms
        return plans.filter { plan ->
            intakes.none { intake ->
                intake.code == plan.code &&
                kotlin.math.abs(intake.dateTime - plan.scheduledTime) <= tolerance
            }
        }
    }

    fun clearPlans() {
        viewModelScope.launch {
            repository.clearAllPlans()
            loadPlans()
        }
    }
} 