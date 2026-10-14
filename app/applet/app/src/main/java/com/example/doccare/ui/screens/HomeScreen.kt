package com.example.doccare.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doccare.R
import com.example.doccare.model.Doctor
import com.example.doccare.ui.DocCareUiState
import com.example.doccare.ui.DoctorFilter
import com.example.doccare.ui.components.CategoryItem
import com.example.doccare.ui.components.DoctorCard
import com.example.doccare.ui.components.HealthBanner
import com.example.doccare.ui.components.SectionHeader
import com.example.doccare.ui.theme.MedicalBlue
import com.example.doccare.ui.theme.MedicalBlueLight
import com.example.doccare.ui.theme.SlateLight
import com.example.doccare.ui.theme.SlateMedium

@Composable
fun HomeScreen(
    uiState: DocCareUiState,
    onSearchChanged: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onFilterSelected: (DoctorFilter) -> Unit,
    onDoctorClicked: (Doctor) -> Unit,
    onFavoriteToggle: (Int) -> Unit,
    onBannerBookClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top User Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MedicalBlueLight)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.women),
                            contentDescription = "User Profile Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Hi, Welcome Back!",
                            fontSize = 12.sp,
                            color = SlateMedium
                        )
                        Text(
                            text = "Alex Morgan",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Notification Bell with Badge
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = MedicalBlue,
                            modifier = Modifier.size(8.dp)
                        )
                    }
                ) {
                    IconButton(
                        onClick = { /* Notifications */ },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bell_icon),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChanged,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.search_hint),
                            fontSize = 14.sp,
                            color = SlateLight
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MedicalBlue
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = SlateMedium
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_doctor_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MedicalBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true
                )
            }
        }

        // Promotional Health Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                HealthBanner(
                    onBookClick = onBannerBookClicked
                )
            }
        }

        // Specialty Categories Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                SectionHeader(
                    title = stringResource(id = R.string.specialist_categories),
                    actionText = if (uiState.selectedCategoryName != null) "Clear Filter" else "See All",
                    onActionClick = { onCategorySelected(null) }
                )
            }
        }

        // Specialty Categories Horizontal List
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.categories, key = { it.id }) { category ->
                    val isSelected = uiState.selectedCategoryName.equals(category.name, ignoreCase = true)
                    CategoryItem(
                        category = category,
                        isSelected = isSelected,
                        onClick = { onCategorySelected(category.name) }
                    )
                }
            }
        }

        // Doctors Section Header & Filters
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                SectionHeader(
                    title = if (uiState.selectedCategoryName != null) {
                        "${uiState.selectedCategoryName} Specialists (${uiState.filteredDoctors.size})"
                    } else {
                        "${stringResource(id = R.string.top_doctors)} (${uiState.filteredDoctors.size})"
                    }
                )

                // Filters Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = uiState.selectedFilter == DoctorFilter.ALL,
                        onClick = { onFilterSelected(DoctorFilter.ALL) },
                        label = { Text("All", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MedicalBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = uiState.selectedFilter == DoctorFilter.TOP_RATED,
                        onClick = { onFilterSelected(DoctorFilter.TOP_RATED) },
                        label = { Text("Top Rated ★", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MedicalBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = uiState.selectedFilter == DoctorFilter.HIGH_EXPERIENCE,
                        onClick = { onFilterSelected(DoctorFilter.HIGH_EXPERIENCE) },
                        label = { Text("Most Experienced", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MedicalBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Doctors List or Empty State
        if (uiState.filteredDoctors.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.doctor),
                            contentDescription = "No Doctors",
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(id = R.string.no_doctors_found),
                            color = SlateMedium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(uiState.filteredDoctors, key = { it.id }) { doctor ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    DoctorCard(
                        doctor = doctor,
                        onDoctorClick = { onDoctorClicked(doctor) },
                        onFavoriteClick = { onFavoriteToggle(doctor.id) }
                    )
                }
            }
        }
    }
}
