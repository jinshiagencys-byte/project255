package com.example.doccare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doccare.data.DoctorRepository
import com.example.doccare.model.AppointmentStatus
import com.example.doccare.ui.DocCareViewModel
import com.example.doccare.ui.NavigationTab
import com.example.doccare.ui.screens.AppointmentsScreen
import com.example.doccare.ui.screens.DoctorDetailScreen
import com.example.doccare.ui.screens.HomeScreen
import com.example.doccare.ui.screens.MessagesScreen
import com.example.doccare.ui.screens.ProfileScreen
import com.example.doccare.ui.theme.DocCareTheme
import com.example.doccare.ui.theme.MedicalDeepPine
import com.example.doccare.ui.theme.MedicalForestTeal
import com.example.doccare.ui.theme.MutedSlateText
import com.example.doccare.ui.theme.WarmCardBorder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = DoctorRepository(applicationContext)

        setContent {
            DocCareTheme {
                val viewModel: DocCareViewModel = viewModel {
                    DocCareViewModel(repository)
                }
                DocCareApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DocCareApp(viewModel: DocCareViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.bookingSuccessMessage) {
        uiState.bookingSuccessMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearBookingSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // Masquer la barre de navigation sur la fiche praticien
            if (uiState.selectedDoctor == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.border(1.dp, WarmCardBorder)
                ) {
                    val upcomingCount = uiState.appointments.count { it.status == AppointmentStatus.UPCOMING }

                    NavigationBarItem(
                        selected = uiState.activeTab == NavigationTab.HOME,
                        onClick = { viewModel.setNavigationTab(NavigationTab.HOME) },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.btn_1),
                                contentDescription = stringResource(id = R.string.home),
                                modifier = Modifier.size(22.dp),
                                colorFilter = if (uiState.activeTab == NavigationTab.HOME) {
                                    ColorFilter.tint(MedicalForestTeal)
                                } else {
                                    ColorFilter.tint(MutedSlateText)
                                }
                            )
                        },
                        label = { Text(stringResource(id = R.string.home), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MedicalForestTeal,
                            selectedTextColor = MedicalForestTeal,
                            indicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = uiState.activeTab == NavigationTab.APPOINTMENTS,
                        onClick = { viewModel.setNavigationTab(NavigationTab.APPOINTMENTS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (upcomingCount > 0) {
                                        Badge(containerColor = MedicalForestTeal) {
                                            Text(upcomingCount.toString(), color = Color.White)
                                        }
                                    }
                                }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.btn_2),
                                    contentDescription = stringResource(id = R.string.appointments),
                                    modifier = Modifier.size(22.dp),
                                    colorFilter = if (uiState.activeTab == NavigationTab.APPOINTMENTS) {
                                        ColorFilter.tint(MedicalForestTeal)
                                    } else {
                                        ColorFilter.tint(MutedSlateText)
                                    }
                                )
                            }
                        },
                        label = { Text(stringResource(id = R.string.appointments), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MedicalForestTeal,
                            selectedTextColor = MedicalForestTeal,
                            indicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("nav_appointments")
                    )

                    NavigationBarItem(
                        selected = uiState.activeTab == NavigationTab.MESSAGES,
                        onClick = { viewModel.setNavigationTab(NavigationTab.MESSAGES) },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.btn_3),
                                contentDescription = stringResource(id = R.string.messages),
                                modifier = Modifier.size(22.dp),
                                colorFilter = if (uiState.activeTab == NavigationTab.MESSAGES) {
                                    ColorFilter.tint(MedicalForestTeal)
                                } else {
                                    ColorFilter.tint(MutedSlateText)
                                }
                            )
                        },
                        label = { Text(stringResource(id = R.string.messages), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MedicalForestTeal,
                            selectedTextColor = MedicalForestTeal,
                            indicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("nav_messages")
                    )

                    NavigationBarItem(
                        selected = uiState.activeTab == NavigationTab.PROFILE,
                        onClick = { viewModel.setNavigationTab(NavigationTab.PROFILE) },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.btn_4),
                                contentDescription = stringResource(id = R.string.profile),
                                modifier = Modifier.size(22.dp),
                                colorFilter = if (uiState.activeTab == NavigationTab.PROFILE) {
                                    ColorFilter.tint(MedicalForestTeal)
                                } else {
                                    ColorFilter.tint(MutedSlateText)
                                }
                            )
                        },
                        label = { Text(stringResource(id = R.string.profile), fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MedicalForestTeal,
                            selectedTextColor = MedicalForestTeal,
                            indicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            val doctor = uiState.selectedDoctor
            if (doctor != null) {
                DoctorDetailScreen(
                    doctor = doctor,
                    onBackClick = { viewModel.selectDoctor(null) },
                    onFavoriteToggle = { viewModel.toggleFavorite(it) },
                    onBookAppointment = { doc, date, time, type, notes ->
                        viewModel.bookAppointment(doc, date, time, type, notes)
                    },
                    onOpenChat = { doc ->
                        viewModel.selectDoctor(null)
                        viewModel.setNavigationTab(NavigationTab.MESSAGES)
                    }
                )
            } else {
                when (uiState.activeTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            uiState = uiState,
                            onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                            onCategorySelected = { viewModel.onCategorySelected(it) },
                            onFilterSelected = { viewModel.onFilterSelected(it) },
                            onDoctorClicked = { viewModel.selectDoctor(it) },
                            onFavoriteToggle = { viewModel.toggleFavorite(it) },
                            onBannerBookClicked = {
                                val firstDoc = uiState.filteredDoctors.firstOrNull() ?: uiState.allDoctors.firstOrNull()
                                if (firstDoc != null) {
                                    viewModel.selectDoctor(firstDoc)
                                }
                            }
                        )
                    }

                    NavigationTab.APPOINTMENTS -> {
                        AppointmentsScreen(
                            uiState = uiState,
                            onStatusTabChanged = { viewModel.setAppointmentStatusFilter(it) },
                            onCancelAppointment = { viewModel.cancelAppointment(it) },
                            onRescheduleAppointment = { id, date, time ->
                                viewModel.rescheduleAppointment(id, date, time)
                            },
                            onFindDoctorsClick = { viewModel.setNavigationTab(NavigationTab.HOME) }
                        )
                    }

                    NavigationTab.MESSAGES -> {
                        MessagesScreen(
                            uiState = uiState,
                            onSendMessage = { docId, text ->
                                viewModel.sendChatMessage(docId, text)
                            }
                        )
                    }

                    NavigationTab.PROFILE -> {
                        ProfileScreen(
                            uiState = uiState,
                            onDoctorClicked = { viewModel.selectDoctor(it) }
                        )
                    }
                }
            }
        }
    }
}
