package com.example.doccare.model

import androidx.annotation.DrawableRes
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val Id: Int,
    val Name: String,
    val Picture: String = ""
)

@Serializable
data class DoctorDto(
    val Id: Int? = null,
    val Name: String? = null,
    val Special: String? = null,
    val Biography: String? = null,
    val Expriense: Int? = null,
    val Rating: Double? = null,
    val Patiens: String? = null,
    val Address: String? = null,
    val Mobile: String? = null,
    val Site: String? = null,
    val Location: String? = null,
    val Picture: String? = null
)

@Serializable
data class DatabaseDto(
    val Category: List<CategoryDto> = emptyList(),
    val Doctors: List<DoctorDto?> = emptyList()
)

data class DoctorCategory(
    val id: Int,
    val name: String,
    val pictureUrl: String,
    @DrawableRes val localDrawableRes: Int
)

data class Doctor(
    val id: Int,
    val name: String,
    val specialty: String,
    val biography: String,
    val experienceYears: Int,
    val rating: Double,
    val patientsCount: String,
    val address: String,
    val mobile: String,
    val website: String,
    val locationUrl: String,
    val pictureUrl: String,
    @DrawableRes val localDrawableRes: Int,
    val isFavorite: Boolean = false
)

enum class AppointmentStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED
}

data class Appointment(
    val id: String,
    val doctorId: Int,
    val doctorName: String,
    val specialty: String,
    @DrawableRes val doctorImageRes: Int,
    val date: String,
    val time: String,
    val type: String,
    val status: AppointmentStatus,
    val notes: String = ""
)

data class ChatMessage(
    val id: String,
    val doctorId: Int,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromUser: Boolean
)
