package com.example.doccare.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.doccare.ui.DocCareUiState
import com.example.doccare.ui.theme.CrimsonAlert
import com.example.doccare.ui.theme.MedicalDeepPine
import com.example.doccare.ui.theme.MedicalEucalyptus
import com.example.doccare.ui.theme.MedicalForestTeal
import com.example.doccare.ui.theme.MedicalSageLight
import com.example.doccare.ui.theme.MedicalTealLight
import com.example.doccare.ui.theme.MutedSlateText
import com.example.doccare.ui.theme.SubtleGrayText
import com.example.doccare.ui.theme.WarmCardBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: DocCareUiState,
    onDoctorClicked: (Doctor) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showFavoritesDialog by remember { mutableStateOf(false) }
    var showMedicalNotesDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    val favoriteDoctors = uiState.allDoctors.filter { it.isFavorite }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile),
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Carte de profil patient
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .clip(CircleShape)
                                .background(MedicalTealLight)
                                .border(2.dp, MedicalForestTeal.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.women),
                                contentDescription = "Photo de profil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Alex Morgan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "alex.morgan@email.fr • N° Sécu : 2 92 04 75 …",
                            fontSize = 12.sp,
                            color = MutedSlateText
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Indicateurs de santé
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            HealthMetricChip(title = "Groupe", value = "O+")
                            HealthMetricChip(title = "Poids", value = "68 kg")
                            HealthMetricChip(title = "Taille", value = "175 cm")
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Section Santé & Documents
            item {
                Text(
                    text = "Santé & Données médicales",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedSlateText,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        ProfileMenuRow(
                            icon = Icons.Default.Favorite,
                            iconTint = CrimsonAlert,
                            title = stringResource(id = R.string.favorites),
                            badge = "${favoriteDoctors.size} enregistrés",
                            onClick = { showFavoritesDialog = true }
                        )

                        ProfileMenuRow(
                            icon = Icons.Default.Description,
                            iconTint = MedicalForestTeal,
                            title = "Mon carnet de santé & bilans",
                            badge = "3 comptes-rendus",
                            onClick = { showMedicalNotesDialog = true }
                        )

                        ProfileMenuRow(
                            icon = Icons.Default.Emergency,
                            iconTint = CrimsonAlert,
                            title = "Numéro d'urgence SAMU 15",
                            badge = "Appel direct",
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:15"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Préférences & Confidentialité
            item {
                Text(
                    text = "Préférences & Confidentialité",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedSlateText,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MedicalTealLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Rappels",
                                        tint = MedicalForestTeal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Rappels de rendez-vous",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MedicalForestTeal
                                )
                            )
                        }

                        ProfileMenuRow(
                            icon = Icons.Default.Security,
                            iconTint = MedicalEucalyptus,
                            title = "Protection des données de santé (HDS / RGPD)",
                            onClick = { /* HDS */ }
                        )

                        ProfileMenuRow(
                            icon = Icons.Default.Info,
                            iconTint = MedicalForestTeal,
                            title = "Centre d'aide & assistance patient",
                            onClick = { /* Aide */ }
                        )

                        ProfileMenuRow(
                            icon = Icons.Default.Info,
                            iconTint = SubtleGrayText,
                            title = "À propos de DocCare",
                            badge = "v1.0.0",
                            onClick = { /* Version */ }
                        )
                    }
                }
            }
        }
    }

    // Dialogue des médecins favoris
    if (showFavoritesDialog) {
        AlertDialog(
            onDismissRequest = { showFavoritesDialog = false },
            title = { Text("Médecins enregistrés", fontWeight = FontWeight.Bold) },
            text = {
                if (favoriteDoctors.isEmpty()) {
                    Text("Vous n'avez pas encore enregistré de praticien en favori. Touchez l'icône cœur sur une fiche praticien pour le retrouver facilement.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        favoriteDoctors.forEach { doc ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        showFavoritesDialog = false
                                        onDoctorClicked(doc)
                                    }
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = doc.localDrawableRes),
                                    contentDescription = doc.name,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = doc.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = doc.specialty, color = MedicalForestTeal, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFavoritesDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }

    // Dialogue du carnet de santé
    if (showMedicalNotesDialog) {
        AlertDialog(
            onDismissRequest = { showMedicalNotesDialog = false },
            title = { Text("Carnet de santé & Bilans", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Dernier bilan cardiologique : ECG normal, tension artérielle 118/78 mmHg.", fontSize = 13.sp)
                    Text("• Bilan sanguin récent : Numération formule sanguine et glycémie conformes.", fontSize = 13.sp)
                    Text("• Allergies médicamenteuses : Pénicilline (réaction cutanée modérée).", fontSize = 13.sp)
                    Text("• Calendrier vaccinal : Rappels à jour.", fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showMedicalNotesDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }
}

@Composable
private fun HealthMetricChip(
    title: String,
    value: String
) {
    Surface(
        color = MedicalTealLight,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MedicalDeepPine
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = MutedSlateText
            )
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Surface(
                    color = MedicalTealLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 11.sp,
                        color = MedicalDeepPine,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = SubtleGrayText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
