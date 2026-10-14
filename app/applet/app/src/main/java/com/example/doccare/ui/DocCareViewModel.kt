package com.example.doccare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doccare.data.DoctorRepository
import com.example.doccare.model.Appointment
import com.example.doccare.model.AppointmentStatus
import com.example.doccare.model.ChatMessage
import com.example.doccare.model.Doctor
import com.example.doccare.model.DoctorCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab {
    HOME,
    APPOINTMENTS,
    MESSAGES,
    PROFILE
}

enum class DoctorFilter {
    ALL,
    TOP_RATED,
    HIGH_EXPERIENCE
}

data class DocCareUiState(
    val categories: List<DoctorCategory> = emptyList(),
    val allDoctors: List<Doctor> = emptyList(),
    val filteredDoctors: List<Doctor> = emptyList(),
    val appointments: List<Appointment> = emptyList(),
    val chatMessages: Map<Int, List<ChatMessage>> = emptyMap(),
    val searchQuery: String = "",
    val selectedCategoryName: String? = null,
    val selectedFilter: DoctorFilter = DoctorFilter.ALL,
    val selectedDoctor: Doctor? = null,
    val activeTab: NavigationTab = NavigationTab.HOME,
    val isOnboardingCompleted: Boolean = true, // Default to true or can toggle
    val activeAppointmentStatus: AppointmentStatus = AppointmentStatus.UPCOMING,
    val bookingSuccessMessage: String? = null
)

class DocCareViewModel(private val repository: DoctorRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategoryName = MutableStateFlow<String?>(null)
    private val _selectedFilter = MutableStateFlow(DoctorFilter.ALL)
    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    private val _activeTab = MutableStateFlow(NavigationTab.HOME)
    private val _isOnboardingCompleted = MutableStateFlow(true)
    private val _activeAppointmentStatus = MutableStateFlow(AppointmentStatus.UPCOMING)
    private val _bookingSuccessMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DocCareUiState> = combine(
        repository.categories,
        repository.doctors,
        repository.appointments,
        repository.chatMessages,
        _searchQuery,
        _selectedCategoryName,
        _selectedFilter,
        _selectedDoctor,
        _activeTab,
        _isOnboardingCompleted,
        _activeAppointmentStatus,
        _bookingSuccessMessage
    ) { args ->
        val categories = args[0] as List<DoctorCategory>
        val doctors = args[1] as List<Doctor>
        val appointments = args[2] as List<Appointment>
        val chatMessages = args[3] as Map<Int, List<ChatMessage>>
        val searchQuery = args[4] as String
        val selectedCategory = args[5] as String?
        val selectedFilter = args[6] as DoctorFilter
        val selectedDoctor = args[7] as Doctor?
        val activeTab = args[8] as NavigationTab
        val isOnboardingCompleted = args[9] as Boolean
        val activeAppointmentStatus = args[10] as AppointmentStatus
        val bookingSuccessMessage = args[11] as String?

        val filtered = doctors.filter { doctor ->
            val matchesCategory = selectedCategory == null || doctor.specialty.contains(selectedCategory, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                    doctor.name.contains(searchQuery, ignoreCase = true) ||
                    doctor.specialty.contains(searchQuery, ignoreCase = true) ||
                    doctor.address.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesQuery
        }.let { list ->
            when (selectedFilter) {
                DoctorFilter.ALL -> list
                DoctorFilter.TOP_RATED -> list.sortedByDescending { it.rating }
                DoctorFilter.HIGH_EXPERIENCE -> list.sortedByDescending { it.experienceYears }
            }
        }

        // Keep selected doctor in sync if favorites or data changes
        val updatedSelectedDoctor = selectedDoctor?.let { current ->
            doctors.find { it.id == current.id } ?: current
        }

        DocCareUiState(
            categories = categories,
            allDoctors = doctors,
            filteredDoctors = filtered,
            appointments = appointments,
            chatMessages = chatMessages,
            searchQuery = searchQuery,
            selectedCategoryName = selectedCategory,
            selectedFilter = selectedFilter,
            selectedDoctor = updatedSelectedDoctor,
            activeTab = activeTab,
            isOnboardingCompleted = isOnboardingCompleted,
            activeAppointmentStatus = activeAppointmentStatus,
            bookingSuccessMessage = bookingSuccessMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DocCareUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(categoryName: String?) {
        _selectedCategoryName.value = if (_selectedCategoryName.value == categoryName) null else categoryName
    }

    fun onFilterSelected(filter: DoctorFilter) {
        _selectedFilter.value = filter
    }

    fun selectDoctor(doctor: Doctor?) {
        _selectedDoctor.value = doctor
    }

    fun setNavigationTab(tab: NavigationTab) {
        _activeTab.value = tab
        _selectedDoctor.value = null // Back to list when changing tab
    }

    fun setAppointmentStatusFilter(status: AppointmentStatus) {
        _activeAppointmentStatus.value = status
    }

    fun toggleFavorite(doctorId: Int) {
        repository.toggleFavorite(doctorId)
    }

    fun bookAppointment(doctor: Doctor, date: String, time: String, type: String, notes: String = "") {
        repository.bookAppointment(doctor, date, time, type, notes)
        _bookingSuccessMessage.value = "Your appointment with ${doctor.name} has been booked!"
        _activeTab.value = NavigationTab.APPOINTMENTS
        _activeAppointmentStatus.value = AppointmentStatus.UPCOMING
        _selectedDoctor.value = null
    }

    fun cancelAppointment(appointmentId: String) {
        repository.cancelAppointment(appointmentId)
    }

    fun rescheduleAppointment(appointmentId: String, newDate: String, newTime: String) {
        repository.rescheduleAppointment(appointmentId, newDate, newTime)
    }

    fun clearBookingSuccess() {
        _bookingSuccessMessage.value = null
    }

    fun sendChatMessage(doctorId: Int, message: String) {
        if (message.isNotBlank()) {
            repository.sendUserMessage(doctorId, message.trim())
        }
    }

    fun completeOnboarding() {
        _isOnboardingCompleted.value = true
    }
}
