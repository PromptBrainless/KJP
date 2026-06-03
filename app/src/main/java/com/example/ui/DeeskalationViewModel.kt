package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CrisisPlan
import com.example.data.DeeskalationRepository
import com.example.data.IncidentReview
import com.example.data.CmsSection
import com.example.data.TeamLearning
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class BreathingPhase {
    IDLE, INHALE, EXHALE
}

class DeeskalationViewModel(private val repository: DeeskalationRepository) : ViewModel() {

    // Database flows
    val allCrisisPlans: StateFlow<List<CrisisPlan>> = repository.allCrisisPlans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allIncidentReviews: StateFlow<List<IncidentReview>> = repository.allIncidentReviews
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allCmsSections: StateFlow<List<CmsSection>> = repository.allCmsSections
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allTeamLearnings: StateFlow<List<TeamLearning>> = repository.allTeamLearnings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Interactive UI search & filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryTab = MutableStateFlow("HANDBUCH") // HANDBUCH, TOOLS, ADMIN
    val selectedCategoryTab: StateFlow<String> = _selectedCategoryTab.asStateFlow()

    private val _toolsMainTab = MutableStateFlow("COREG_SKILLS")
    val toolsMainTab: StateFlow<String> = _toolsMainTab.asStateFlow()

    private val _toolsSubTab = MutableStateFlow("BREATHING")
    val toolsSubTab: StateFlow<String> = _toolsSubTab.asStateFlow()

    private val _selectedPhaseId = MutableStateFlow("GELB")
    val selectedPhaseId: StateFlow<String> = _selectedPhaseId.asStateFlow()

    private val _selectedDiagnosisId = MutableStateFlow("EIPS")
    val selectedDiagnosisId: StateFlow<String> = _selectedDiagnosisId.asStateFlow()

    // Breathing trainer state flow
    private val _breathingPhase = MutableStateFlow(BreathingPhase.IDLE)
    val breathingPhase: StateFlow<BreathingPhase> = _breathingPhase.asStateFlow()

    private val _breathingSecondsLeft = MutableStateFlow(0)
    val breathingSecondsLeft: StateFlow<Int> = _breathingSecondsLeft.asStateFlow()

    private val _breathingCycleCount = MutableStateFlow(0)
    val breathingCycleCount: StateFlow<Int> = _breathingCycleCount.asStateFlow()

    private var breathingJob: Job? = null

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategoryTab(tab: String) {
        _selectedCategoryTab.value = tab
    }

    fun setToolsMainTab(tab: String) {
        _toolsMainTab.value = tab
        _toolsSubTab.value = when (tab) {
            "COREG_SKILLS" -> "BREATHING"
            "TEAM_STATION" -> "ASSIST_ALERT"
            "LEARN_REVIEWS" -> "CASE_SIMS"
            else -> "BREATHING"
        }
    }

    fun setToolsSubTab(tab: String) {
        _toolsSubTab.value = tab
    }

    fun navigateToTools(mainTab: String, subTab: String) {
        _selectedCategoryTab.value = "TOOLS"
        _toolsMainTab.value = mainTab
        _toolsSubTab.value = subTab
    }

    fun setSelectedPhaseId(phaseId: String) {
        _selectedPhaseId.value = phaseId
    }

    fun setSelectedDiagnosisId(diagId: String) {
        _selectedDiagnosisId.value = diagId
    }

    // Breathing co-regulation logic
    fun startBreathing() {
        stopBreathing()
        _breathingCycleCount.value = 0
        breathingJob = viewModelScope.launch {
            while (true) {
                // Inhale for 4 seconds
                _breathingPhase.value = BreathingPhase.INHALE
                for (s in 4 downTo 1) {
                    _breathingSecondsLeft.value = s
                    delay(1000)
                }
                
                // Exhale for 8 seconds (activation of parasympathetic system)
                _breathingPhase.value = BreathingPhase.EXHALE
                for (s in 8 downTo 1) {
                    _breathingSecondsLeft.value = s
                    delay(1000)
                }
                _breathingCycleCount.value += 1
            }
        }
    }

    fun stopBreathing() {
        breathingJob?.cancel()
        breathingJob = null
        _breathingPhase.value = BreathingPhase.IDLE
        _breathingSecondsLeft.value = 0
    }

    // Database writes
    fun saveCrisisPlan(
        initials: String,
        diagnosisId: String,
        trigger: String,
        warningSigns: String,
        calming: String,
        worsening: String
    ) {
        viewModelScope.launch {
            repository.insertCrisisPlan(
                CrisisPlan(
                    patientInitials = initials,
                    mainDiagnosis = diagnosisId,
                    individualTrigger = trigger,
                    earlyWarningSigns = warningSigns,
                    preferredCalming = calming,
                    whatVerschlimmert = worsening
                )
            )
        }
    }

    fun deleteCrisisPlan(id: Int) {
        viewModelScope.launch {
            repository.deleteCrisisPlanById(id)
        }
    }

    fun saveIncidentReview(
        initials: String,
        dateString: String,
        descr: String,
        trig: String,
        strengths: String,
        lessons: String,
        wellbeing: String
    ) {
        viewModelScope.launch {
            repository.insertIncidentReview(
                IncidentReview(
                    patientInitials = initials,
                    incidentDate = dateString,
                    description = descr,
                    triggerSource = trig,
                    teamStrengths = strengths,
                    lessonsLearned = lessons,
                    teamWellbeing = wellbeing
                )
            )
        }
    }

    fun deleteIncidentReview(id: Int) {
        viewModelScope.launch {
            repository.deleteIncidentReviewById(id)
        }
    }

    // CMS Actions
    fun saveCmsSection(
        id: Int = 0,
        title: String,
        description: String,
        contentText: String,
        imageUrl: String,
        accentColorHex: String,
        phaseId: String
    ) {
        viewModelScope.launch {
            repository.insertCmsSection(
                CmsSection(
                    id = id,
                    title = title,
                    description = description,
                    contentText = contentText,
                    imageUrl = imageUrl,
                    accentColorHex = accentColorHex,
                    phaseId = phaseId
                )
            )
        }
    }

    fun deleteCmsSection(id: Int) {
        viewModelScope.launch {
            repository.deleteCmsSectionById(id)
        }
    }

    // Team Learning Actions ("Was hat funktioniert?")
    fun saveTeamLearning(situation: String, whatWorked: String, submittedByRole: String) {
        viewModelScope.launch {
            repository.insertTeamLearning(
                TeamLearning(
                    situation = situation,
                    whatWorked = whatWorked,
                    submittedByRole = submittedByRole
                )
            )
        }
    }

    fun deleteTeamLearning(id: Int) {
        viewModelScope.launch {
            repository.deleteTeamLearningById(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopBreathing()
    }
}

class DeeskalationViewModelFactory(private val repository: DeeskalationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeeskalationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeeskalationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
