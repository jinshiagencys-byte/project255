package com.example.doccare.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doccare.R
import com.example.doccare.model.Doctor
import com.example.doccare.ui.theme.CrimsonAlert
import com.example.doccare.ui.theme.DeepCharcoalText
import com.example.doccare.ui.theme.MedicalDeepPine
import com.example.doccare.ui.theme.MedicalEucalyptus
import com.example.doccare.ui.theme.MedicalForestTeal
import com.example.doccare.ui.theme.MedicalSageLight
import com.example.doccare.ui.theme.MedicalTealLight
import com.example.doccare.ui.theme.MutedSlateText
import com.example.doccare.ui.theme.SubtleGrayText
import com.example.doccare.ui.theme.WarmAmber
import com.example.doccare.ui.theme.WarmCardBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    doctor: Doctor,
    onBackClick: () -> Unit,
    onFavoriteToggle: (Int) -> Unit,
    onBookAppointment: (Doctor, String, String, String, String) -> Unit,
    onOpenChat: (Doctor) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)
    val context = LocalContext.current

    val dateOptions = remember {
        listOf(
            "Aujourd'hui, 14 oct.",
            "Demain, 15 oct.",
            "Jeudi 16 oct.",
            "Vendredi 17 oct.",
            "Samedi 18 oct.",
            "Lundi 20 oct."
        )
    }

    val timeOptions = remember {
        listOf(
            "09h00",
            "10h00",
            "11h30",
            "14h00",
            "15h30",
            "16h30"
        )
    }

    var selectedDate by remember { mutableStateOf(dateOptions[0]) }
    var selectedTime by remember { mutableStateOf(timeOptions[1]) }
    var selectedType by remember { mutableStateOf("Au cabinet médical") }
    var appointmentNotes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fiche praticien",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onFavoriteToggle(doctor.id) },
                        modifier = Modifier.testTag("detail_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (doctor.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (doctor.isFavorite) CrimsonAlert else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Consultez le ${doctor.name} (${doctor.specialty}) sur DocCare. Cabinet : ${doctor.address}"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Partager le praticien"))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Partager"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, WarmCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Tarif conventionné",
                            fontSize = 11.sp,
                            color = MutedSlateText
                        )
                        Text(
                            text = "50,00 €",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalForestTeal
                        )
                    }

                    Button(
                        onClick = {
                            onBookAppointment(doctor, selectedDate, selectedTime, selectedType, appointmentNotes)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalForestTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("book_appointment_button")
                    ) {
                        Text(
                            text = stringResource(id = R.string.book_appointment),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Carte principale du médecin
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MedicalTealLight)
                        ) {
                            Image(
                                painter = painterResource(id = doctor.localDrawableRes),
                                contentDescription = doctor.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = doctor.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.tick),
                                    contentDescription = "Certifié",
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = doctor.specialty,
                                fontSize = 13.sp,
                                color = MedicalForestTeal,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Adresse",
                                    tint = SubtleGrayText,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = doctor.address,
                                    fontSize = 11.sp,
                                    color = MutedSlateText,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            // Statistiques du médecin
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBox(
                        title = "Patients",
                        value = doctor.patientsCount,
                        icon = Icons.Default.CheckCircle,
                        iconColor = MedicalEucalyptus,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "Expérience",
                        value = "${doctor.experienceYears} ans",
                        icon = Icons.Default.AccessTime,
                        iconColor = MedicalForestTeal,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "Note",
                        value = "${doctor.rating} / 5",
                        icon = Icons.Default.Star,
                        iconColor = WarmAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Boutons d'action rapide
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    QuickActionItem(
                        label = stringResource(id = R.string.call),
                        icon = Icons.Default.Call,
                        tint = MedicalForestTeal,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${doctor.mobile}")
                            }
                            context.startActivity(intent)
                        }
                    )
                    QuickActionItem(
                        label = stringResource(id = R.string.message),
                        icon = Icons.Default.Email,
                        tint = MedicalEucalyptus,
                        onClick = { onOpenChat(doctor) }
                    )
                    QuickActionItem(
                        label = stringResource(id = R.string.direction),
                        icon = Icons.Default.Map,
                        tint = MedicalForestTeal,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(doctor.locationUrl))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Impossible d'ouvrir le plan", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    QuickActionItem(
                        label = stringResource(id = R.string.website),
                        icon = Icons.Default.Language,
                        tint = MedicalForestTeal,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(doctor.website))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Impossible d'ouvrir le site web", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // Présentation du médecin
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.about_doctor),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = doctor.biography,
                            fontSize = 13.sp,
                            color = MutedSlateText,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // Mode de consultation
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = stringResource(id = R.string.consultation_type),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ConsultationTypeCard(
                            title = stringResource(id = R.string.in_person),
                            subtitle = "Cabinet médical - Salle 3",
                            icon = Icons.Default.LocationOn,
                            isSelected = selectedType == "Au cabinet médical",
                            onClick = { selectedType = "Au cabinet médical" },
                            modifier = Modifier.weight(1f)
                        )
                        ConsultationTypeCard(
                            title = stringResource(id = R.string.video_call),
                            subtitle = "Visio HD sécurisée",
                            icon = Icons.Default.Videocam,
                            isSelected = selectedType == "Téléconsultation vidéo",
                            onClick = { selectedType = "Téléconsultation vidéo" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Choix de la date
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = stringResource(id = R.string.select_date),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dateOptions) { date ->
                        val isSelected = selectedDate == date
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MedicalForestTeal else MaterialTheme.colorScheme.surface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MedicalForestTeal else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedDate = date }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                                .testTag("date_chip_$date")
                        ) {
                            Text(
                                text = date,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Choix de l'horaire
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = stringResource(id = R.string.select_time),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timeOptions.take(3).forEach { time ->
                            TimeChip(
                                time = time,
                                isSelected = selectedTime == time,
                                onClick = { selectedTime = time },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timeOptions.drop(3).forEach { time ->
                            TimeChip(
                                time = time,
                                isSelected = selectedTime == time,
                                onClick = { selectedTime = time },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Note pour le médecin
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Notes ou antécédents (facultatif)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = appointmentNotes,
                        onValueChange = { appointmentNotes = it },
                        placeholder = { Text("Précisez vos symptômes ou vos questions pour le praticien...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MedicalForestTeal
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = MutedSlateText
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ConsultationTypeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MedicalTealLight else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) MedicalForestTeal else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) MedicalForestTeal else SubtleGrayText,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MedicalForestTeal else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MutedSlateText
                )
            }
        }
    }
}

@Composable
private fun TimeChip(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MedicalForestTeal else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (isSelected) MedicalForestTeal else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}
