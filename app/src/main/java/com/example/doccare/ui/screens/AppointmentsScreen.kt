package com.example.doccare.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doccare.R
import com.example.doccare.model.Appointment
import com.example.doccare.model.AppointmentStatus
import com.example.doccare.ui.DocCareUiState
import com.example.doccare.ui.components.AppointmentCard
import com.example.doccare.ui.theme.CrimsonAlert
import com.example.doccare.ui.theme.MedicalDeepPine
import com.example.doccare.ui.theme.MedicalForestTeal
import com.example.doccare.ui.theme.MedicalTealLight
import com.example.doccare.ui.theme.MutedSlateText
import com.example.doccare.ui.theme.SubtleGrayText
import com.example.doccare.ui.theme.WarmCardBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    uiState: DocCareUiState,
    onStatusTabChanged: (AppointmentStatus) -> Unit,
    onCancelAppointment: (String) -> Unit,
    onRescheduleAppointment: (String, String, String) -> Unit,
    onFindDoctorsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var appointmentToCancel by remember { mutableStateOf<Appointment?>(null) }
    var appointmentToReschedule by remember { mutableStateOf<Appointment?>(null) }

    val filteredAppointments = uiState.appointments.filter { it.status == uiState.activeAppointmentStatus }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.appointments),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Onglets de statut (À venir, Passés, Annulés)
            val tabs = listOf(
                AppointmentStatus.UPCOMING to stringResource(id = R.string.upcoming),
                AppointmentStatus.COMPLETED to stringResource(id = R.string.completed),
                AppointmentStatus.CANCELLED to stringResource(id = R.string.cancelled)
            )

            PrimaryTabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == uiState.activeAppointmentStatus }.coerceAtLeast(0),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MedicalForestTeal
            ) {
                tabs.forEach { (status, title) ->
                    val isSelected = uiState.activeAppointmentStatus == status
                    val count = uiState.appointments.count { it.status == status }
                    Tab(
                        selected = isSelected,
                        onClick = { onStatusTabChanged(status) },
                        text = {
                            Text(
                                text = "$title ($count)",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.testTag("appointment_tab_${status.name}")
                    )
                }
            }

            if (filteredAppointments.isEmpty()) {
                // État vide
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MedicalTealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.btn_2),
                                contentDescription = "Rendez-vous",
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(id = R.string.no_appointments),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = stringResource(id = R.string.no_appointments_desc),
                            fontSize = 13.sp,
                            color = MutedSlateText,
                            lineHeight = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = onFindDoctorsClick,
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalForestTeal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("empty_find_doctors_btn")
                        ) {
                            Text("Trouver un praticien", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredAppointments, key = { it.id }) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onCancelClick = { appointmentToCancel = appointment },
                            onRescheduleClick = { appointmentToReschedule = appointment },
                            onDirectionClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=Cabinet+Medical"))
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Impossible d'ouvrir le plan", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialogue d'annulation
    appointmentToCancel?.let { appt ->
        AlertDialog(
            onDismissRequest = { appointmentToCancel = null },
            title = { Text("Annuler le rendez-vous ?", fontWeight = FontWeight.Bold) },
            text = { Text("Êtes-vous sûr de vouloir annuler votre consultation avec le ${appt.doctorName} prévue le ${appt.date} ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancelAppointment(appt.id)
                        appointmentToCancel = null
                    }
                ) {
                    Text("Confirmer l'annulation", color = CrimsonAlert, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { appointmentToCancel = null }) {
                    Text("Conserver le RDV")
                }
            }
        )
    }

    // Dialogue de déplacement de RDV
    appointmentToReschedule?.let { appt ->
        var rescheduleDate by remember { mutableStateOf("Demain, 15 oct.") }
        var rescheduleTime by remember { mutableStateOf("11h30") }

        AlertDialog(
            onDismissRequest = { appointmentToReschedule = null },
            title = { Text("Déplacer le rendez-vous", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Choisissez un nouveau créneau avec le ${appt.doctorName} :", fontSize = 13.sp, color = MutedSlateText)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Date", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    val newDates = listOf("Demain, 15 oct.", "Jeudi 16 oct.", "Vendredi 17 oct.")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        newDates.forEach { d ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (rescheduleDate == d) MedicalForestTeal else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (rescheduleDate == d) MedicalForestTeal else WarmCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { rescheduleDate = d }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = d,
                                    fontSize = 11.sp,
                                    color = if (rescheduleDate == d) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Horaire", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    val newTimes = listOf("10h00", "11h30", "14h30", "16h30")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        newTimes.forEach { t ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (rescheduleTime == t) MedicalForestTeal else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (rescheduleTime == t) MedicalForestTeal else WarmCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { rescheduleTime = t }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = t,
                                    fontSize = 11.sp,
                                    color = if (rescheduleTime == t) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRescheduleAppointment(appt.id, rescheduleDate, rescheduleTime)
                        appointmentToReschedule = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalForestTeal)
                ) {
                    Text("Valider le nouveau RDV")
                }
            },
            dismissButton = {
                TextButton(onClick = { appointmentToReschedule = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}
