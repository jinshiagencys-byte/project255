package com.example.doccare.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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
import com.example.doccare.ui.theme.DangerRed
import com.example.doccare.ui.theme.MedicalBlue
import com.example.doccare.ui.theme.MedicalBlueLight
import com.example.doccare.ui.theme.MedicalTeal
import com.example.doccare.ui.theme.MedicalTealLight
import com.example.doccare.ui.theme.SlateLight
import com.example.doccare.ui.theme.SlateMedium

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
                        fontSize = 20.sp,
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
            // Profile Card Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(MedicalBlueLight)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.women),
                                contentDescription = "Profile Picture",
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
                            text = "alex.morgan@example.com • ID: DC-9842",
                            fontSize = 12.sp,
                            color = SlateMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Health metrics chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            HealthMetricChip(title = "Blood Group", value = "O+")
                            HealthMetricChip(title = "Weight", value = "68 kg")
                            HealthMetricChip(title = "Height", value = "175 cm")
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Account & Preferences Section
            item {
                Text(
                    text = "Health & Records",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMedium,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        ProfileMenuRow(
                            icon = Icons.Default.Favorite,
                            iconTint = DangerRed,
                            title = stringResource(id = R.string.favorites),
                            badge = "${favoriteDoctors.size} Saved",
                            onClick = { showFavoritesDialog = true }
                        )

                        ProfileMenuRow(
                            icon = Icons.Default.Description,
                            iconTint = MedicalBlue,
                            title = "My Medical History & Records",
                            badge = "3 Reports",
                            onClick = { showMedicalNotesDialog = true }
                        )

                        ProfileMenuRow(
                            icon = Icons.Default.Emergency,
                            iconTint = DangerRed,
                            title = "Emergency Contact Hotline",
                            badge = "Call 911",
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Settings & Preferences
            item {
                Text(
                    text = "Preferences",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMedium,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
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
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MedicalBlue.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = MedicalBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Appointment Notifications",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = MedicalBlue)
                            )
                        }

                        ProfileMenuRow(
                            icon = Icons.Default.Security,
                            iconTint = MedicalTeal,
                            title = "Privacy & HIPAA Compliance",
                            onClick = { /* Privacy info */ }
                        )

                        ProfileMenuRow(
                            icon = Icons.Default.HelpOutline,
                            iconTint = MedicalBlue,
                            title = "Help & Support",
                            onClick = { /* Help */ }
                        )

                        ProfileMenuRow(
                            icon = Icons.Default.Info,
                            iconTint = SlateMedium,
                            title = "About DocCare",
                            badge = "v1.0.0",
                            onClick = { /* Version */ }
                        )
                    }
                }
            }
        }
    }

    // Favorites Dialog
    if (showFavoritesDialog) {
        AlertDialog(
            onDismissRequest = { showFavoritesDialog = false },
            title = { Text("Favorite Doctors", fontWeight = FontWeight.Bold) },
            text = {
                if (favoriteDoctors.isEmpty()) {
                    Text("You have not saved any doctors to your favorites yet. Tap the heart icon on any doctor profile to save them.")
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
                                        .size(42.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = doc.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = doc.specialty, color = MedicalBlue, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFavoritesDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Medical Notes Dialog
    if (showMedicalNotesDialog) {
        AlertDialog(
            onDismissRequest = { showMedicalNotesDialog = false },
            title = { Text("Medical History & Lab Notes", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Recent Cardiology Checkup: Normal ECG, Blood Pressure 118/78 mmHg.", fontSize = 13.sp)
                    Text("• Routine Blood Panel: All values within normal physiological ranges.", fontSize = 13.sp)
                    Text("• Allergies: Penicillin (Mild).", fontSize = 13.sp)
                    Text("• Active Vaccinations: Up to date.", fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showMedicalNotesDialog = false }) {
                    Text("OK")
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
        color = MedicalBlueLight,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MedicalBlue
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = SlateMedium
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
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Surface(
                    color = MedicalBlueLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 11.sp,
                        color = MedicalBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = SlateLight,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
