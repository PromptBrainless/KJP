package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CrisisPlan
import com.example.data.DeeskalationRepository
import com.example.data.IncidentReview
import com.example.data.CmsSection
import com.example.data.TeamLearning
import com.example.data.IcdDiagnosis
import com.example.data.IcdSearchEntity
import com.example.data.IcdApiRepository
import com.example.data.GeminiApiRepository
import com.example.data.HandoverReport
import com.example.data.StrategyRating
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log

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

    val allHandoverReports: StateFlow<List<HandoverReport>> = repository.allHandoverReports
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allStrategyRatings: StateFlow<List<StrategyRating>> = repository.allStrategyRatings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val topStrategiesLast7Days: StateFlow<List<Pair<String, Int>>> = repository.allStrategyRatings
        .map { ratings ->
            val sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000L)
            ratings.filter { it.timestamp >= sevenDaysAgo && it.isHelpful }
                .groupBy { it.strategyName }
                .mapValues { it.value.size }
                .toList()
                .sortedByDescending { it.second }
                .take(3)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ICD Live API & Customized Database States
    private val icdApiRepository = IcdApiRepository()

    val allIcdDiagnoses: StateFlow<List<IcdDiagnosis>> = repository.allIcdDiagnoses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _icdSearchResults = MutableStateFlow<List<IcdSearchEntity>>(emptyList())
    val icdSearchResults: StateFlow<List<IcdSearchEntity>> = _icdSearchResults.asStateFlow()

    private val _icdSearchInProgress = MutableStateFlow(false)
    val icdSearchInProgress: StateFlow<Boolean> = _icdSearchInProgress.asStateFlow()

    private val _icdSearchError = MutableStateFlow<String?>(null)
    val icdSearchError: StateFlow<String?> = _icdSearchError.asStateFlow()

    init {
        seedIcdDiagnosesIfEmpty()
    }

    private fun seedIcdDiagnosesIfEmpty() {
        viewModelScope.launch {
            try {
                // Read the first emission of allIcdDiagnoses securely to avoid infinite loop
                val existing = repository.allIcdDiagnoses.first()
                if (existing.isEmpty()) {
                    Log.d("DeeskalationViewModel", "Local ICD database is empty. Preheating with psychiatric deescalation templates.")
                    
                    // 1. Seed from hardcoded pediatric psychiatry diagnoses first
                    com.example.data.ScientificContent.diagnoses.forEach { d ->
                        val code = when (d.id) {
                            "ADHS" -> "6A05"
                            "EIPS" -> "6D11.0"
                            "PTBS" -> "6B40"
                            "ASS" -> "6A02"
                            "Psychose" -> "6A20"
                            "Sozialverhalten" -> "6A06"
                            else -> ""
                        }
                        repository.insertIcdDiagnosis(
                            IcdDiagnosis(
                                codeOrId = d.id,
                                code = code,
                                name = d.name,
                                dynamik = d.dynamik,
                                absicherung = d.absicherung,
                                klaerung = d.klaerung,
                                aufloesung = d.aufloesung,
                                customNotes = "Standard-Krankheitsbild im Handbuch.",
                                isCustom = false
                            )
                        )
                    }

                    // 2. Add extra highly common ICD clinical syndromes
                    repository.insertIcdDiagnosis(
                        IcdDiagnosis(
                            codeOrId = "6A70.1",
                            code = "6A70.1",
                            name = "Mittelgradige depressive Episode",
                            dynamik = "Eskalationen entspringen oft tiefer Verzweiflung, Resignation oder Reizüberflutung. Aggressive Symptome treten meist als dysfunktionaler Schutz vor Hilflosigkeit oder Überforderung auf.",
                            absicherung = "Absolute Reizarmut etablieren · Jeglichen Druck herausnehmen · Keine lauten Töne oder schnellen/hektischen Bewegungen.",
                            klaerung = "Innere Not validieren: 'Ich merke, wie unerträglich das gerade ist. Ich bin hier und bleibe ruhig.' · Keine falschen Ratschläge.",
                            aufloesung = "Sehr einfache, niedrigschwellige Aktivierungs-Wahlmöglichkeiten anbieten. Schritte extrem klein halten.",
                            customNotes = "Häufiges psychiatrisches Krankheitsbild mit vegetativer Erschöpfung.",
                            isCustom = false
                        )
                    )

                    repository.insertIcdDiagnosis(
                        IcdDiagnosis(
                            codeOrId = "6B01.1",
                            code = "6B01.1",
                            name = "Panikstörung / Akute Agitierte Angst",
                            dynamik = "Akute Todesangst triggert den Sympathikus maximal (Flucht- oder Kampfsyndrom). Das Kind/Adoleszente reagiert hyperaktiv, schreckhaft oder hochaggressiv bei Einengung.",
                            absicherung = "Niemals den physischen Fluchtweg blockieren oder den Raum verengen · Körperkontakt absolut vermeiden · Beruhigend sprechen.",
                            klaerung = "Therapeutisches Atem-Matching (Paced Breathing 4-8s) des Teams sichtbar einsetzen. Reize minimieren.",
                            aufloesung = "Sicherheit bestätigen: 'Du bist in Sicherheit, dein Herz schlägt nur schnell.' · Kühles Wasser anbieten.",
                            customNotes = "Strikter Einsatz von Co-Regulation über Atmung indiziert.",
                            isCustom = false
                        )
                    )

                    repository.insertIcdDiagnosis(
                        IcdDiagnosis(
                            codeOrId = "6C50",
                            code = "6C50",
                            name = "Suchtmedizin / Akute Intoxikation",
                            dynamik = "Einschränkungen der Impulskontrolle und Urteilsfähigkeit durch Substanzwirkung (z.B. Alkohol, Cannabis, Amphetamine). Hohe paradoxe Aggressionsneigung.",
                            absicherung = "Sicherheitsabstand vergrößern · Keine logischen Diskussionen führen · Klare, kurze Warnungen bei Eigengefährdung.",
                            klaerung = "Grenzen unemotional und unmissverständlich setzen · Ablenkung auf primäre vitale Bedürfnisse (Schlaf, Essen, Trinken) lenken.",
                            aufloesung = "Substanzabbau in geschützter, reizarmer Umgebung abwarten. Vitalparameter (Pupillen, Atmung) im Blick behalten.",
                            customNotes = "Alkohol- und Amphetamin-induzierte Psychofreie Entgleisungen erfordern Distanz.",
                            isCustom = false
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("DeeskalationViewModel", "Failed during ICD preheating/seeding: ${e.message}", e)
            }
        }
    }

    // Interactive UI search & filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryTab = MutableStateFlow("HANDBUCH") // HANDBUCH, TOOLS, ADMIN, ICD_WORKSPACE
    val selectedCategoryTab: StateFlow<String> = _selectedCategoryTab.asStateFlow()

    // Gemini API Companionship states
    private val geminiRepository = GeminiApiRepository()
    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

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
    private var icdSearchJob: Job? = null

    fun searchIcdWebOrLocal(query: String) {
        _searchQuery.value = query
        if (query.trim().isEmpty()) {
            icdSearchJob?.cancel()
            _icdSearchResults.value = emptyList()
            _icdSearchInProgress.value = false
            return
        }

        icdSearchJob?.cancel()
        _icdSearchInProgress.value = true
        _icdSearchError.value = null
        
        icdSearchJob = viewModelScope.launch {
            try {
                // Safely read WHO client keys from Secrets-injected BuildConfig
                val clientId = com.example.BuildConfig.ICD_CLIENT_ID
                val clientSecret = com.example.BuildConfig.ICD_CLIENT_SECRET
                
                val results = icdApiRepository.searchWebIcd(clientId, clientSecret, query)
                _icdSearchResults.value = results
                if (results.isEmpty()) {
                    Log.d("DeeskalationViewModel", "No live Results returned. Local database search remains active.")
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    throw e
                }
                Log.e("DeeskalationViewModel", "WHO live API search failed: ${e.message}")
                _icdSearchError.value = "Konnte ICD-Katalog nicht laden: ${e.localizedMessage}"
            } finally {
                if (icdSearchJob == coroutineContext[Job]) {
                    _icdSearchInProgress.value = false
                }
            }
        }
    }

    fun saveIcdDiagnosis(
        codeOrId: String,
        code: String,
        name: String,
        dynamik: String,
        absicherung: String,
        klaerung: String,
        aufloesung: String,
        customNotes: String,
        isCustom: Boolean = false
    ) {
        viewModelScope.launch {
            repository.insertIcdDiagnosis(
                IcdDiagnosis(
                    codeOrId = codeOrId,
                    code = code,
                    name = name,
                    dynamik = dynamik,
                    absicherung = absicherung,
                    klaerung = klaerung,
                    aufloesung = aufloesung,
                    customNotes = customNotes,
                    isCustom = isCustom
                )
            )
        }
    }

    fun deleteIcdDiagnosis(codeOrId: String) {
        viewModelScope.launch {
            repository.deleteIcdDiagnosisById(codeOrId)
        }
    }

    fun importIcdSearchEntity(entity: IcdSearchEntity) {
        viewModelScope.launch {
            val existing = repository.getIcdDiagnosisById(entity.id)
            if (existing == null) {
                repository.insertIcdDiagnosis(
                    IcdDiagnosis(
                        codeOrId = entity.id,
                        code = entity.theCode ?: "",
                        name = entity.title,
                        dynamik = entity.definition ?: "Inportiertes Krankheitsbild aus dem ICD-11 WHO Register. Perfekt für deeskalatives Arbeiten.",
                        absicherung = "Säule 1 (Absicherung): Druck reduzieren, Reizarme Umgebung herstellen, Ruhe vermitteln.",
                        klaerung = "Säule 2 (Klärung): Symptome anerkennen, Bedürfnisse erfragen, Gefühlsdynamik deeskalieren.",
                        aufloesung = "Säule 3 (Auflösung): Kleine Erfolge stabilisieren, klare Wahlmöglichkeiten vorlegen.",
                        customNotes = "Live importiert aus ICD-11 WHO API.",
                        isCustom = true
                    )
                )
            }
        }
    }

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
        worsening: String,
        id: Int = 0
    ) {
        viewModelScope.launch {
            repository.insertCrisisPlan(
                CrisisPlan(
                    id = id,
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
        wellbeing: String,
        id: Int = 0
    ) {
        viewModelScope.launch {
            repository.insertIncidentReview(
                IncidentReview(
                    id = id,
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
    fun saveTeamLearning(situation: String, whatWorked: String, submittedByRole: String, id: Int = 0) {
        viewModelScope.launch {
            repository.insertTeamLearning(
                TeamLearning(
                    id = id,
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

    // Interactive conversational companion method
    fun consultCompanion(situation: String, diagnosisId: String, phaseId: String) {
        _aiLoading.value = true
        _aiResponse.value = null
        viewModelScope.launch {
            try {
                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                val response = geminiRepository.consultCompanion(apiKey, situation, diagnosisId, phaseId)
                _aiResponse.value = response
            } catch (e: Exception) {
                _aiResponse.value = "Klinischer Fehler bei der API-Konsultation: ${e.localizedMessage}"
            } finally {
                _aiLoading.value = false
            }
        }
    }

    fun saveHandoverReport(
        phase: String,
        diagnosis: String,
        strategies: String,
        result: String,
        outcome: String,
        roomNumber: String = ""
    ) {
        viewModelScope.launch {
            repository.insertHandoverReport(
                HandoverReport(
                    phase = phase,
                    diagnosis = diagnosis,
                    strategies = strategies,
                    result = result.take(200),
                    outcome = outcome,
                    roomNumber = roomNumber
                )
            )
        }
    }

    fun deleteHandoverReport(id: Int) {
        viewModelScope.launch {
            repository.deleteHandoverReportById(id)
        }
    }

    fun submitStrategyRating(strategyName: String, isHelpful: Boolean, stationId: String = "Allgemein") {
        viewModelScope.launch {
            repository.insertStrategyRating(
                StrategyRating(
                    strategyName = strategyName,
                    isHelpful = isHelpful,
                    stationId = stationId
                )
            )
        }
    }

    fun clearAiConsultation() {
        _aiResponse.value = null
        _aiLoading.value = false
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
