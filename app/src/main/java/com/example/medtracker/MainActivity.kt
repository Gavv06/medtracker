package com.example.medtracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medtracker.ui.theme.MedtrackerTheme
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.livedata.observeAsState
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.border
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.background
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Création du canal de notification (obligatoire Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "medtracker_reminder",
                "Rappels de médication",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications pour rappeler la prise de médicaments."
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        // Demande la permission notification si nécessaire (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 2001)
            }
        }
        // Demande la permission caméra si nécessaire
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
        setContent {
            MedtrackerTheme {
                MainTabs()
            }
        }
    }
}

@Composable
fun MainTabs(viewModel: MedicationViewModel = viewModel()) {
    // Charger l'historique dès le lancement
    LaunchedEffect(Unit) {
        viewModel.loadIntakes()
    }
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Scanner", "Plan de médication")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, color = MaterialTheme.colorScheme.onBackground) }
                )
            }
        }
        when (selectedTab) {
            0 -> ScannerTab(viewModel)
            1 -> PlanTab(viewModel)
        }
    }
}

@Composable
fun ScannerTab(viewModel: MedicationViewModel) {
    val intakes by viewModel.intakes.observeAsState(emptyList())
    val context = LocalContext.current
    val hasCameraPermission = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(96.dp))
        if (hasCameraPermission) {
            ScannerScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                onCodeScanned = { code ->
                    viewModel.addIntake(
                        MedicationIntake(
                            code = code,
                            dateTime = System.currentTimeMillis()
                        )
                    )
                }
            )
        } else {
            Text(
                "La permission caméra est requise pour scanner.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(Modifier.height(220.dp))
        Text(
            "Historique des prises :",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f, fill = true)) {
            items(intakes) { intake ->
                val date = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(intake.dateTime))
                Text("$date - ${intake.code}", modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
fun PlanTab(viewModel: MedicationViewModel) {
    val plans by viewModel.plans.observeAsState(emptyList())
    val intakes by viewModel.intakes.observeAsState(emptyList())
    val scannedCodes = intakes.map { it.code }.filter { it.isNotBlank() }.distinct()
    var selectedCode by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedDateTime by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Ajouter une prise prévue", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.padding(8.dp)) {
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(56.dp)
                        .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
                        .clickable { expanded = true }
                        .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = if (selectedCode.isNotBlank()) selectedCode else "Code DataMatrix",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (scannedCodes.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Aucun code scanné", modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onBackground) },
                                onClick = { expanded = false },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            scannedCodes.forEach { code ->
                                DropdownMenuItem(
                                    text = { Text(code, color = MaterialTheme.colorScheme.onBackground) },
                                    onClick = {
                                        try {
                                            selectedCode = code
                                        } catch (e: Exception) {
                                            selectedCode = ""
                                        }
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .align(Alignment.Bottom)
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = if (selectedDateTime != null)
                            SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(selectedDateTime!!))
                        else "Choisir la date et l'heure",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            if (showDatePicker) {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, day)
                        showDatePicker = false
                        showTimePicker = true
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            if (showTimePicker) {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        selectedDateTime = calendar.timeInMillis
                        showTimePicker = false
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }
            Button(
                onClick = {
                    if (selectedCode.isNotBlank() && selectedDateTime != null && selectedDateTime!! > System.currentTimeMillis()) {
                        viewModel.addPlan(MedicationPlan(code = selectedCode, scheduledTime = selectedDateTime!!))
                        try {
                            // DEBUG : exécution immédiate du Worker (delay = 0)
                            val data = Data.Builder().putString("code", selectedCode).build()
                            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                                .setInitialDelay(0, TimeUnit.MILLISECONDS)
                                .setInputData(data)
                                .build()
                            WorkManager.getInstance(context.applicationContext).enqueue(workRequest)
                        } catch (e: Exception) {
                            android.util.Log.e("PlanNotif", "Erreur WorkManager", e)
                            android.widget.Toast.makeText(context, "Erreur WorkManager : ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                        }
                        selectedCode = ""
                        selectedDateTime = null
                        showDatePicker = false
                        showTimePicker = false
                        viewModel.loadPlans()
                    } else if (selectedDateTime != null && selectedDateTime!! <= System.currentTimeMillis()) {
                        android.widget.Toast.makeText(context, "La date/heure doit être dans le futur.", android.widget.Toast.LENGTH_LONG).show()
                    }
                },
                enabled = selectedCode.isNotBlank() && selectedDateTime != null && selectedDateTime!! > System.currentTimeMillis(),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Ajouter au plan", color = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(Modifier.height(24.dp))
            Text("Plan de médication :", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            val upcomingPlans = plans.filter { it.scheduledTime > System.currentTimeMillis() }
            LazyColumn {
                items(upcomingPlans) { plan ->
                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(plan.scheduledTime))
                    Text("$date (${plan.scheduledTime}) - ${plan.code}", modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
        FloatingActionButton(
            onClick = { viewModel.clearPlans() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Delete, contentDescription = "Vider le plan de médication", tint = MaterialTheme.colorScheme.onBackground)
        }
    }
}