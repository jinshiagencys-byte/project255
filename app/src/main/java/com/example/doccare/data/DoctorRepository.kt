package com.example.doccare.data

import android.content.Context
import com.example.doccare.R
import com.example.doccare.model.Appointment
import com.example.doccare.model.AppointmentStatus
import com.example.doccare.model.ChatMessage
import com.example.doccare.model.DatabaseDto
import com.example.doccare.model.Doctor
import com.example.doccare.model.DoctorCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import java.util.UUID

class DoctorRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val _categories = MutableStateFlow<List<DoctorCategory>>(emptyList())
    val categories: StateFlow<List<DoctorCategory>> = _categories.asStateFlow()

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> = _doctors.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _chatMessages = MutableStateFlow<Map<Int, List<ChatMessage>>>(emptyMap())
    val chatMessages: StateFlow<Map<Int, List<ChatMessage>>> = _chatMessages.asStateFlow()

    init {
        loadInitialData()
    }

    private fun translateCategoryName(name: String): String {
        return when (name.lowercase().trim()) {
            "cardiology", "cardiologie" -> "Cardiologie"
            "dentistry", "dentisterie" -> "Dentisterie"
            "neurology", "neurologie" -> "Neurologie"
            "orthopedics", "orthopédie", "orthopedie" -> "Orthopédie"
            "radiology", "radiologie" -> "Radiologie"
            else -> name
        }
    }

    private fun translateSpecialty(specialty: String): String {
        val s = specialty.lowercase()
        return when {
            s.contains("cardio") -> "Cardiologie"
            s.contains("dentist") || s.contains("dent") -> "Chirurgie dentaire"
            s.contains("neuro") -> "Neurologie clinique"
            s.contains("ortho") -> "Chirurgie orthopédique"
            s.contains("radio") -> "Radiologie & Imagerie"
            else -> specialty
        }
    }

    private fun translateBiography(docName: String, originalBio: String?): String {
        return when {
            docName.contains("Michael", ignoreCase = true) ->
                "Chirurgien orthopédiste avec plus de 20 ans d'expérience. Spécialisé dans la chirurgie articulaire, la traumatologie sportive et la prise en charge de l'arthrose."
            docName.contains("Sarah", ignoreCase = true) ->
                "Cardiologue spécialisée dans la prévention cardiovasculaire, les bilans d'effort et le suivi des arythmies. Consultations au cabinet et téléconsultations."
            docName.contains("David", ignoreCase = true) ->
                "Neurologue praticien hospitalier avec 15 ans d'expérience. Spécialiste des troubles du sommeil, migraines chroniques et affections neurovasculaires."
            docName.contains("Jessica", ignoreCase = true) ->
                "Médecin radiologue spécialisée en échographie, IRM et radiologie interventionnelle diagnostique de pointe."
            docName.contains("Richard", ignoreCase = true) ->
                "Chirurgien-dentiste conventionné, spécialisé dans l'implantologie, la parodontologie et l'esthétique du sourire avec équipements 3D de dernière génération."
            else -> originalBio ?: "Praticien qualifié et certifié dédié aux soins des patients."
        }
    }

    private fun loadInitialData() {
        var parsedCategories = emptyList<DoctorCategory>()
        var parsedDoctors = emptyList<Doctor>()

        try {
            val jsonString = context.assets.open("database.json").bufferedReader().use { it.readText() }
            val dbDto = json.decodeFromString<DatabaseDto>(jsonString)

            parsedCategories = dbDto.Category.map { catDto ->
                val localRes = when (catDto.Name.lowercase()) {
                    "cardiology" -> R.drawable.cat_cardiology
                    "dentistry" -> R.drawable.cat_dentistry
                    "neurology" -> R.drawable.cat_neurology
                    "orthopedics" -> R.drawable.cat_orthopedics
                    "radiology" -> R.drawable.cat_radiology
                    else -> R.drawable.doctor
                }
                DoctorCategory(
                    id = catDto.Id,
                    name = translateCategoryName(catDto.Name),
                    pictureUrl = catDto.Picture,
                    localDrawableRes = localRes
                )
            }

            parsedDoctors = dbDto.Doctors.filterNotNull().map { docDto ->
                val docName = docDto.Name ?: "Docteur"
                val localRes = when {
                    docName.contains("Michael", ignoreCase = true) -> R.drawable.dr_michael_roberts
                    docName.contains("Sarah", ignoreCase = true) -> R.drawable.dr_sarah_thompson
                    docName.contains("David", ignoreCase = true) -> R.drawable.dr_david_johnson
                    docName.contains("Jessica", ignoreCase = true) -> R.drawable.dr_jessica_wyne
                    docName.contains("Richard", ignoreCase = true) -> R.drawable.dr_richard_lee
                    else -> R.drawable.doctor
                }

                val specialty = translateSpecialty(docDto.Special ?: "Médecine générale")
                val bio = translateBiography(docName, docDto.Biography)

                // French address formatting
                val addressFr = when (docDto.Id) {
                    0 -> "14 Rue de la Paix, 75002 Paris"
                    1 -> "28 Boulevard Haussmann, 75009 Paris"
                    2 -> "5 Place Bellecour, 69002 Lyon"
                    4 -> "12 Avenue Jean Médecin, 06000 Nice"
                    5 -> "8 Cours Victor Hugo, 33000 Bordeaux"
                    else -> "8502 Rue Pasteur, 75015 Paris"
                }

                Doctor(
                    id = docDto.Id ?: 0,
                    name = docName,
                    specialty = specialty,
                    biography = bio,
                    experienceYears = docDto.Expriense ?: 8,
                    rating = docDto.Rating ?: 4.6,
                    patientsCount = docDto.Patiens ?: "600+",
                    address = addressFr,
                    mobile = "01 42 68 55 00",
                    website = docDto.Site ?: "https://www.doccare.fr",
                    locationUrl = docDto.Location ?: "https://maps.google.com",
                    pictureUrl = docDto.Picture ?: "",
                    localDrawableRes = localRes,
                    isFavorite = false
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fallback default dataset if empty
        if (parsedCategories.isEmpty()) {
            parsedCategories = listOf(
                DoctorCategory(0, "Cardiologie", "", R.drawable.cat_cardiology),
                DoctorCategory(1, "Dentisterie", "", R.drawable.cat_dentistry),
                DoctorCategory(2, "Neurologie", "", R.drawable.cat_neurology),
                DoctorCategory(3, "Orthopédie", "", R.drawable.cat_orthopedics),
                DoctorCategory(4, "Radiologie", "", R.drawable.cat_radiology)
            )
        }

        if (parsedDoctors.isEmpty()) {
            parsedDoctors = listOf(
                Doctor(
                    id = 0,
                    name = "Dr. Michael Roberts",
                    specialty = "Chirurgie orthopédique",
                    biography = "Chirurgien orthopédiste spécialisé dans les pathologies du genou et de la hanche.",
                    experienceYears = 20,
                    rating = 4.8,
                    patientsCount = "1200+",
                    address = "14 Rue de la Paix, 75002 Paris",
                    mobile = "01 42 68 55 00",
                    website = "https://www.doccare.fr",
                    locationUrl = "http://maps.google.com/maps?q=loc:48.8698,2.3308",
                    pictureUrl = "",
                    localDrawableRes = R.drawable.dr_michael_roberts
                ),
                Doctor(
                    id = 1,
                    name = "Dr. Sarah Thompson",
                    specialty = "Cardiologie",
                    biography = "Cardiologue conventionnée secteur 1 avec expertise en prévention et hypertension.",
                    experienceYears = 14,
                    rating = 4.9,
                    patientsCount = "950+",
                    address = "28 Boulevard Haussmann, 75009 Paris",
                    mobile = "01 42 68 55 00",
                    website = "https://www.doccare.fr",
                    locationUrl = "http://maps.google.com/maps?q=loc:48.8722,2.3361",
                    pictureUrl = "",
                    localDrawableRes = R.drawable.dr_sarah_thompson
                )
            )
        }

        _categories.value = parsedCategories
        _doctors.value = parsedDoctors

        // Initial appointments in French
        val initialAppointments = mutableListOf<Appointment>()
        if (parsedDoctors.isNotEmpty()) {
            val firstDoc = parsedDoctors[0]
            initialAppointments.add(
                Appointment(
                    id = UUID.randomUUID().toString(),
                    doctorId = firstDoc.id,
                    doctorName = firstDoc.name,
                    specialty = firstDoc.specialty,
                    doctorImageRes = firstDoc.localDrawableRes,
                    date = "Demain, 10h30",
                    time = "10h30",
                    type = "Au cabinet médical",
                    status = AppointmentStatus.UPCOMING,
                    notes = "Consultation de contrôle et renouvellement d'ordonnance"
                )
            )
            if (parsedDoctors.size > 1) {
                val secondDoc = parsedDoctors[1]
                initialAppointments.add(
                    Appointment(
                        id = UUID.randomUUID().toString(),
                        doctorId = secondDoc.id,
                        doctorName = secondDoc.name,
                        specialty = secondDoc.specialty,
                        doctorImageRes = secondDoc.localDrawableRes,
                        date = "Lundi dernier, 14h00",
                        time = "14h00",
                        type = "Téléconsultation vidéo",
                        status = AppointmentStatus.COMPLETED,
                        notes = "Bilan cardiologique annuel - constantes dans les normes"
                    )
                )
            }
        }
        _appointments.value = initialAppointments

        // Prepopulate introductory French doctor chats
        val initialChats = mutableMapOf<Int, List<ChatMessage>>()
        parsedDoctors.forEach { doc ->
            initialChats[doc.id] = listOf(
                ChatMessage(
                    id = UUID.randomUUID().toString(),
                    doctorId = doc.id,
                    text = "Bonjour ! Je suis le ${doc.name}, praticien en ${doc.specialty}. Bienvenue sur votre espace DocCare. N'hésitez pas à m'écrire si vous avez des questions avant votre rendez-vous.",
                    timestamp = System.currentTimeMillis() - 3600000,
                    isFromUser = false
                )
            )
        }
        _chatMessages.value = initialChats
    }

    fun toggleFavorite(doctorId: Int) {
        _doctors.value = _doctors.value.map { doc ->
            if (doc.id == doctorId) doc.copy(isFavorite = !doc.isFavorite) else doc
        }
    }

    fun bookAppointment(
        doctor: Doctor,
        date: String,
        time: String,
        type: String,
        notes: String
    ): Appointment {
        val newAppointment = Appointment(
            id = UUID.randomUUID().toString(),
            doctorId = doctor.id,
            doctorName = doctor.name,
            specialty = doctor.specialty,
            doctorImageRes = doctor.localDrawableRes,
            date = "$date à $time",
            time = time,
            type = type,
            status = AppointmentStatus.UPCOMING,
            notes = notes
        )
        _appointments.value = listOf(newAppointment) + _appointments.value
        return newAppointment
    }

    fun cancelAppointment(appointmentId: String) {
        _appointments.value = _appointments.value.map { appt ->
            if (appt.id == appointmentId) appt.copy(status = AppointmentStatus.CANCELLED) else appt
        }
    }

    fun rescheduleAppointment(appointmentId: String, newDate: String, newTime: String) {
        _appointments.value = _appointments.value.map { appt ->
            if (appt.id == appointmentId) {
                appt.copy(
                    date = "$newDate à $newTime",
                    time = newTime,
                    status = AppointmentStatus.UPCOMING
                )
            } else appt
        }
    }

    fun sendUserMessage(doctorId: Int, text: String) {
        val currentList = _chatMessages.value[doctorId] ?: emptyList()
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            doctorId = doctorId,
            text = text,
            timestamp = System.currentTimeMillis(),
            isFromUser = true
        )
        val updated = currentList + userMsg

        // Automated smart doctor response in French
        val doctorName = _doctors.value.find { it.id == doctorId }?.name ?: "Le praticien"
        val docReply = ChatMessage(
            id = UUID.randomUUID().toString(),
            doctorId = doctorId,
            text = "Bonjour, j'ai bien pris connaissance de votre message pour $doctorName. Si vos symptômes nécessitent une consultation immédiate, pensez à réserver un créneau d'urgence ou à contacter le 15 (SAMU).",
            timestamp = System.currentTimeMillis() + 800,
            isFromUser = false
        )

        _chatMessages.value = _chatMessages.value + (doctorId to (updated + docReply))
    }
}
