package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun DeeskalationApp(viewModel: DeeskalationViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedCategoryTab.collectAsStateWithLifecycle()
    val selectedPhaseId by viewModel.selectedPhaseId.collectAsStateWithLifecycle()
    val selectedDiagnosisId by viewModel.selectedDiagnosisId.collectAsStateWithLifecycle()
    val crisisPlans by viewModel.allCrisisPlans.collectAsStateWithLifecycle()
    val incidentReviews by viewModel.allIncidentReviews.collectAsStateWithLifecycle()
    val cmsSections by viewModel.allCmsSections.collectAsStateWithLifecycle()
    val teamLearnings by viewModel.allTeamLearnings.collectAsStateWithLifecycle()

    val icdDiagnoses by viewModel.allIcdDiagnoses.collectAsStateWithLifecycle()
    val icdSearchResults by viewModel.icdSearchResults.collectAsStateWithLifecycle()
    val icdSearchInProgress by viewModel.icdSearchInProgress.collectAsStateWithLifecycle()
    val icdSearchError by viewModel.icdSearchError.collectAsStateWithLifecycle()

    val breathingPhase by viewModel.breathingPhase.collectAsStateWithLifecycle()
    val breathingSeconds by viewModel.breathingSecondsLeft.collectAsStateWithLifecycle()
    val breathingCycles by viewModel.breathingCycleCount.collectAsStateWithLifecycle()
    val toolsMainTab by viewModel.toolsMainTab.collectAsStateWithLifecycle()
    val toolsSubTab by viewModel.toolsSubTab.collectAsStateWithLifecycle()

    var showAdminUnlockDialog by remember { mutableStateOf(false) }
    var adminPinInput by remember { mutableStateOf("") }
    var isAdminUnlocked by remember { mutableStateOf(false) }
    var adminDialogErrorMessage by remember { mutableStateOf("") }

    if (showAdminUnlockDialog) {
        AlertDialog(
            onDismissRequest = {
                showAdminUnlockDialog = false
                adminPinInput = ""
                adminDialogErrorMessage = ""
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("CMS-Admin freischalten")
                }
            },
            text = {
                Column {
                    Text(
                        "Geben Sie den Administrator-Code ein, um neue Inhaltsbereiche zu erstellen, Pfaden zuzuweisen und Texte/Bilder per CMS zu ändern.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = adminPinInput,
                        onValueChange = { adminPinInput = it },
                        label = { Text("Code (Hinweis: admin)") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("admin_pin_input")
                    )
                    if (adminDialogErrorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = adminDialogErrorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (adminPinInput == "admin") {
                            isAdminUnlocked = true
                            showAdminUnlockDialog = false
                            adminPinInput = ""
                            adminDialogErrorMessage = ""
                            viewModel.setSelectedCategoryTab("ADMIN")
                        } else {
                            adminDialogErrorMessage = "Falscher Code! Bitte verwenden Sie 'admin'."
                        }
                    },
                    modifier = Modifier.testTag("submit_admin_confirm")
                ) {
                    Text("Freischalten")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAdminUnlockDialog = false
                        adminPinInput = ""
                        adminDialogErrorMessage = ""
                    }
                ) {
                    Text("Abheben")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .clickable {
                                // Secret trigger: clicking on the title reveals the admin login dialog too!
                                showAdminUnlockDialog = true
                            }
                    ) {
                        Text(
                            text = "Deeskalation KJP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Safewards · Polyvagal · DBT · GFK · Mentalisierung",
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = {
                            if (isAdminUnlocked) {
                                // Locked if already unlocked, toggles state
                                isAdminUnlocked = false
                                viewModel.setSelectedCategoryTab("PHASEN")
                            } else {
                                showAdminUnlockDialog = true
                            }
                        },
                        modifier = Modifier.testTag("admin_lock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "CMS Admin",
                            tint = if (isAdminUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(
                        onClick = { viewModel.setSearchQuery("") },
                        modifier = Modifier.testTag("reset_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Nachrichten zurücksetzen"
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Mobile-first, Adaptive Custom Navigation Bar
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                val menuTabs = mutableListOf(
                    Triple("HANDBUCH", "Handbuch", Icons.Default.Info),
                    Triple("ICD_WORKSPACE", "ICD-Symptome", Icons.Default.Search)
                )
                if (isAdminUnlocked) {
                    menuTabs.add(Triple("ADMIN", "CMS Admin", Icons.Default.Settings))
                }
                
                menuTabs.forEach { (tabId, tabName, icon) ->
                    NavigationBarItem(
                        selected = (selectedTab == tabId) || (tabId == "HANDBUCH" && (selectedTab == "PHASEN" || selectedTab == "DIAGNOSEN" || selectedTab == "KNOWLEDGE")),
                        onClick = { viewModel.setSelectedCategoryTab(tabId) },
                        icon = { Icon(icon, contentDescription = tabName) },
                        label = { Text(tabName, fontSize = 11.sp) },
                        alwaysShowLabel = true,
                        modifier = Modifier.testTag("nav_item_${tabId.lowercase()}")
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .testTag("main_app_scaffold_content")
        ) {
            // Max Width constraint for Large screens (Tablets/Landscape)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                // Main Content Switching
                val activeTab = when (selectedTab) {
                    "PHASEN", "DIAGNOSEN", "KNOWLEDGE" -> "HANDBUCH"
                    else -> selectedTab
                }
                when (activeTab) {
                    "HANDBUCH" -> HandbuchScreen(
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        selectedPhaseId = selectedPhaseId,
                        onPhaseSelected = { viewModel.setSelectedPhaseId(it) },
                        selectedDiagnosisId = selectedDiagnosisId,
                        onDiagnosisSelected = { viewModel.setSelectedDiagnosisId(it) },
                        cmsSections = cmsSections,
                        icdDiagnoses = icdDiagnoses,
                        breathingPhase = breathingPhase,
                        breathingSeconds = breathingSeconds,
                        breathingCycles = breathingCycles,
                        onStartBreathing = { viewModel.startBreathing() },
                        onStopBreathing = { viewModel.stopBreathing() },
                        crisisPlans = crisisPlans,
                        onSaveCrisisPlan = { init, diag, trig, warn, calm, worsening ->
                            viewModel.saveCrisisPlan(init, diag, trig, warn, calm, worsening)
                        },
                        onDeleteCrisisPlan = { id -> viewModel.deleteCrisisPlan(id) },
                        incidentReviews = incidentReviews,
                        onSaveIncidentReview = { init, date, desc, trig, stren, less, wellbeing ->
                            viewModel.saveIncidentReview(init, date, desc, trig, stren, less, wellbeing)
                        },
                        onDeleteIncidentReview = { id -> viewModel.deleteIncidentReview(id) },
                        teamLearnings = teamLearnings,
                        onSaveTeamLearning = { situation, help, role ->
                            viewModel.saveTeamLearning(situation, help, role)
                        },
                        onDeleteTeamLearning = { id -> viewModel.deleteTeamLearning(id) }
                    )
                    "ICD_WORKSPACE" -> {
                        IcdSymptomWorkspaceScreen(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.searchIcdWebOrLocal(it) },
                            icdDiagnoses = icdDiagnoses,
                            searchResults = icdSearchResults,
                            searchInProgress = icdSearchInProgress,
                            searchError = icdSearchError,
                            onSaveDiagnosis = { id, code, name, dyn, abs, kla, auf, notes, custom ->
                                viewModel.saveIcdDiagnosis(id, code, name, dyn, abs, kla, auf, notes, custom)
                            },
                            onDeleteDiagnosis = { id -> viewModel.deleteIcdDiagnosis(id) },
                            onImportIcdEntity = { entity -> viewModel.importIcdSearchEntity(entity) }
                        )
                    }
                    "ADMIN" -> {
                        if (isAdminUnlocked) {
                            AdminCmsScreen(
                                cmsSections = cmsSections,
                                onSaveSection = { id, title, desc, text, url, color, phase ->
                                    viewModel.saveCmsSection(id, title, desc, text, url, color, phase)
                                },
                                onDeleteSection = { id -> viewModel.deleteCmsSection(id) }
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Bitte entsperren Sie den Admin-Bereich.")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// 1. PHASEN SCREEN
// ══════════════════════════════════════════════════════
@Composable
fun PhasenScreen(
    selectedPhaseId: String,
    onPhaseSelected: (String) -> Unit,
    cmsSections: List<CmsSection>,
    breathingPhase: BreathingPhase,
    breathingSeconds: Int,
    breathingCycles: Int,
    onStartBreathing: () -> Unit,
    onStopBreathing: () -> Unit,
    crisisPlans: List<CrisisPlan>,
    onSaveCrisisPlan: (String, String, String, String, String, String) -> Unit,
    onDeleteCrisisPlan: (Int) -> Unit,
    incidentReviews: List<IncidentReview>,
    onSaveIncidentReview: (String, String, String, String, String, String, String) -> Unit,
    onDeleteIncidentReview: (Int) -> Unit,
    teamLearnings: List<TeamLearning>,
    onSaveTeamLearning: (String, String, String) -> Unit,
    onDeleteTeamLearning: (Int) -> Unit,
    icdDiagnoses: List<com.example.data.IcdDiagnosis>
) {
    val context = LocalContext.current
    
    // States for various tools remembered saveably/locally inside page context
    // Ice/TIPP timer
    var tTimerActive by remember { mutableStateOf(false) }
    var tSecondsLeft by remember { mutableStateOf(30) }
    LaunchedEffect(tTimerActive) {
        if (tTimerActive) {
            tSecondsLeft = 30
            while (tSecondsLeft > 0) {
                delay(1000)
                tSecondsLeft--
            }
            tTimerActive = false
        }
    }
    
    // Sensory Reduction state
    var sensoryLightsDipped by remember { mutableStateOf(false) }
    var sensoryNoiseClosed by remember { mutableStateOf(false) }
    var sensoryAudienceRemoved by remember { mutableStateOf(false) }
    var sensoryDistanceMaintained by remember { mutableStateOf(false) }
    
    // Case Simulation State
    var currentSimIndex by remember { mutableStateOf(0) }
    var selectedSimAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var simIsSubmitted by remember { mutableStateOf(false) }
    
    // Quizzes State
    var quizScore by remember { mutableStateOf(0) }
    var currentQuizIndex by remember { mutableStateOf(0) }
    var selectedQuizAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var quizIsSubmitted by remember { mutableStateOf(false) }
    
    // Team Learning state
    var newLearningSit by remember { mutableStateOf("") }
    var newLearningWorked by remember { mutableStateOf("") }
    var newLearningRole by remember { mutableStateOf("Pflege") }
    
    // Micro Report State
    var ptInitials by remember { mutableStateOf("") }
    var selectedBehavior by remember { mutableStateOf("") }
    var selectedIntervention by remember { mutableStateOf("") }
    var selectedResult by remember { mutableStateOf("") }
    var generatedReportText by remember { mutableStateOf("") }
    
    // Alert State
    var assistantActive by remember { mutableStateOf(false) }
    var activeAlarmLevel by remember { mutableStateOf("GELB") }
    var alarmSecondsElapsed by remember { mutableStateOf(0) }
    var alarmRespondersCount by remember { mutableStateOf(0) }
    var collapseRequested by remember { mutableStateOf(false) }
    
    LaunchedEffect(assistantActive) {
        if (assistantActive) {
            alarmSecondsElapsed = 0
            alarmRespondersCount = 0
            while (true) {
                delay(1000)
                alarmSecondsElapsed++
                if (alarmSecondsElapsed == 4) alarmRespondersCount = 1
                if (alarmSecondsElapsed == 9) alarmRespondersCount = 2
                if (alarmSecondsElapsed == 15) alarmRespondersCount = 3
            }
        }
    }

    val phaseList = ScientificContent.phases
    val currentPhase = phaseList.first { it.id == selectedPhaseId }

    // Color definitions for clean Material 3 design and color-coded pages
    val phaseColor = when (selectedPhaseId) {
        "WEISS" -> Color(0xFFF8FAFC)
        "GRUEN" -> Color(0xFFF0FDF4)
        "GELB" -> Color(0xFFFFFBEB)
        "ROT" -> Color(0xFFFEF2F2)
        "BLAU" -> Color(0xFFEFF6FF)
        else -> Color(0xFFF8FAFC)
    }
    val textColor = when (selectedPhaseId) {
        "WEISS" -> Color(0xFF1E293B)
        "GRUEN" -> Color(0xFF14532D)
        "GELB" -> Color(0xFF78350F)
        "ROT" -> Color(0xFF7F1D1D)
        "BLAU" -> Color(0xFF1E3A8A)
        else -> Color(0xFF1E293B)
    }
    val accentColor = when (selectedPhaseId) {
        "WEISS" -> Color(0xFF475569)
        "GRUEN" -> Color(0xFF16A34A)
        "GELB" -> Color(0xFFD97706)
        "ROT" -> Color(0xFFDC2626)
        "BLAU" -> Color(0xFF2563EB)
        else -> Color(0xFF475569)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Die 5 Deeskalations-Phasen",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Navigieren Sie durch die Phasen, um das wissenschaftlich fundierte Curriculum zu ergründen. Jede Phase enthält die passenden interaktiven Praxis-Werkzeuge.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Horizontal Phase Buttons with color-coded badges
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                phaseList.forEach { phase ->
                    val isSelected = phase.id == selectedPhaseId
                    val bgCol = when (phase.id) {
                        "WEISS" -> if (isSelected) Color(0xFF475569) else Color(0xFFF1F5F9)
                        "GRUEN" -> if (isSelected) Color(0xFF16A34A) else Color(0xFFDCFCE7)
                        "GELB" -> if (isSelected) Color(0xFFD97706) else Color(0xFFFEF3C7)
                        "ROT" -> if (isSelected) Color(0xFFDC2626) else Color(0xFFFEE2E2)
                        "BLAU" -> if (isSelected) Color(0xFF2563EB) else Color(0xFFDBEAFE)
                        else -> Color(0xFFE2E8F0)
                    }
                    val textCol = if (isSelected) Color.White else when (phase.id) {
                        "WEISS" -> Color(0xFF334155)
                        "GRUEN" -> Color(0xFF15803D)
                        "GELB" -> Color(0xFF92400E)
                        "ROT" -> Color(0xFF991B1B)
                        "BLAU" -> Color(0xFF1E40AF)
                        else -> Color(0xFF475569)
                    }
                    val borderCol = when (phase.id) {
                        "WEISS" -> Color(0xFF94A3B8)
                        "GRUEN" -> Color(0xFF86EFAC)
                        "GELB" -> Color(0xFFFCD34D)
                        "ROT" -> Color(0xFFFCA5A5)
                        "BLAU" -> Color(0xFF93C5FD)
                        else -> Color(0xFFCBD5E1)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgCol)
                            .border(1.dp, borderCol, RoundedCornerShape(8.dp))
                            .clickable { onPhaseSelected(phase.id) }
                            .padding(vertical = 8.dp)
                            .testTag("phase_pill_${phase.id.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = phase.id,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = textCol
                        )
                    }
                }
            }
        }

        // Active Phase Summary and Title Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = phaseColor),
                border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AKTIVE DEESKALATIONS-PHASE: ${currentPhase.deName}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = accentColor,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (selectedPhaseId) {
                            "WEISS" -> "Basis & therapeutische Haltung: Beziehungsaufbau im stressfreien Milieu."
                            "GRUEN" -> "Alltagsstruktur & Vorhersehbarkeit: Haltbare Bindungsgrenzen auf Station."
                            "GELB" -> "Symptom-Warnung & Akute Co-Regulation: Den Amygdala Hijack de-eskalieren."
                            "ROT" -> "Biologischer Ernstfall & Schutz: Deeskalative Begleitung bei vollem Logikverlust."
                            "BLAU" -> "Nachbereitung & Reintegration: Schamsensible Aufarbeitung im Team."
                            else -> currentPhase.deName
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }

        // SCIENTIFIC TEXTBOOK SECTION (Fachlich korrektes Nachschlagewerk mit Quellen)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "WISSENSCHAFTLICHES KAPITEL",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    when (selectedPhaseId) {
                        "WEISS" -> {
                            Text(
                                text = "Ventraler Vagus & Physiologische Sicherheit (Porges, 2011):\n" +
                                        "Die absolute Grundlage jeder erfolgreichen Krisenprävention liegt in der Etablierung biologischer Sicherheit. Nach der Polyvagal-Theorie (S. Porges) signalisiert ein physiologisch ruhiger Zustand (ventraler Vagus) Kontaktbereitschaft, soziale Kooperation und angstfreie Reflexion. Ist das Milieu der psychiatrischen Station von berechenbarem Respekt geprägt, schüttet das Zentralnervensystem des Jugendlichen Oxytocin aus, welches die basale Erregung der Amygdala und somit die Stressachse profund dämpft.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Das Safewards-Modell (Bowers et al., 2014):\n" +
                                        "Als evidenzbasiertes Rahmenwerk hat Safewards nachgewiesen, dass durch unvoreingenommene Kommunikation und klares Erwartungsmanagement Zwangsausmaße auf Station drastisch sinken. In Phase WEISS arbeiten Pflegekräfte, Ärzte und Therapeuten präventiv an individuellen Krisenplänen (Crisis Plans) mit dem Jugendlichen zusammen. Dies geschieht in einer Atmosphäre der Deeskalation und unbedingten Wertschätzung (Carl Rogers).",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Supervision & Mentalisierung im Team (Fonagy et al., 2014):\n" +
                                        "KJP-Behandlungsteams laufen Gefahr, unter Stress reaktive Regelsysteme (Power-Struggles) aufzubauen. Regelmäßige multiprofessionelle Supervision schützt vor unbewussten Triangulationen, Splitting und transgenerationellen Retraumatisierungen durch rigide Stationsregeln. Sie erhält die genuine psychotherapeutische Haltung aufrecht.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        "GRUEN" -> {
                            Text(
                                text = "Die präventive Macht verlässlicher Alltagsstrukturen:\n" +
                                        "Verlässlichkeit und strukturierte, transparente Abläufe wirken direkt schützend. Trauma-sensibel erfahrene Jugendliche assoziieren unklare Regeländerungen oder personalabhängig schwankende Ausnahmen nicht mit 'Flexibilität', sondern mit existenzieller Bedrohung und Willkür (Porges, 2011). Dies triggert blitzschnell vegetative Stressreaktionen.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Beziehungstests & Grenzüberschreitung (Bowlby, 1988):\n" +
                                        "Aus Sicht der Bindungstheorie sind provokante Regelüberschreitungen oder Grenzaustestungen im Stationsalltag unbewusste Festigkeitstests. Der Jugendliche versucht reflexartig zu prüfen, ob die Bezugspersonen (das Stations-Team) emotional haltbar und berechenbar bleiben, oder ob sie mit verdeckter Ablehnung, Gegenangriff oder Distanzierung reagieren. Durch deeskalatives Halten des Rahmens ohne Aggression wird Bindungssicherheit erfahren.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        "GELB" -> {
                            Text(
                                text = "Erkennung sympathischer Erregung (Sympathikotonus):\n" +
                                        "Die Phase GELB repräsentiert den biologischen Weichensteller. Das autonome Nervensystem mobilisiert sich für Flucht oder Kampf (Sympathikus). Klinische Anzeichen umfassen gesteigerte motorische Unruhe, flache Atmung, verkleinertes Sehfeld, Versteifung des Muskeltonus und einsilbige verbale Reaktionsweisen. Der Jugendliche befindet sich im beginnenden Tunnelblick.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Der Scham-Wut-Zyklus und seine Vermeidung (Gottman, 1994):\n" +
                                        "Jugendliche mit schwerer emotionaler Instabilität (z.B. EIPS) empfinden bei Konflikten unerträglichen Schmerz – tiefste Scham. Da echte Scham neurophysiologisch unerträglich ist, weicht das Gehirn in Millisekunden aus auf schützende Wut. Behandlungsfehler wie laute Kritik, Zurechtweisung vor Dritten oder Bloßstellen eskalieren diesen Scham-Wut-Zyklus dramatisch. Verbale Validation ('Ich sehe deine Not') nimmt den Wind aus den Segeln.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Achtsames Innehalten - Der DBT STOP-Skill (Linehan, 2015):\n" +
                                        "Bevor eine Akutkraft de-eskalieren kann, muss sie ihr eigenes Nervensystem regulieren: S (Stop), T (Take a step back), O (Observe - eigene Trigger und Gefühle scannen), P (Proceed mindfully). Hierdurch wird verhindert, dass eigene Bedrohungsgesten oder rauer Stimmklang den Jugendlichen tiefer in den Amygdala Hijack stoßen.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        "ROT" -> {
                            Text(
                                text = "Der biologische Notstand (Amygdala-Hijack/Goleman, 1995):\n" +
                                        "In Phase ROT hat die Amygdala das Kommando über das Verhalten vollständig übernommen. Der präfrontale Kortex (Logik, Vernunft, Sprache) is funktionell deaktiviert. Jede verbale Belehrung, moralisierende Vorwürfe oder lange Erklärungen sind völlig nutzlos. Schlimmer noch: Die Amygdala dekodiert jedes zusätzliche laute Wort als unmittelbar physisches Bedrohungssignal und intensiviert die Gegenwehr.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Co-Regulation über Spiegelneurone:\n" +
                                        "Die Akutkraft wirkt primär als physischer Co-Regulator: Tiefe Atemfrequenz, extrem reduziertes Stimmtempo, tiefer Stimmklang und offene, seitliche Körperhaltung. Über vegetative Spiegelneurone des Jugendlichen signalisiert dies dem Gehirn Entspannung und holt ihn langsam aus dem Kampf-Flucht-Modus zurück.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Klinische Teamkoordination (Zwangsvermeidung nach Safewards):\n" +
                                        "Ein klar definiertes 3er-Rollenkonzept schützt Patienten und Team:\n" +
                                        "1. Der Sprecher: Ausschließlich EINE Person spricht mit dem Jugendlichen. Mehrstimmiges Hereinrufen irritiert das ohnehin reizüberflutete Gehirn und triggert Panik-Eskalationen.\n" +
                                        "2. Die Sicherung: Behält die räumliche Orientierung im Auge, hält sicheren Abstand.\n" +
                                        "3. Die Koordination: Sichert unauffällig Fluchtwege und fordert im Extremfall ärztliche Unterstützung an.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        "BLAU" -> {
                            Text(
                                text = "Die pharmakologische & biologische Kortisol-Latenz (Bowers, 2014):\n" +
                                        "Nach dem Abklingen des akuten Verhaltens der Phase ROT wirkt der Jugendliche äußerlich oft ruhig. Dies trügt! Die hormonelle Stressachse (Kortisol, Adrenalin) flutet extrem träge ab. Die biologische Abklingzeit beträgt nachweislich 20 bis 60 Minuten. In diesem Zeitraum reagiert das Nervensystem hochgradig reizempfindlich. Frühzeitige moralische Aufarbeitung oder disziplinarische Maßregelungen führen hier verlässlich zur sofortigen Re-Eskalation.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Schamsensibles Beziehungs-Debriefing & Wiederaufbau:\n" +
                                        "Nach einer Krise plagen den Jugendlichen Ängste vor Beziehungsabbruch, Isolation und Bestrafung (Bowlby, 1988). Das Beziehungsreintegrations-Gespräch auf Basis der Gewaltfreien Kommunikation (GFK) sichert den therapeutischen Rapport ab und stellt die Würde des Jugendlichen unter Wahrung der Grenzen wieder her.",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // CLINICAL GUIDELINES / NURSING INSTRUCTIONS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "AKUT-LEITLINIEN FÜR PFLEGE & ÄRZTE:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when (selectedPhaseId) {
                            "WEISS" -> "• Aktives Beziehungsangebot etablieren (Safewards: 1-to-1-Zeit).\n" +
                                       "• Krisenplan präventiv mit Patient formulieren und im Zimmer visualisieren.\n" +
                                       "• Regelmäßige Supervision im multidisziplinären Team wahrnehmen.\n" +
                                       "• ICD-Symptomregister präventiv für feinsinnige Diagnose-Lernwege nutzen."
                            "GRUEN" -> "• Strukturierte Stationszeiten, Kiosk- und Mahlzeiten strikt einhalten.\n" +
                                       "• Transparenz bei Grenzziehungen; keine personellen 'Geheimabsprachen'.\n" +
                                       "• Bindungstests freundlich-neutral aushalten, Grenzen klar spiegeln.\n" +
                                       "• SBAR-Schema für strukturierte Übergaben anwenden."
                            "GELB" -> "• Stimme um 1-2 Oktaven senken und bewusst langsamer sprechen.\n" +
                                      "• GFK Sprache nutzen: Beobachtung nennen, Scham validieren.\n" +
                                      "• Physische Reizreduktion einleiten: Reize dämpfen, Gang räumen.\n" +
                                      "• DBT Skills wie Sensory-Kit, Eispack oder Ball-Kompression bereithalten."
                            "ROT" -> "• Ausschließlich EINE Bezugsperson heranziehen (Sprecher-Rolle).\n" +
                                     "• Mindestens 1,5 Meter Abstand (Bedrohungsreflexe im Gesichtsfeld dämpfen).\n" +
                                     "• Körperhaltung seitlich geöffnet einnehmen. Niemals über den Patienten beugen.\n" +
                                     "• Bei körperlicher Fremdgefährdung: Ärztlichen Notfallkontakt (Zentr. 3012) unverzüglich alarmieren."
                            "BLAU" -> "• Mindestens 45 Minuten 'Kortisol-Latenz' absolut reizarm abwarten.\n" +
                                      "• Beziehungs-Reparaturgespräch schamsensibel führen.\n" +
                                      "• Safewards Post-Incident-Review im Dienstzimmer digital loggen.\n" +
                                      "• Kollegiale Fallbesprechung mit Kollegen posten für gemeinsame Lerneffekte."
                            else -> "Keine spezifischen Handlungsleitlinien ausgewiesen."
                        },
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // CRITICAL DON'TS (ESKALATIONSTREIBER)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFF991B1B), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "KRITISCHES DON'T (ESKALATIONSCHRAUBE):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B),
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (selectedPhaseId) {
                            "WEISS" -> "Keine Krisenpläne pflegen! Den Jugendlichen unvorbereitet in die Krise laufen lassen. Mangelnde Team-Supervision tolerieren, was unbewusstes Splitting-Verhalten auf Station Vorschub leistet."
                            "GRUEN" -> "Rigide oder willkürliche Regeländerungen je nach Sympathie der Schichtleitung einführen. Grenzüberschreitungen des Jugendlichen persönlich nehmen und emotional beleidigt reagieren."
                            "GELB" -> "Die Erregung vor der gesamten Patientengruppe bagatellisieren, laut auf dem Gang kritisieren oder den Jugendlichen bloßstellen. Das erzeugt extreme Abwehr-Schamreaktionen und sofortige Gewalt."
                            "ROT" -> "Mit mehreren Personen gleichzeitig lautstark auf den Jugendlichen einreden oder drohend auf ihn zugehen. Ihn in ein Eck drängen. Das Gehirn schaltet im Hijack-Status dann unweigerlich auf Kampf."
                            "BLAU" -> "Sofort nach der Krise auf unruhige Klärungen drängen oder Strafen verkünden. Das strapazierte Nervensystem befindet sich noch mitten in der biologischen Kortisol-Latenz und explodiert erneut."
                            else -> "Keine Eskalationsschrauben ausgewiesen."
                        },
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFF7F1D1D)
                    )
                }
            }
        }

        // INTEGRATION OF STRATEGIC CLINICAL SCREEN PATH FOR GRUEN
        if (selectedPhaseId == "GRUEN") {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "KLINISCHE DIAGNOSERICHTLINIEN (INTEGRIERT)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Lernen Sie, wie sich die deeskalierenden Strategien zielgerichtet auf spezifische Krankheitsbilder anwenden lassen. Für ausführliche Pflegeprozedere öffnen Sie Kapitel 02.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(icdDiagnoses) { d ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = d.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(4.dp)) {
                                Text(text = d.codeOrId, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = d.dynamik, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)
                    }
                }
            }
        }

        // SPLIT LINE / HEADER FOR THE PRACTICAL INTERACTIVE WORKSHEET TOOL
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.2.dp)
            Spacer(modifier = Modifier.height(16.dp))
            
            val (toolIcon, toolTitle, toolSubtitle) = when (selectedPhaseId) {
                "WEISS" -> Triple(Icons.Default.Add, "Interaktive Behandlungs-Krisenpläne (Room SQL)", "Erfassen Sie schützende Trigger und Beruhigungsfaktoren bei Jugendlichen präventiv in der Datenbank.")
                "GRUEN" -> Triple(Icons.Default.Menu, "Klinische Fallsimulation & Wissens-Quizzes", "Trainieren Sie Ihre deeskalativen Entscheidungen für den Ernstfall bei traumatisierten Patienten.")
                "GELB" -> Triple(Icons.Default.Build, "2-Tap Mikro-Bericht & Sensor-Checklist", "Generieren Sie DSGVO-konforme Stations-Verlaufsberichte live durch Antippen.")
                "ROT" -> Triple(Icons.Default.PlayArrow, "Atem-Taktgeber & Team-Intervent.", "Physiologische Atemanleitung zur Co-Regulation und Notruf-Simulation auf Station.")
                "BLAU" -> Triple(Icons.Default.List, "Sicherheits-Vorfalls-Analysen & Peer-Supervision", "Archivieren Sie systematische Safewards Review-Protokolle unaufgeregt nach Krisen.")
                else -> Triple(Icons.Default.Star, "Praxis-Werkzeug", "Werkzeug zur Ausführung deeskalativer Hilfen.")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = toolIcon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "INTERAKTIVES PRAXIS-WERKZEUG",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = accentColor,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = toolTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = toolSubtitle,
                fontSize = 11.2.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // TOOL SECTIONS CORRESPONDING TO ACTIVE PHASES
        when (selectedPhaseId) {
            "WEISS" -> {
                // WEISS TOOL: CRISIS PLANS WORKSPACE (ROOM SQL DB Integration)
                item {
                    CrisisPlanWorkspaceSection(
                        crisisPlans = crisisPlans,
                        onSaveCrisisPlan = onSaveCrisisPlan,
                        onDeleteCrisisPlan = onDeleteCrisisPlan,
                        icdDiagnoses = icdDiagnoses
                    )
                }
            }
            "GRUEN" -> {
                // GRUEN TOOL: DECISION SIMULATION
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("scenario_training_card"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Szenario-Entscheidungs-Training", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lösen Sie reale Akutfälle. Ihre Entscheidungspfade deeskalieren oder verschlimmern die Erregung aus neurobiologischer Sicht.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Szenario ${currentSimIndex + 1} von 2", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                TextButton(onClick = {
                                    currentSimIndex = (currentSimIndex + 1) % 2
                                    selectedSimAnswerIndex = null
                                    simIsSubmitted = false
                                }) {
                                    Text("Nächstes Szenario →", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            val currentScenario = if (currentSimIndex == 0) {
                                Triple(
                                    "Frühwarnung bei Autismus (ASS) u18",
                                    "Ein 15-jähriger Junge mit ASS steht vor dem verschlossenen Stationskiosk, schreit hysterisch und tritt aggressiv gegen das Gitter, weil er seine vertraute Limonadenmarke heute nicht erhält:",
                                    listOf(
                                        "A) Ihm ruhig und bestimmt erklären, dass die Kioskzeit abgelaufen ist und die Hausordnung für alle gleich gilt.",
                                        "B) Die Limonade sofort herausgeben, um die Aggression abzufangen, selbst wenn es regelfreie Ausnahmen erzeugt.",
                                        "C) Seine sensorische Überreizung validieren, den Fluchtweg unversperrt seitlich absichern, Reizquellen dämpfen und ihm eine visuell strukturierte Alternative anbieten."
                                    )
                                )
                            } else {
                                Triple(
                                    "EIPS Krisenregulation am Gang",
                                    "Eine 16-jährige Patientin mit EIPS (GELB) läuft weinend über den Stationsgang und schlägt sich leicht den Kopf an die Wand. Ein Kollege herrscht sie im Gang lautstark an, sofort in ihr Zimmer zu verschwinden:",
                                    listOf(
                                        "A) Sich lautstark einmischen und den Kollegen vor der Gruppe kritisieren, um die Patientin zu schützen.",
                                        "B) Die Patientin ruhig ansprechen, sie diskret in ein ruhiges Therapiezimmer begleiten, Gefühle validieren und ihr ein DBT TIPP Eispack-Kohlereiz anbieten.",
                                        "C) Den Kollegen gewähren lassen, da beziehungsorientierte Zuwendung in diesem Moment das selbstverletzende Verhalten ungewollt verstärken würde."
                                    )
                                )
                            }

                            Text(text = currentScenario.first, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = currentScenario.second, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)

                            Spacer(modifier = Modifier.height(8.dp))

                            currentScenario.third.forEachIndexed { idx, ans ->
                                val isSelected = selectedSimAnswerIndex == idx
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            if (!simIsSubmitted) {
                                                selectedSimAnswerIndex = idx
                                            }
                                        },
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Text(text = ans, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                                }
                            }

                            if (selectedSimAnswerIndex != null && !simIsSubmitted) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { simIsSubmitted = true },
                                    modifier = Modifier.fillMaxWidth().testTag("solve_sim_button")
                                ) {
                                    Text("Entscheidung auswerten")
                                }
                            }

                            if (simIsSubmitted) {
                                Spacer(modifier = Modifier.height(10.dp))
                                val explanation = if (currentSimIndex == 0) {
                                    when (selectedSimAnswerIndex) {
                                        0 -> "❌ Nicht optimal! Bei ASS blockiert extremer Stress die rationale Informationsverarbeitung. Das Beharren auf Paragraphen (Hausordnung) wirkt wie Verachtung und triggert massiven Amygdala Hijack."
                                        1 -> "⚠️ Nur bedingter Teilerfolg! Das sofortige Nachgeben verhindert zwar die Eskalation, trainiert jedoch unbewusst instrumentelle Gewalt und begünstigt Splitting-Dynamiken auf Station."
                                        else -> "✓ Ausgezeichnete Deeskalation! Gefühlsvalidation nimmt die Not an. Durch die sensorische Abkühlung (Reizreduktion) und das visuelle Angebot wird der ventrale Vagus reaktiviert."
                                    }
                                } else {
                                    when (selectedSimAnswerIndex) {
                                        0 -> "❌ Fatal! Das Kritisieren des Kollegen spaltet das Therapeutenteam (Splitting-Falle). Dies verstärkt bei EIPS das Gefühl unzuverlässiger Bindungsgrenzen."
                                        1 -> "✓ Großartig reguliert! Die Verlegung in ein ruhiges Zimmer verhindert die schambeladene Flureskalation vor Dritten. Das Eispack-Kälte-TIPP-Angebot dämpft physiologische Übererregung augenblicklich."
                                        else -> "❌ Vorsicht! Die Annahme, dass emotionale Notfälle bei EIPS reine Manipulation zur Belohnung sind, ist neurobiologisch überholt. Unsichere Bindungsmuster brauchen feinfühlige Haltepunkte."
                                    }
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("FEEDBACK & ERKLÄRUNG:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = explanation, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer, lineHeight = 15.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // GRUEN TOOL: DEESKALATION WISSENSTEST (QUIZ)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("quiz_card"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Deeskalations-Wissens-Check", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Prüfen Sie Ihr theoretisches Fachwissen über Safewards-Konzepte, Polyvagal-Theorie, GFK und DBT.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Frage ${currentQuizIndex + 1} von 3", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("Punkte: $quizScore", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            val quizQuestions = listOf(
                                Triple(
                                    "Wie lange dauert die biologische 'Kortisol-Latenz' nach heftigen emotionalen Krisen (KJP)?",
                                    listOf(
                                        "A) Ca. 2-5 Minuten, danach ist das System sofort wieder neutral",
                                        "B) Mindestens 20-60 Minuten, in denen Verhandlungen wegen Re-Eskalationsgefahr warten müssen",
                                        "C) Exakt 24 Stunden, in denen der Patient das Zimmer nicht verlassen sollte"
                                    ),
                                    1
                                ),
                                Triple(
                                    "Welchen physiologischen Effekt bewirkt das DBT TIPP Temperatur-Element (Eispack im Gesicht)?",
                                    listOf(
                                        "A) Triggert den Tauchreflex, der den Puls augenblicklich drosselt",
                                        "B) Es erzeugt langanhaltende Schmerzen zur Reizüberlagerung",
                                        "C) Es blockiert die visuelle Perceptual Narrowing Seh-Einschränkung"
                                    ),
                                    0
                                ),
                                Triple(
                                    "Wie sollte Kritik an Jugendlichen geäußert werden, um extreme Scham-Wut-Spiralen abzufangen?",
                                    listOf(
                                        "A) Unmittelbar auf dem Gang, um ein klares Statement für alle Akteure zu setzen",
                                        "B) Unter vier Augen in einem separaten Rückzugsraum",
                                        "C) Durch konsequentes Ignorieren über den restlichen Tag"
                                    ),
                                    1
                                )
                            )

                            val qObj = quizQuestions[currentQuizIndex]
                            Text(text = qObj.first, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(6.dp))

                            qObj.second.forEachIndexed { index, ansOption ->
                                val isSelected = selectedQuizAnswerIndex == index
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            if (!quizIsSubmitted) {
                                                selectedQuizAnswerIndex = index
                                            }
                                        }
                                        .testTag("quiz_ans_$index"),
                                    color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Text(text = ansOption, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                                }
                            }

                            if (selectedQuizAnswerIndex != null && !quizIsSubmitted) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        quizIsSubmitted = true
                                        if (selectedQuizAnswerIndex == qObj.third) {
                                            quizScore += 10
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("submit_quiz_answer")
                                ) {
                                    Text("Antwort überprüfen")
                                }
                            }

                            if (quizIsSubmitted) {
                                Spacer(modifier = Modifier.height(8.dp))
                                val isCorrect = selectedQuizAnswerIndex == qObj.third
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCorrect) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = if (isCorrect) "✓ Richtig gelöst!" else "✗ Leider falsch!",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isCorrect) Color(0xFF166534) else Color(0xFF991B1B)
                                        )
                                        Text(
                                            text = "Wissenschaftlicher Hintergrund: " + if (currentQuizIndex == 0) {
                                                "Kortisol flutet im Blut extrem träge ab. Wer zu früh klärt, re-eskaliert unfreiwillig, da die Amygdala hochsensibel geschaltet bleibt."
                                            } else if (currentQuizIndex == 1) {
                                                "Der parasympathische Vagus-Ast wird durch Kälte-Thermorezeptoren getriggert – senkt die vegetative Grundspannung."
                                            } else {
                                                "Scham ist die schmerzhafteste Emotion. Kritik vor Dritten vernichtet das Selbstwertgefühl und mündet fast immer in Gegenangriffe."
                                            },
                                            fontSize = 10.sp,
                                            color = if (isCorrect) Color(0xFF14532D) else Color(0xFF7F1D1D)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        if (currentQuizIndex < 2) {
                                            currentQuizIndex++
                                        } else {
                                            currentQuizIndex = 0
                                            quizScore = 0
                                        }
                                        selectedQuizAnswerIndex = null
                                        quizIsSubmitted = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(if (currentQuizIndex < 2) "Nächste Frage" else "Quiz neustarten")
                                }
                            }
                        }
                    }
                }
            }
            "GELB" -> {
                // GELB TOOL: 2-TAP MIKRO-REPORT-GENERATOR (Verlaufsbericht für Doku)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "2-Tap Mikro-Bericht-Dokumentation", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tippen Sie das Verhalten des Jugendlichen und Ihre Deeskalations-Intervention an. Das Tool generiert einen professionellen, DSGVO-konformen Verlaufsbericht für die Pflegedokumentation.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = ptInitials,
                                onValueChange = { ptInitials = it },
                                label = { Text("Kürzel Jugendliche(r) bzw. Patient (Optional, z.B. J.K.)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("report_initials_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("1. VERHALTEN / GERÄUSCHPEGEL (1-Tap):", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(
                                    "Starke innere Anspannung & unruhige Gereiztheit (GELB)",
                                    "Verbale Drohung und Grenzüberschreitung am Gang",
                                    "Dissoziative Erstarrung & lautloser Rückzug (ROT)",
                                    "Körperliche Aggression gegen Inventar / Türen schlagen",
                                    "Scham-Abwehr & laute verbale Provokation"
                                ).forEach { behavior ->
                                    val isSel = selectedBehavior == behavior
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedBehavior = behavior }
                                            .testTag("report_behavior_${behavior.take(4).lowercase()}"),
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    ) {
                                        Text(text = behavior, fontSize = 11.sp, modifier = Modifier.padding(8.dp), fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("2. DEESKALATIONS-INTERVENTION (2-Tap):", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(
                                    "Validation und schamsensibles GFK-Einfühlungs-Gespräch",
                                    "Co-regulatives Atmen & Stimmabsenkung um eine Oktave",
                                    "Reizreduktion und physische Distanzgewährung durchgeführt",
                                    "Anleitung DBT STOP & TIPP Kältereiz (Eispack im Nacken)",
                                    "Regulationsangebot über Sensory-Kit (Gewichtsdecke & Kopfhörer)"
                                ).forEach { inter ->
                                    val isSel = selectedIntervention == inter
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedIntervention = inter }
                                            .testTag("report_inter_${inter.take(4).lowercase()}"),
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    ) {
                                        Text(text = inter, fontSize = 11.sp, modifier = Modifier.padding(8.dp), fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("3. ERGEBNIS / VERLAUF:", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(
                                    "Erfolgreich co-reguliert, Jugendliche(r) entspannt in Phase GRÜN",
                                    "Situation beruhigt, engmaschige Begleitung auf Station fortgesetzt",
                                    "Arzt zur diagnostischen Klärung und Absicherung hinzugezogen"
                                ).forEach { res ->
                                    val isSel = selectedResult == res
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedResult = res }
                                            .testTag("report_res_${res.take(4).lowercase()}"),
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    ) {
                                        Text(text = res, fontSize = 11.sp, modifier = Modifier.padding(8.dp), fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                                    val init = if (ptInitials.isNotBlank()) "bei Patient ($ptInitials)" else "beim Jugendlichen"
                                    generatedReportText = "Doku $timeStr Uhr: Akute Erregung $init.\nVerhaltensaspekt: $selectedBehavior.\nKlinische Deeskalation: $selectedIntervention.\nVerlauf: $selectedResult. Keine freiheitsentziehenden Maßnahmen nötig."
                                },
                                enabled = selectedBehavior.isNotEmpty() && selectedIntervention.isNotEmpty() && selectedResult.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth().testTag("compile_microreport")
                            ) {
                                Text("Berichtsbaustein generieren")
                            }

                            if (generatedReportText.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("GENERIERTER VERLAUFSBERICHT:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = generatedReportText,
                                        fontSize = 11.sp,
                                        fontStyle = FontStyle.Italic,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Patientenbericht", generatedReportText)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Doku-Text in Zwischenablage kopiert!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Eintrag kopieren")
                                }
                            }
                        }
                    }
                }

                // GELB TOOL: SENSORY REACTION CHECKLIST & VERBAL DEESKALATIONS-TEMPLATES
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Reizreduktions-Prozedere & Sensory-Kit", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Haken Sie reizminimierende Schritte nacheinander ab, um die sympathikotone Erregung an Reizquellen dämpfen.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = sensoryLightsDipped, onCheckedChange = { sensoryLightsDipped = it }, modifier = Modifier.testTag("chk_lights"))
                                    Text("Beleuchtung dämpfen (Deckenlicht aus, Akutstation dimmen)", fontSize = 11.5.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = sensoryNoiseClosed, onCheckedChange = { sensoryNoiseClosed = it })
                                    Text("Lärmquellen senken (Dienstzimmertür zu, Kopfhörer anbieten)", fontSize = 11.5.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = sensoryAudienceRemoved, onCheckedChange = { sensoryAudienceRemoved = it })
                                    Text("Zuschauer entfernen (Mitpatienten diskret in Zimmer schicken)", fontSize = 11.5.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = sensoryDistanceMaintained, onCheckedChange = { sensoryDistanceMaintained = it })
                                    Text("Abstand halten (Eigenschutz; mindestens 1.5 Meter Luftraum)", fontSize = 11.5.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Klinische Deeskalations-Gesprächsskripte", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Validation: „Ich höre dich und sehe, wie extrem wütend du bist. Ich bin hier, um dich absolut abzusichern, nicht um dich zu bestrafen.“\n" +
                                        "• Grenzen spiegeln: „Ich möchte dein Anliegen verstehen. Bitte senke deine Stimme, damit wir gemeinsam nach einer ehrlichen Lösung suchen können.“\n" +
                                        "• Ausstieg anbieten: „Du darfst jederzeit in dein Zimmer oder in den Ruheraum gehen, wenn es dir am Flur gerade zu viele Reize sind.“",
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            "ROT" -> {
                // ROT TOOL: SYSTEMIC PACED BREATHING (utilizing Paced Breathing component)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Co-Regulativer Atem-Taktgeber", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Atmen Sie gemeinsam mit dem Jugendlichen in diesem Takt. Längere Ausatmung aktiviert augenblicklich das parasympathische Vagus-Cardio-System.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            BreathingGuideComponent(
                                breathingPhase = breathingPhase,
                                breathingSeconds = breathingSeconds,
                                breathingCycles = breathingCycles,
                                onStartBreathing = onStartBreathing,
                                onStopBreathing = onStopBreathing
                            )
                        }
                    }
                }

                // ROT TOOL: DBT COOL DOWN TIMEOUT (ICE PACK COUNTDOWN)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "DBT TIPP Kältereiz-Timer (Eispack)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Legen Sie dem Jugendlichen ein eiskaltes Gelfalt-Eispack auf Gesicht oder Nacken für 30 Sekunden auf. Der ausgelöste Tauchreflex senkt Herzschlag und Pulsfrequenz vegetativ ab.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    color = if (tTimerActive) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape,
                                    border = BorderStroke(2.dp, if (tTimerActive) Color(0xFF2563EB) else Color.Transparent),
                                    modifier = Modifier.size(80.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (tSecondsLeft > 0) "$tSecondsLeft s" else "STOPP",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 20.sp,
                                            color = if (tTimerActive) Color(0xFF2563EB) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { tTimerActive = true },
                                        enabled = !tTimerActive,
                                        modifier = Modifier.weight(1f).testTag("start_tipp_timer_btn")
                                    ) {
                                        Text("Reiz starten (30s)", fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { tTimerActive = false; tSecondsLeft = 30 },
                                        enabled = tTimerActive,
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Abbrechen", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // ROT TOOL: BACKUP ALERT NOTFALL-SIMULATION ON STATION
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Rückhalt anforderndes Team-Assistenz-System (Simulation)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sollte eine akute Gefährdung bestehen, fordern Sie Unterstützung an. Die Simulation dispatcht sofort Behandler-Verstärkung.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (!assistantActive) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Wählen Sie die therapeutische Notrufstufe:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf("GELB" to Color(0xFFD97706), "ORANGE" to Color(0xFFEA580C), "NOTFALL" to Color(0xFFDC2626)).forEach { (lvl, col) ->
                                            Button(
                                                onClick = {
                                                    activeAlarmLevel = lvl
                                                    assistantActive = true
                                                    collapseRequested = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = col),
                                                modifier = Modifier.weight(1f).testTag("trigger_alert_$lvl")
                                            ) {
                                                Text(lvl, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            } else {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color(0xFFDC2626))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "ASSISTENZAKTIVIERUNG LAUFT! (Stufe: $activeAlarmLevel)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFDC2626))
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("• Vergangene Sekunden: $alarmSecondsElapsed", fontSize = 11.sp)
                                        Text("• Kollegiale Unterstützung vor Ort: $alarmRespondersCount Behandler eingetroffen", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        
                                        if (alarmSecondsElapsed >= 15) {
                                            Text("• Dr. Becker (Oberarzt-Dienst) wurde per Krisentelefon hinzugerufen.", fontSize = 11.sp, fontStyle = FontStyle.Italic)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { collapseRequested = !collapseRequested },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (collapseRequested) Color(0xFF1D4ED8) else Color(0xFF6B7280)
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (collapseRequested) "Ablösung gerufen!" else "Sprecher ablösen", fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = {
                                            assistantActive = false
                                            collapseRequested = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF166534)),
                                        modifier = Modifier.weight(1f).testTag("dismiss_alarm")
                                    ) {
                                        Text("Entwarnung / Reset", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // ROT TOOL: CLINICAL EMERGENCY INFO DIRECTORY & CONTACTS & SBAR
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Zwangsvermeidungs- & Schutz-Prozedere", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. Letzte Ratio: Freiheitsentziehende Maßnahmen (Fixierung, Isolierung) sind die absolute Notausnahme bei unmittelbarer Selbst-/Fremdgefährdung.\n" +
                                        "2. Einholen: Arztliche Anordnung unverzüglich einholen.\n" +
                                        "3. Dauer-Sitzwache: 1-to-1 Begleitung durch Pflegekraft unterbrechungsfrei herstellen.\n" +
                                        "4. Vitalwerte: Alle 15 Minuten Puls, Atmung, Bewusstseinslage messen und dokumentieren.\n" +
                                        "5. Debriefing: Nach jeder Maßnahme strukturierte Nachbesprechung (PIR) mit Arzt, Patient und Team binnen 24h.",
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Klinische Telefonnummern im Krisenfall", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• KJP Arzt (Allgemeine Zentrale): Tel: 3012\n" +
                                    "• Oberarzt-Diensttelefon (Harburg): Tel: 3044\n" +
                                    "• Kriseninterventionsdienst (Mobil): Tel: 9110\n" +
                                    "• Pflegedienstleitung Akutstation: Tel: 4402\n" +
                                    "• Kälte-Eispacks für DBT-TIPP: Dienstzimmer-Kühlschrank 1. Stock / Schrank 2A",
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            "BLAU" -> {
                // BLAU TOOL: POST-INCIDENT REVIEWS ARCHIV (Room DB integration)
                item {
                    IncidentReviewWorkspaceSection(
                        reviews = incidentReviews,
                        onSaveIncidentReview = onSaveIncidentReview,
                        onDeleteIncidentReview = onDeleteIncidentReview
                    )
                }

                // BLAU TOOL: CO-LEARNING BOARD FOR TEAM BEST-PRACTICES (Room DB integration)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Kollegiales Best-Practice Board", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Was hat in einer Krise funktioniert? Teilen Sie Ihre Erfahrungen unaufgeregt und offline-verfügbar für das gesamte Team.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = newLearningSit,
                                onValueChange = { newLearningSit = it },
                                label = { Text("Situation / Auslöser (z.B. Visite ADHS)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("learning_input_situation")
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = newLearningWorked,
                                onValueChange = { newLearningWorked = it },
                                label = { Text("Was hat geholfen? (z.B. Timebox visuell gestellt)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("learning_input_worked")
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Rolle:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                listOf("Pflege", "Arzt", "Therapeut").forEach { role ->
                                    val isSel = newLearningRole == role
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { newLearningRole = role },
                                        label = { Text(role, fontSize = 10.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onSaveTeamLearning(newLearningSit, newLearningWorked, newLearningRole)
                                    newLearningSit = ""
                                    newLearningWorked = ""
                                    Toast.makeText(context, "Erfahrung kollegial geteilt!", Toast.LENGTH_SHORT).show()
                                },
                                enabled = newLearningSit.isNotBlank() && newLearningWorked.isNotBlank(),
                                modifier = Modifier.fillMaxWidth().testTag("save_team_learning_button")
                            ) {
                                Text("Auf Board posten")
                            }
                        }
                    }
                }

                if (teamLearnings.isEmpty()) {
                    item {
                        Text("Etablierte Best-Practices der Akutstation:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                    val defaultPractices = listOf(
                        Triple("ASS Reizüberflutung", "Sinnvoll: Gewichtsdecke und Kopfhörer im Entspannungsraum unaufgefordert anbieten.", "Pflege"),
                        Triple("ADHS Entladung", "Sinnvoll: Klare, visuelle Sanduhr einsetzen und Bewegungsalternativen im KJP-Schulhof erlauben.", "Therapeut"),
                        Triple("EIPS Dissoziation", "Sinnvoll: Kältesensoren aktivieren (Eispack im Nacken) statt kognitiven Dialogen.", "Arzt")
                    )
                    items(defaultPractices) { (sit, helped, role) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Situation: $sit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(4.dp)) {
                                        Text(text = role, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Hilfreich: $helped", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    item {
                        Text("Kollegiale Beiträge (${teamLearnings.size}):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    items(teamLearnings) { learning ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Klient/Sit: ${learning.situation}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp)) {
                                            Text(text = learning.submittedByRole, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { onDeleteTeamLearning(learning.id) },
                                            modifier = Modifier.size(24.dp).testTag("delete_learning_${learning.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Löschen", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Hilfreich: ${learning.whatWorked}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Real-time CMS Dynamic Custom Sections for this active phase
        val phaseCmsItems = cmsSections.filter { it.phaseId == selectedPhaseId }
        if (phaseCmsItems.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Zusätzliche Stations-Leitlinien (CMS)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(phaseCmsItems) { section ->
                CmsSectionCard(section = section, isAdminView = false)
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// 2. DIAGNOSEN SCREEN
// ══════════════════════════════════════════════════════
@Composable
fun DiagnosenScreen(
    selectedDiagnosisId: String,
    onDiagnosisSelected: (String) -> Unit,
    icdDiagnoses: List<com.example.data.IcdDiagnosis>
) {
    val currentDiagnosis = icdDiagnoses.firstOrNull { it.codeOrId == selectedDiagnosisId } ?: icdDiagnoses.firstOrNull()

    if (currentDiagnosis == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(36.dp))
                    Text(
                        text = "Keine Behandlungsleitlinien gefunden",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Bitte navigieren Sie zum 'ICD-Symptome' Tab, um Diagnosen aus dem WHO ICD-11 Register live zu suchen, anzupassen oder neu zu erfassen.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Störungsspezifische Strategien",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Häufig besitzen Jugendliche mehrere Diagnosen. Adressieren Sie primär die klinisch am stärksten dysregulierte Symptomatik und passen Sie Stimme und Distanz an.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Diagnosis Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                icdDiagnoses.chunked(3).forEach { row ->
                    Column(modifier = Modifier.weight(1f)) {
                        row.forEach { d ->
                            val isSelected = d.codeOrId == currentDiagnosis.codeOrId
                            TextButton(
                                onClick = { onDiagnosisSelected(d.codeOrId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .height(38.dp)
                                    .testTag("diag_tab_${d.codeOrId.lowercase()}"),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = d.codeOrId,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Diagnosis Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentDiagnosis.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ESKALATIONS-DYNAMIK & DIAGNOSTISCHER KERN:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentDiagnosis.dynamik,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // The 3 Pillars of Action
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Pile 1: Absicherung (Yellow background)
                ActionPillarCard(
                    title = "1. ABSICHERUNG (BEI ERREGUNG)",
                    content = currentDiagnosis.absicherung,
                    bg = Color(0xFFFFFBEB),
                    tc = Color(0xFFB45309),
                    border = Color(0xFFFCD34D),
                    icon = Icons.Default.Warning
                )

                // Pile 2: Klärung (Blue background)
                ActionPillarCard(
                    title = "2. KLÄRUNG (GELB / INTERAKTION)",
                    content = currentDiagnosis.klaerung,
                    bg = Color(0xFFEFF6FF),
                    tc = Color(0xFF1E40AF),
                    border = Color(0xFF93C5FD),
                    icon = Icons.Default.Info
                )

                // Pile 3: Auflösung (Green background)
                ActionPillarCard(
                    title = "3. AUFLÖSUNG (BLAU / WIEDERAUFBAU)",
                    content = currentDiagnosis.aufloesung,
                    bg = Color(0xFFF0FDF4),
                    tc = Color(0xFF15803D),
                    border = Color(0xFF86EFAC),
                    icon = Icons.Default.Check
                )
            }
        }
    }
}

@Composable
fun ActionPillarCard(
    title: String,
    content: String,
    bg: Color,
    tc: Color,
    border: Color,
    imageVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = BorderStroke(1.dp, border),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tc,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = tc,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Color(0xFF1E293B)
            )
        }
    }
}

// ══════════════════════════════════════════════════════
// 3. WISSEN / REFERENCE SCREEN
// ══════════════════════════════════════════════════════
// 3. HANDBUCH / COHESIVE 7-CHAPTER REFERENCE TEXTBOOK SCREEN
// ══════════════════════════════════════════════════════
@Composable
fun HandbuchScreen(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedPhaseId: String,
    onPhaseSelected: (String) -> Unit,
    selectedDiagnosisId: String,
    onDiagnosisSelected: (String) -> Unit,
    cmsSections: List<CmsSection>,
    icdDiagnoses: List<com.example.data.IcdDiagnosis>,
    breathingPhase: BreathingPhase,
    breathingSeconds: Int,
    breathingCycles: Int,
    onStartBreathing: () -> Unit,
    onStopBreathing: () -> Unit,
    crisisPlans: List<CrisisPlan>,
    onSaveCrisisPlan: (String, String, String, String, String, String) -> Unit,
    onDeleteCrisisPlan: (Int) -> Unit,
    incidentReviews: List<IncidentReview>,
    onSaveIncidentReview: (String, String, String, String, String, String, String) -> Unit,
    onDeleteIncidentReview: (Int) -> Unit,
    teamLearnings: List<TeamLearning>,
    onSaveTeamLearning: (String, String, String) -> Unit,
    onDeleteTeamLearning: (Int) -> Unit
) {
    var selectedChapterId by remember { mutableStateOf<Int?>(null) }

    if (searchQuery.isNotEmpty()) {
        HandbuchSearchResultsView(
            searchQuery = searchQuery,
            onSearchChange = onSearchChange,
            cmsSections = cmsSections,
            onNavigateToChapter = { chapterId ->
                selectedChapterId = if (chapterId in 1..3) chapterId else 1
                onSearchChange("")
            },
            onNavigateToPhase = { phaseId ->
                selectedChapterId = 1
                onPhaseSelected(phaseId)
                onSearchChange("")
            },
            onNavigateToDiagnosis = { diagId ->
                selectedChapterId = 2
                onDiagnosisSelected(diagId)
                onSearchChange("")
            }
        )
    } else if (selectedChapterId == null) {
        TableOfContentsView(
            searchQuery = searchQuery,
            onSearchChange = onSearchChange,
            onSelectChapter = { selectedChapterId = it }
        )
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { selectedChapterId = null },
                    modifier = Modifier.testTag("back_to_toc_button_chapter")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Zurück zum Inhaltsverzeichnis"
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "KAPITEL 0$selectedChapterId",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = { selectedChapterId = null }
                ) {
                    Text("Inhaltsverzeichnis", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            Box(modifier = Modifier.weight(1f)) {
                when (selectedChapterId) {
                    1 -> PhasenScreen(
                        selectedPhaseId = selectedPhaseId,
                        onPhaseSelected = onPhaseSelected,
                        cmsSections = cmsSections,
                        breathingPhase = breathingPhase,
                        breathingSeconds = breathingSeconds,
                        breathingCycles = breathingCycles,
                        onStartBreathing = onStartBreathing,
                        onStopBreathing = onStopBreathing,
                        crisisPlans = crisisPlans,
                        onSaveCrisisPlan = onSaveCrisisPlan,
                        onDeleteCrisisPlan = onDeleteCrisisPlan,
                        incidentReviews = incidentReviews,
                        onSaveIncidentReview = onSaveIncidentReview,
                        onDeleteIncidentReview = onDeleteIncidentReview,
                        teamLearnings = teamLearnings,
                        onSaveTeamLearning = onSaveTeamLearning,
                        onDeleteTeamLearning = onDeleteTeamLearning,
                        icdDiagnoses = icdDiagnoses
                    )
                    2 -> DiagnosenScreen(
                        selectedDiagnosisId = selectedDiagnosisId,
                        onDiagnosisSelected = onDiagnosisSelected,
                        icdDiagnoses = icdDiagnoses
                    )
                    3 -> Chapter7View(onNavigateToTools = { _, _ -> })
                }
            }
        }
    }
}

@Composable
fun BorderedBox(
    title: String,
    borderColor: Color,
    contentColor: Color = borderColor,
    backgroundColor: Color = borderColor.copy(alpha = 0.05f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.2.dp, borderColor.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = contentColor,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            content()
        }
    }
}

@Composable
fun SideBySideCards(
    title1: String,
    border1: Color,
    items1: List<String>,
    title2: String,
    border2: Color,
    items2: List<String>
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BorderedBox(title = title1, borderColor = border1) {
            items1.forEach { item ->
                Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                    Text("• ", fontWeight = FontWeight.Bold, color = border1)
                    Text(text = item, fontSize = 12.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        BorderedBox(title = title2, borderColor = border2) {
            items2.forEach { item ->
                Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                    Text("• ", fontWeight = FontWeight.Bold, color = border2)
                    Text(text = item, fontSize = 12.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun CompactTableCard(
    badgeText: String,
    badgeBg: Color,
    badgeTextCol: Color,
    title: String,
    fields: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeBg,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeTextCol,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(text = title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(8.dp))
            fields.forEach { (label, value) ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(text = label, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                    Text(text = value, fontSize = 12.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun TableOfContentsView(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSelectChapter: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ASKLEPIOS KLINIKUM HAMBURG HARBURG · KINDER- UND JUGENDPSYCHIATRIE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 0.5.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "KLINISCHES REFERENZWERK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Deeskalierende Gesprächsführung",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Multiprofessionelle Kompetenz für die psychiatrische Akutstation • 14 bis 17 Jahre",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val circles = listOf(
                            Triple("W", "WEISS", Color(0xFFF1F5F9) to Color(0xFF334155)),
                            Triple("G", "GRÜN", Color(0xFFDCFCE7) to Color(0xFF166534)),
                            Triple("G", "GELB", Color(0xFFFEF3C7) to Color(0xFF92400E)),
                            Triple("R", "ROT", Color(0xFFFEE2E2) to Color(0xFF991B1B)),
                            Triple("B", "BLAU", Color(0xFFDBEAFE) to Color(0xFF1E40AF))
                        )
                        circles.forEach { (let, full, colors) ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(colors.first, CircleShape)
                                    .border(1.dp, colors.second.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = let, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.second)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val tags = listOf("Safewards", "Polyvagal-Theorie", "DBT Skills", "GFK", "Mentalisierung")
                        tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.08f),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Pflege · Medizin · Psychologie · Sozialarbeit · Ergotherapie · Schule",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Version 1.0 · 2025 · Nur für internen Dienstgebrauch",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("handbuch_search_bar"),
                placeholder = { Text("Suchbegriff oder Kapitel suchen...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "INHALTSVERZEICHNIS",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        val chapters = listOf(
            Triple(1, "Das 5-Phasen-Deeskalationsmodell", "Phasenübersicht · Integrierte Deeskalations-Interaktionen · Theorie & Praxis-Werkzeuge"),
            Triple(2, "Klinische Diagnoserichtlinien", "Krankheitsbilder in der KJP · Spezifische Handlungsleitlinien · ICD-11 Integration"),
            Triple(3, "Quellen- und Literaturverzeichnis", "Klinische Standardwerke · Evidenzbasierte Studien · Wissenschaftlicher Apparat")
        )

        items(chapters) { (id, title, contents) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectChapter(id) }
                    .testTag("toc_chapter_$id"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "0$id",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = contents,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Öffnen",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "WARUM DIESES DOKUMENT?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Deeskalation ist keine individuelle Technik – sie ist eine multiprofessionelle Teamkompetenz. Dieses Referenzwerk fasst fünf wissenschaftlich evaluierte Ansätze zu einem konsistenten Gesamtbild zusammen, das für alle Berufsgruppen auf der Station gleichzeitig gilt.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            BorderedBox(title = "DAS KERNPRINZIP", borderColor = Color(0xFF1E40AF)) {
                Text(
                    text = "Deeskalation beginnt im eigenen Nervensystem – nicht beim Patienten. Co-Regulation ist neurobiologisch messbar: Das Nervensystem des Teams reguliert das der gesamten Station.",
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF1E3A8A)
                )
            }

            BorderedBox(title = "WISSENSCHAFTLICHE BASIS", borderColor = Color(0xFF15803D)) {
                val bases = listOf(
                    "Safewards" to "bis zu 20% Reduktion von Zwangsmaßnahmen (Bowers et al., 2014)",
                    "Polyvagal-Theorie" to "Neurobiologie von Sicherheit und Stress (Porges, 2011)",
                    "DBT-Skills" to "Interaktive Selbstregulation des Fachpersonals (Linehan, 2015)",
                    "GFK" to "Strukturierte, bedürfnisbezogene Gesprächsführung (Rosenberg, 2016)",
                    "Mentalisierung" to "Verstehen innerer Zustände und Absichten (Fonagy, 2004)"
                )
                bases.forEach { (title, desc) ->
                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                        Text("• ", fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        Text(
                            text = "$title: $desc",
                            fontSize = 11.sp,
                            color = Color(0xFF14532D),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            BorderedBox(title = "WIE DIESES DOKUMENT ZU LESEN IST", borderColor = Color(0xFF475569)) {
                Text(
                    text = "Die Kapitel bauen aufeinander auf – das Neurobiologie-Kapitel erklärt, warum die Interventionen in den Phasen so gestaltet sind, wie sie sind. Das Diagnose-Kapitel kombiniert alle vorherigen Konzepte für spezifische klinische Konstellationen. Kapitel 04 (Die 5 Phasen im Detail) is die primäre praktische Referenz für den Stationsalltag.",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}

@Composable
fun Chapter1View(
    onSelectPhase: (String) -> Unit,
    onNavigateToTools: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "KAPITEL 01 · SCHNELLREFERENZ",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Das 5-Farben-Modell",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gemeinsame Sprache des Teams · Grundlage für schnelle, abgestimmte Krisenreaktion. Jede Phase signalisiert dem gesamten Team sofort die anwendbaren neurobiologischen Methoden.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            
            // Selector Row of Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "WEISS" to Color(0xFFF1F5F9),
                    "GRUEN" to Color(0xFFDCFCE7),
                    "GELB" to Color(0xFFFEF3C7),
                    "ROT" to Color(0xFFFEE2E2),
                    "BLAU" to Color(0xFFDBEAFE)
                ).forEach { (ph, bg) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(bg, RoundedCornerShape(4.dp))
                            .clickable { onSelectPhase(ph) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(ph, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "EVIDENZBASIERTE SCHNELLREFERENZ",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 0.5.sp
            )
        }

        val rows = listOf(
            CompactTableCardData(
                badgeText = "W", badgeBg = Color(0xFFF1F5F9), badgeTextCol = Color(0xFF334155), title = "WEISS (Grundlage)",
                fields = listOf(
                    "NERVENSYSTEM" to "Ventraler Vagus · Soziale Verbundenheit",
                    "ERKENNUNGSZEICHEN" to "Stabile Grundstimmung, Team-Kooperation, keine Frühwarnzeichen",
                    "KERNINTERVENTIONEN" to "Ausbildung · Krisenplanung · Selbstfürsorge · DBT PLEASE · Supervision",
                    "ABSOLUT VERMEIDEN" to "Krisenplanung aufschieben · Supervision als optional behandeln"
                )
            ),
            CompactTableCardData(
                badgeText = "G", badgeBg = Color(0xFFDCFCE7), badgeTextCol = Color(0xFF166534), title = "GRÜN (Prävention)",
                fields = listOf(
                    "NERVENSYSTEM" to "Ventraler Vagus · aktiv & zugänglich",
                    "ERKENNUNGSZEICHEN" to "Patient offen, kommunizierend, ruhige Mimik, Blickkontakt",
                    "KERNINTERVENTIONEN" to "Beziehungsaufbau · Krisenplan herstellen/aktualisieren · klare Strukturen",
                    "ABSOLUT VERMEIDEN" to "Unangekündigte Regeländerungen · personalabhängige Ausnahmen"
                )
            ),
            CompactTableCardData(
                badgeText = "G", badgeBg = Color(0xFFFEF3C7), badgeTextCol = Color(0xFF92400E), title = "GELB (Frühwarnung)",
                fields = listOf(
                    "NERVENSYSTEM" to "Sympathikus beginnt zu aktivieren · Kortex noch erreichbar",
                    "ERKENNUNGSZEICHEN" to "Rückzug · motorische Unruhe · angespannter Körper · einsilbig",
                    "KERNINTERVENTIONEN" to "DBT STOP · Absicherung · Grounding · GFK Schritte 1-3 · Reizreduktion",
                    "ABSOLUT VERMEIDEN" to "Klärungsgespräch beginnen · Erklärungen geben · zwei Personen sprechen zugleich"
                )
            ),
            CompactTableCardData(
                badgeText = "R", badgeBg = Color(0xFFFEE2E2), badgeTextCol = Color(0xFF991B1B), title = "ROT (Akutkrise)",
                fields = listOf(
                    "NERVENSYSTEM" to "Amygdala übernimmt; Kortex offline ODER dorsaler Vagus; Erstarrung",
                    "ERKENNUNGSZEICHEN" to "Aggression · Schreien · Grenzüberschreitung ODER Starre · leerer Blick",
                    "KERNINTERVENTIONEN" to "Sicherheit · Reizreduktion · verlängerte Ausatmung · Grounding (Erstarrung)",
                    "ABSOLUT VERMEIDEN" to "Klärung versuchen · Argumente · Berühren ohne Erlaubnis"
                )
            ),
            CompactTableCardData(
                badgeText = "B", badgeBg = Color(0xFFDBEAFE), badgeTextCol = Color(0xFF1E40AF), title = "BLAU (Nachbereitung)",
                fields = listOf(
                    "NERVENSYSTEM" to "Rückkehr zu ventral · aber Kortisol noch 20-60 Min. erhöht",
                    "ERKENNUNGSZEICHEN" to "Äußerlich ruhig – aber erst nach Kortisol-Latenz wirklich zugänglich",
                    "KERNINTERVENTIONEN" to "Warten (min. 20 Min.) · Klärung · GFK Schritt 4 · Würde herstellen · PIR",
                    "ABSOLUT VERMEIDEN" to "Sofortiges Klärungsgespräch · Vorwürfe · Entschuldigung erzwingen"
                )
            )
        )

        items(rows) { data ->
            CompactTableCard(badgeText = data.badgeText, badgeBg = data.badgeBg, badgeTextCol = data.badgeTextCol, title = data.title, fields = data.fields)
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            BorderedBox(title = "NEUROBIOLOGISCHE GRUNDREGEL", borderColor = Color(0xFF1E40AF)) {
                Text(
                    text = "Was in einer phase sinnvoll ist, ist in einer anderen neurobiologisch nicht erreichbar. Ein Klärungsgespräch in Phase ROT scheitert nicht an mangelndem Geschick – es scheitert an der Physiologie des Gehirns. Die Phase bestimmt, was möglich ist.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF1E3A8A)
                )
            }
            BorderedBox(title = "WICHTIGSTE TEAMREGEL", borderColor = Color(0xFF15803D)) {
                Text(
                    text = "Phasenwechsel laut ansagen: „Ich sehe GELB“ ist keine Diagnose – es ist ein Koordinationssignal. Wer zuerst erkennt, gibt die Information weiter. Das Team reagiert auf den Zustand des Patienten, nicht auf die eigene Deutung des Verhaltens.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF14532D)
                )
            }
            BorderedBox(title = "KORTISOL-LATENZ - DIE UNSICHTBAE GEFAHR", borderColor = Color(0xFFD97706)) {
                Text(
                    text = "Nach dem sichtbaren Abklingen einer ROT-Krise bleibt der Kortisol-Spiegel noch 20 bis 60 Minuten erhöht. Der Patient wirkt ruhig – das Nervensystem bleibt in Alarmbereitschaft. Ein Klärungsgespräch in dieser Zeit kann einen zweiten Eskalationszyklus auslösen. Warten ist keine Gleichgültigkeit, sondern klinisch notwendige Rücksicht auf physiologische Realität.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF78350F)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DIREKTE VERLINKUNG ZU PRAXIS-TOOLS",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Wenden Sie die gelernten Prinzipien sofort mit unseren interaktiven Stations-Hilfsmitteln an:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToTools("COREG_SKILLS", "BREATHING") },
                            modifier = Modifier.testTag("ch1_link_breathing"),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Atemcoaching", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { onNavigateToTools("COREG_SKILLS", "DBT_SENSORY") },
                            modifier = Modifier.testTag("ch1_link_dbt"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("DBT & Sensorik (Eispack)", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

data class CompactTableCardData(
    val badgeText: String,
    val badgeBg: Color,
    val badgeTextCol: Color,
    val title: String,
    val fields: List<Pair<String, String>>
)

@Composable
fun Chapter2View(onNavigateToTools: (String, String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "KAPITEL 02 · NEUROBIOLOGISCHE GRUNDLAGEN",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Gehirn & Nervensystem im Stress",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Warum verhalten sich Jugendliche in Krisen so wie sie es tun? Die moderne Gehirnforschung erklärt die Notwendigkeit von Co-Regulation und deeskalierendem Verhalten.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "POLYVAGAL-THEORIE (PORGES, 2011)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        val pvData = listOf(
            CompactTableCardData(
                "W/G", Color(0xFFDCFCE7), Color(0xFF166534), "Ventraler Vagus (Soziale Verbundenheit)",
                fields = listOf(
                    "ERKENNBAR AN" to "Gute Mimik, Blickkontakt, freie Sprache, Kooperation",
                    "WAS PHYSIOLOGISCH MÖGLICH IST" to "Klärung · Therapie · Krisenplan · Lernen · Nachgespräch (einziges therapeutisches Fenster)"
                )
            ),
            CompactTableCardData(
                "Y", Color(0xFFFEF3C7), Color(0xFF92400E), "Sympathikus (Kampf-Flucht / GELB)",
                fields = listOf(
                    "ERKENNBAR AN" to "Rückzug · Unruhe · angespannter Körper · einsilbig · Provokation",
                    "WAS PHYSIOLOGISCH MÖGLICH IST" to "Nur Validation und Absicherung – kein rationales Klärungsgespräch"
                )
            ),
            CompactTableCardData(
                "R", Color(0xFFFEE2E2), Color(0xFF991B1B), "Dorsaler Vagus (Erstarrung-Collapse / ROT)",
                fields = listOf(
                    "ERKENNBAR AN" to "Dissoziation, vollständige Starre, starrer Blick, Sprache blockiert",
                    "WAS PHYSIOLOGISCH MÖGLICH IST" to "Ausschließlich Grounding (Erdung) und Reizarmut, keine Berührung"
                )
            )
        )

        items(pvData) { data ->
            CompactTableCard(badgeText = data.badgeText, badgeBg = data.badgeBg, badgeTextCol = data.badgeTextCol, title = data.title, fields = data.fields)
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "AMYGDALA HIJACK & KORTISOL-LATENZ",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "In 17 bis 100 Millisekunden kann die Amygdala bei stressreiz den Kortex offline schalten – stumm schalten. Das betrifft Patient und Fachperson gleichermaßen.",
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        val ahData = listOf(
            CompactTableCardData(
                "GELB", Color(0xFFFEF3C7), Color(0xFF92400E), "Vor dem Hijack",
                fields = listOf(
                    "NEUROBIOLOGIE" to "Amygdala zunehmend aktiv · Kortex noch erreichbar",
                    "KONSEQUENZ FÜR DAS TEAM" to "Letztes Zeitfenster für Kommunikation · DBT STOP jetzt · keine Konfrontation"
                )
            ),
            CompactTableCardData(
                "ROT", Color(0xFFFEE2E2), Color(0xFF991B1B), "Hijack active",
                fields = listOf(
                    "NEUROBIOLOGIE" to "Amygdala übernimmt vollständig · Kortex offline",
                    "KONSEQUENZ FÜR DAS TEAM" to "Nur Sicherheit herstellen · keine Klärung · keine Argumente"
                )
            ),
            CompactTableCardData(
                "POST", Color(0xFFECEFEE), Color(0xFF475569), "Post-Hijack (sichtbar ruhig)",
                fields = listOf(
                    "NEUROBIOLOGIE" to "Amygdala beruhigt sich · Kortex beginnt Rückkehr",
                    "KONSEQUENZ FÜR DAS TEAM" to "Kortisol noch erhöht · mind. 20 Minuten warten – noch KEINE Klärung"
                )
            ),
            CompactTableCardData(
                "BLAU", Color(0xFFDBEAFE), Color(0xFF1E40AF), "Latenz abgeklungen",
                fields = listOf(
                    "NEUROBIOLOGIE" to "Volle Rückkehr zum ventralen Vagus",
                    "KONSEQUENZ FÜR DAS TEAM" to "Jetzt ist deeskalierendes Gespräch physiologisch möglich und sinnvoll"
                )
            )
        )

        items(ahData) { data ->
            CompactTableCard(badgeText = data.badgeText, badgeBg = data.badgeBg, badgeTextCol = data.badgeTextCol, title = data.title, fields = data.fields)
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "CO-REGULATION & SPIEGELNEURONEN",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Nervensysteme beeinflussen sich gegenseitig direkt, automatisch und unwillkürlich schneller als Sprache. Spiegelneuronen übertragen Emotionen.",
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        val cgData = listOf(
            CompactTableCardData(
                "1", Color(0xFFECEFEE), Color(0xFF475569), "Atemrhythmus",
                fields = listOf(
                    "WIRKUNG" to "Verlangsamtes Atem signalisiert dem Gegenüber Sicherheit (Parasympathikus)",
                    "ANWENDUNG" to "Sichtbar 4 Sek. einatmen, 8 Sek. ausatmen für deeskalierendes Modell"
                )
            ),
            CompactTableCardData(
                "2", Color(0xFFECEFEE), Color(0xFF475569), "Bewegungstempo",
                fields = listOf(
                    "WIRKUNG" to "Motorische Entschleunigung bremst Amygdala-Spiegelung",
                    "ANWENDUNG" to "Sich langsamer bewegen als die eigene Aufregung es fordert"
                )
            ),
            CompactTableCardData(
                "3", Color(0xFFECEFEE), Color(0xFF475569), "Stimmfrequenz",
                fields = listOf(
                    "WIRKUNG" to "Eine tiefe Stimme triggert Vagus-Bremse",
                    "ANWENDUNG" to "Stimme in den Brustraum senken, gleichmäßig fließen lassen"
                )
            ),
            CompactTableCardData(
                "4", Color(0xFFECEFEE), Color(0xFF475569), "Muskeltonus",
                fields = listOf(
                    "WIRKUNG" to "Gespannte Hände signalisieren Kampf im Gegenüber",
                    "ANWENDUNG" to "Schultern senken, offene Hände, entspanntes Gesicht einnehmen"
                )
            )
        )

        items(cgData) { data ->
            CompactTableCard(badgeText = data.badgeText, badgeBg = data.badgeBg, badgeTextCol = data.badgeTextCol, title = data.title, fields = data.fields)
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            BorderedBox(title = "KERNPRINZIP CO-REGULATION", borderColor = Color(0xFFDC2626)) {
                Text(
                    text = "Dysreguliertes Personal kann kein Kind ko-regulieren. Ablösung in der Krise ist keine Niederlage, sondern professionelle Intervention. Wenn ein Teammitglied anhaltend dysreguliert ist, erhöht das die Grundspannung der gesamten Station – für alle Patienten gleichzeitig.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF991B1B)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "MENTALISIERUNG & STRESSVERZERRUNG",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            
            SideBySideCards(
                title1 = "ZUSAMMENBRUCH DER MENTALISIERUNG – ZEICHEN",
                border1 = Color(0xFFEA580C),
                items1 = listOf(
                    "Extremes Schwarz-Weiß-Denken (Alles/Nichts)",
                    "Globale Abwertungen: „Du hast mich noch nie gemocht“",
                    "Unfähigkeit, Vergangenheit von Gegenwart teils zu trennen",
                    "Verhalten des Gegenübers wird sofort als persönlicher Angriff interpretiert"
                ),
                title2 = "MENTALISIERUNG REAKTIVIEREN",
                border2 = Color(0xFF16A34A),
                items2 = listOf(
                    "Neugier zeigen statt zu korrigieren oder zu belehren",
                    "Traumapädagogische Leitfrage stellen: „Was ist passiert?\" statt „Warum tut er das?\"",
                    "Im Team-Review: „Was könnte im Jugendlichen vorgegangen sein, als es begann?\""
                )
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "WAHRNEHMUNGSFEHLER DES TEAMS UNTER STRESS",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        val wfData = listOf(
            CompactTableCardData(
                "ERR1", Color(0xFFECEFEE), Color(0xFF475569), "Fundamental Attribution Error",
                fields = listOf(
                    "BESCHREIBUNG" to "Das eskalative Verhalten wird fälschlich der stabilen Persönlichkeit zugeschrieben.",
                    "KLINISCHE KONSEQUENZ" to "„Er ist aggressiv“ statt „Er befindet sich im Amygdala Hijack und kann physiologisch gerade nicht anders“"
                )
            ),
            CompactTableCardData(
                "ERR2", Color(0xFFECEFEE), Color(0xFF475569), "Confirmation Bias (Bestätigungsfehler)",
                fields = listOf(
                    "BESCHREIBUNG" to "Stressverengung lässt uns Signale übersehen, die unsere Meinung widerlegen.",
                    "KLINISCHE KONSEQUENZ" to "Frühwarnsignale bei vertrauten oder „schwierigen“ Patienten werden übersehen oder abgetan."
                )
            ),
            CompactTableCardData(
                "ERR3", Color(0xFFECEFEE), Color(0xFF475569), "Perceptual Narrowing (Tunnelblick)",
                fields = listOf(
                    "BESCHREIBUNG" to "Fokus engt sich extrem auf die stärkste Bedrohungs- oder Reizquelle ein.",
                    "KLINISCHE KONSEQUENZ" to "Andere Patienten im Raum, sekundäre Gefahren und Fluchtwege werden visuell ausgeblendet."
                )
            )
        )

        items(wfData) { data ->
            CompactTableCard(badgeText = data.badgeText, badgeBg = data.badgeBg, badgeTextCol = data.badgeTextCol, title = data.title, fields = data.fields)
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "DBT STOP-SKILL FÜR DAS TEAM",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    Triple("S", "STOP", "Innehalten, einfrieren"),
                    Triple("T", "TAKE", "Schritt zurück"),
                    Triple("O", "OBSERVE", "Situation beobachten"),
                    Triple("P", "PROCEED", "Wise Mind nutzen")
                ).forEach { (l, sh, de) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(l, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            Text(sh, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = MaterialTheme.colorScheme.primary)
                            Text(de, fontSize = 8.sp, lineHeight = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            BorderedBox(title = "INNERER CHECK – 10 SEKUNDEN VOR INTERVENTION", borderColor = Color(0xFF9333EA)) {
                listOf(
                    "Wie ist mein Atemrhythmus? bin ich verlangsamt?",
                    "Sind meine Schultern angespannt?",
                    "Arbeite ich gerade im unkontrollierten Emotion Mind oder im kühlen Protokoll (Reasonable Mind)?",
                    "• Fallweise Gegenregulation: Wenn zwei von drei Fragen mit NEIN beantwortet werden: erst drei tiefe Atemzüge (Opposite Action), dann eintreten."
                ).forEach { q ->
                    Text("• $q", fontSize = 11.sp, lineHeight = 15.sp, color = Color(0xFF581C87))
                }
            }
            Text(
                text = "„Wise Mind ist wie der Boden am Grund eines Sees – an der Oberfläche stürmische Wellen, unten immer ruhig.“ – Linehan (2015)",
                fontStyle = FontStyle.Italic,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PRAKTISCHE ANWENDUNG DER NEUROBIOLOGIE",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Nutzen Sie co-regulatorische Atemsynchronisation oder Team-Atemübungen direkt auf Station:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToTools("COREG_SKILLS", "BREATHING") },
                            modifier = Modifier.testTag("ch2_link_breathing"),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Atemcoaching starten", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCard(article: NeuroArticle) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (expanded) article.content else article.content.take(130) + "...",
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (expanded && article.bulletPoints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                article.bulletPoints.forEach { (boldText, plainText) ->
                    Column(modifier = Modifier.padding(vertical = 3.dp)) {
                        Text(
                            text = "◆ $boldText",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = plainText,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Chapter3View(onNavigateToTools: (String, String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "KAPITEL 03 · PROFESSIONELLE HALTUNG & KOMMUNIKATION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Haltung & deeskalierende Gesprächsführung",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kommunikation beginnt in der inneren Haltung des Klinikers. Deeskalationstechniken deeskalieren nachhaltig, wenn sie auf radikaler Akzeptanz und tieber Schamsensibilität beruhen.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "KOMMUNIKATIVE GRUNDREGELN IN DER PRAXIS",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        val commData = listOf(
            CompactTableCardData(
                "ALT1", Color(0xFFDCFCE7), Color(0xFF166534), "Körperhaltung",
                fields = listOf(
                    "DEESKALIEREND ✓" to "Seitlich versetzt · entspannt · offene Hände sichtbar · Fluchtweg des Jugendlichen unversperrt belassen",
                    "ESKALIEREND ✗ (VERMEIDEN)" to "Frontal aufrecht · Arme verschränkt · Weg versperren · Raum dominieren"
                )
            ),
            CompactTableCardData(
                "ALT2", Color(0xFFDCFCE7), Color(0xFF166534), "Blickkontakt",
                fields = listOf(
                    "DEESKALIEREND ✓" to "Freundlich · weich · gelegentlich wegschauen bei hoher Erregung des Gegenübers",
                    "ESKALIEREND ✗ (VERMEIDEN)" to "Starren · fixierender / prüfender Blick · Blickkontakt erzwingen wollen"
                )
            ),
            CompactTableCardData(
                "ALT3", Color(0xFFDCFCE7), Color(0xFF166534), "Stimme",
                fields = listOf(
                    "DEESKALIEREND ✓" to "Tief · verlangsamt · ruhig · gleichmäßig fließend",
                    "ESKALIEREND ✗ (VERMEIDEN)" to "Laut · hoch · gepresst · scharf · sarkastisch · ironisch"
                )
            ),
            CompactTableCardData(
                "ALT4", Color(0xFFDCFCE7), Color(0xFF166534), "Körperliche Distanz",
                fields = listOf(
                    "DEESKALIEREND ✓" to "1,5 bis 2,0 Meter Abstand halten · Fluchtwege frei halten · kein Festhalten",
                    "ESKALIEREND ✗ (VERMEIDEN)" to "Nähe erzwingen · Fluchtwege blockieren · unaufgeforderte Berührungen"
                )
            )
        )

        items(commData) { data ->
            CompactTableCard(badgeText = data.badgeText, badgeBg = data.badgeBg, badgeTextCol = data.badgeTextCol, title = data.title, fields = data.fields)
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "SCHAM ALS PRIMÄRER ESKALATIONSTREIBER",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Scham ist die schmerzhafteste Emotion bei Jugendlichen. Scham-Wut-Spirale: Demütigung -> Scham unerträglich -> Abwehr durch explosive Wut. Der Trigger liegt oft 10-30 Min. zurück.",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            SideBySideCards(
                title1 = "TYPISCHE SCHAM-AUSLÖSER ON STATION",
                border1 = Color(0xFFDC2626),
                items1 = listOf(
                    "Kritik, Zurechtweisung oder Grenzziehung im Beisein Dritter",
                    "Globale Anklagen like „Du hast mal wieder...“",
                    "Abgenötigte, erzwungene öffentliche Entschuldigungen",
                    "Negative Konsequenzen lautstark im Gruppenraum verkünden",
                    "Nichtbeachtung (Blickkontakt-Vermeidung, Gruß verweigern)"
                ),
                title2 = "SCHAM-SENSIBLE ALTERNATIVEN",
                border2 = Color(0xFF16A34A),
                items2 = listOf(
                    "Kritik und korrigierende Konsequenzen AUSSCHLIESSLICH im privaten Raum unter vier Augen",
                    "Faktisches Verhalten beschreiben statt die Persönlichkeit zu bewerten",
                    "Entschuldigungen freiwillig ermöglichen und reifen lassen, niemals erzwingen",
                    "Lob oder positives Feedback gern öffentlich spiegeln, Kritisches immer diskret"
                )
            )

            Spacer(modifier = Modifier.height(10.dp))
            BorderedBox(title = "VERACHTUNG ALS UNBEWUSSTER ESKALATIONSVERSTÄRKER (GOTTMAN, 1994)", borderColor = Color(0xFFF59E0B)) {
                Text(
                    text = "Verachtungssignale (Augenrollen, leises Seufzen beim Betreten des Zimmers, herablassender Tonfall) werden von Jugendlichen extrem sensibel registriert. Verachtung aktiviert dieselben Hirnareale wie körperlicher Schmerz und treibt Eskalation massiv an. Wenn im Team-Review Verachtung auffällt: Das ist ein Signal für tiefe Erschöpfung und Unterstützungsmangel, kein moralisches Urteil.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF78350F)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "GEWALTFREIE KOMMUNIKATION IN DER KRISE",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        val gfkData = listOf(
            CompactTableCardData(
                "GFK1", Color(0xFFECEFEE), Color(0xFF475569), "1 · Beobachtung (Phase GELB)",
                fields = listOf(
                    "FORMULIERUNG" to "„Ich sehe, dass du seit einer halben Stunde am Fenster stehst und die Fäuste ballst.“",
                    "NICHT SO (VERMEIDEN)" to "„Du verhältst dich schon wieder aggressiv.“ (Globale Bewertung schürt Gegenwehr)"
                )
            ),
            CompactTableCardData(
                "GFK2", Color(0xFFECEFEE), Color(0xFF475569), "2 · Gefühl (Phase GELB)",
                fields = listOf(
                    "FORMULIERUNG" to "„Ich glaube, du bist gerade extrem aufgebracht und verzweifelt. Kann das sein?“",
                    "NICHT SO (VERMEIDEN)" to "„Du bist einfach wütend.“ (Nimmt Fremdbestimmung vor)"
                )
            ),
            CompactTableCardData(
                "GFK3", Color(0xFFECEFEE), Color(0xFF475569), "3 · Bedürfnis (Phase GELB)",
                fields = listOf(
                    "FORMULIERUNG" to "„Brauchst du im Moment einfach etwas Abstand und deine Ruhe?“",
                    "NICHT SO (VERMEIDEN)" to "Sofortige Lösungen oder Regeln aufzwingen, ohne den Kern zu kennen."
                )
            ),
            CompactTableCardData(
                "GFK4", Color(0xFFDBEAFE), Color(0xFF1E40AF), "4 · Bitte (Phase BLAU)",
                fields = listOf(
                    "FORMULIERUNG" to "„Möchtest du jetzt pfünf Minuten mit mir in den Hof gehen?“",
                    "NICHT SO (VERMEIDEN)" to "„Du musst dich jetzt beruhigen.“ ODER Scheinalternativen ohne echte Wahl."
                )
            )
        )

        items(gfkData) { data ->
            CompactTableCard(badgeText = data.badgeText, badgeBg = data.badgeBg, badgeTextCol = data.badgeTextCol, title = data.title, fields = data.fields)
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "VALIDATION – DIE WIRKSAMSTE KURZINTERVENTION",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            SideBySideCards(
                title1 = "VALIDATION – SO KLINGT ES ✓",
                border1 = Color(0xFF16A34A),
                items1 = listOf(
                    "„Ich verstehe, dass du gerade extrem wütend bist.“",
                    "„Das macht total Sinn, dass dich das so aufwühlt.“",
                    "„Das klingt wirklich unerträglich belastend für dich.“",
                    "„Ich höre, wie viel dir das gerade wird.“",
                    "„Du musst das nicht alleine durchstehen.“"
                ),
                title2 = "NICHT-VALIDATION (AMYGDALA-AKTIVATOR) ✗",
                border2 = Color(0xFFDC2626),
                items2 = listOf(
                    "„Das ist doch alles nicht so schlimm.“",
                    "„Du überreagierst völlig.“",
                    "„Andere Jugendliche haben es hier auch schwer.“",
                    "„Jetzt reiß dich einfach mal zusammen.“",
                    "„Ich weiß genau, wie du dich fühlst.“"
                )
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "DBT Wise Mind (Weiser Verstand)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    Triple("Emotion Mind", Color(0xFFFEF3C7), "Vollständig überflutet von Gefühlen; Gespräch verzerrt"),
                    Triple("Wise Mind ✓", Color(0xFFDCFCE7), "Integration von Gefühl & Logik; deeskalierende Präsenz"),
                    Triple("Reasonable Mind", Color(0xFFDBEAFE), "Rein rational, wirksamkeitsarm, klingt distanziert wie Protokoll")
                ).forEach { (t, c, d) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = c),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(t, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(d, fontSize = 9.sp, lineHeight = 12.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            BorderedBox(title = "DBT OPPOSITE ACTION IN DER KRISE", borderColor = Color(0xFF7C3AED)) {
                Text(
                    text = "Der natürliche Handlungsimpuls bei aggressivem Erregungsaufbau ist: lauter werden, herantreten, frontal konfrontieren. Die DBT Gegenregulation (Opposite Action) erfordert: Stimme senken (tiefe Tonlage), Abstand vergrößern (einen Schritt zurücktreten), Stille und Schweigen aushalten. Diese Aktion muss vollständig ausgeführt werden, um wirksam zu sein.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFF5B21B6)
                )
            }

            Text(
                text = "„Aggression ist oft ein Hilferuf in erlerter Sprache. Das Team, das das Verhalten vom wahren Kern trennt, deeskaliert nachhaltig.“",
                fontStyle = FontStyle.Italic,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "READY-TO-USE VERBALE SCRIPTS & GFK",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Greifen Sie direkt auf anwendbare Deeskalations-Formulierungen und Gesprächs-Leitfäden für die Praxis zu:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToTools("COREG_SKILLS", "VERBAL_SCRIPTS") },
                            modifier = Modifier.testTag("ch3_link_scripts"),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gesprächsscripts", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Chapter6View(onNavigateToTools: (String, String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Teamkoordination & Nachbereitung",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Krisenintervention ist eine koordinierte Mannschaftsdisziplin. Verlässliche Nachbereitung im System schützt Fachkräfte vor Sekundärtraumatisierung.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(ScientificContent.teamArticles) { article ->
            ArticleCard(article = article)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PRAKTISCHE TEAM-WORKSPACES",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Nutzen Sie koordinierte Alarmierungsmodi oder dokumentieren Sie ein schwieriges Vorkommnis im Team-Review:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToTools("TEAM_STATION", "ASSIST_ALERT") },
                            modifier = Modifier.testTag("ch6_link_alert"),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Team-Assist Alert", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { onNavigateToTools("LEARN_REVIEWS", "INCIDENT_REVIEWS") },
                            modifier = Modifier.testTag("ch6_link_review"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Vorkommnis dokumentieren", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Chapter7View(onNavigateToTools: (String, String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Wissenschaftliche Grundlagen",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Die hier abgebildeten Deeskalationsleitlinien beruhen auf nachfolgenden Standardwerken, klinisch geprüften Studien und Therapiemanualen:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(ScientificContent.references) { ref ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Standardwerk",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = ref,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "WISSENSTEST & SIMULATIONEN",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Überprüfen Sie Ihr klinisches Wissen oder üben Sie interaktive Fallbeispiele:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToTools("LEARN_REVIEWS", "QUIZZES") },
                            modifier = Modifier.testTag("ch7_link_quiz"),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Wissensquiz starten", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { onNavigateToTools("LEARN_REVIEWS", "CASE_SIMS") },
                            modifier = Modifier.testTag("ch7_link_sim"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fall-Simulationen", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HandbuchSearchResultsView(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    cmsSections: List<CmsSection>,
    onNavigateToChapter: (Int) -> Unit,
    onNavigateToPhase: (String) -> Unit,
    onNavigateToDiagnosis: (String) -> Unit
) {
    val q = searchQuery.trim()
    
    val matchedPhases = ScientificContent.phases.filter {
        it.id.contains(q, ignoreCase = true) || 
        it.subtitle.contains(q, ignoreCase = true) || 
        it.summary.contains(q, ignoreCase = true) || 
        it.neuroBasics.contains(q, ignoreCase = true)
    }

    val matchedDiagnoses = ScientificContent.diagnoses.filter {
        it.id.contains(q, ignoreCase = true) || 
        it.name.contains(q, ignoreCase = true) || 
        it.dynamik.contains(q, ignoreCase = true) || 
        it.absicherung.contains(q, ignoreCase = true) || 
        it.klaerung.contains(q, ignoreCase = true) || 
        it.aufloesung.contains(q, ignoreCase = true)
    }

    val matchedNeuro = ScientificContent.neuroArticles.filter {
        it.title.contains(q, ignoreCase = true) || 
        it.content.contains(q, ignoreCase = true)
    }

    val matchedComm = ScientificContent.commArticles.filter {
        it.title.contains(q, ignoreCase = true) || 
        it.description.contains(q, ignoreCase = true)
    }

    val matchedTeam = ScientificContent.teamArticles.filter {
        it.title.contains(q, ignoreCase = true) || 
        it.content.contains(q, ignoreCase = true)
    }

    val totalMatches = matchedPhases.size + matchedDiagnoses.size + matchedNeuro.size + matchedComm.size + matchedTeam.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onSearchChange("") },
                    modifier = Modifier.testTag("clear_search_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Suche zurücksetzen")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Suchergebnisse ($totalMatches)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Echtzeit-Treffer für „$searchQuery“ in allen Standardkapiteln und Diagnosen:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (totalMatches == 0) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Keine passenden Übereinstimmungen gefunden.\nBitte korrigieren Sie Ihre Suche oder leeren Sie das Feld.",
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        if (matchedPhases.isNotEmpty()) {
            item {
                Text(
                    text = "PHASEN-ÜBEREINSTIMMUNGEN",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(matchedPhases) { phase ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Phase: ${phase.id} (${phase.subtitle})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = phase.summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = { onNavigateToPhase(phase.id) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Öffne Phase ${phase.id} in Kap. 4 →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (matchedDiagnoses.isNotEmpty()) {
            item {
                Text(
                    text = "DIAGNOSEN-ÜBEREINSTIMMUNGEN",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(matchedDiagnoses) { diag ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = diag.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = diag.dynamik, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = { onNavigateToDiagnosis(diag.id) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("In diagnostische Strategien (Kap. 5) öffnen →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (matchedNeuro.isNotEmpty()) {
            item {
                Text(
                    text = "KAPITEL 2: NEUROBIOLOGIE TREFFER",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(matchedNeuro) { art ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = art.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = art.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = { onNavigateToChapter(2) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("In Kapitel 2 (Neurobiologie) öffnen →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (matchedComm.isNotEmpty()) {
            item {
                Text(
                    text = "KAPITEL 3: GESPRÄCHSFÜHRUNG TREFFER",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(matchedComm) { art ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = art.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = art.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = { onNavigateToChapter(3) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("In Kapitel 3 (Kommunikation) öffnen →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (matchedTeam.isNotEmpty()) {
            item {
                Text(
                    text = "KAPITEL 6: TEAM & NACHBEREITUNG TREFFER",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(matchedTeam) { art ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = art.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = art.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = { onNavigateToChapter(6) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("In Kapitel 6 (Nachbereitung) öffnen →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}


// ══════════════════════════════════════════════════════
// 4. TOOLS & CLINICAL WORKSPACE SCREEN (Room DB writes)
// ══════════════════════════════════════════════════════
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ToolsScreen(
    breathingPhase: BreathingPhase,
    breathingSeconds: Int,
    breathingCycles: Int,
    crisisPlans: List<CrisisPlan>,
    incidentReviews: List<IncidentReview>,
    teamLearnings: List<TeamLearning>,
    toolsMainTab: String,
    toolsSubTab: String,
    onToolsMainTabChange: (String) -> Unit,
    onToolsSubTabChange: (String) -> Unit,
    onStartBreathing: () -> Unit,
    onStopBreathing: () -> Unit,
    onSaveCrisisPlan: (String, String, String, String, String, String) -> Unit,
    onDeleteCrisisPlan: (Int) -> Unit,
    onSaveIncidentReview: (String, String, String, String, String, String, String) -> Unit,
    onDeleteIncidentReview: (Int) -> Unit,
    onSaveTeamLearning: (String, String, String) -> Unit,
    onDeleteTeamLearning: (Int) -> Unit,
    icdDiagnoses: List<com.example.data.IcdDiagnosis>
) {
    val context = LocalContext.current
    val mainTab = toolsMainTab
    val subTab = toolsSubTab

    // Assistance Alert Local State
    var assistantActive by remember { mutableStateOf(false) }
    var activeAlarmLevel by remember { mutableStateOf("GELB") }
    var alarmSecondsElapsed by remember { mutableStateOf(0) }
    var alarmRespondersCount by remember { mutableStateOf(0) }
    var collapseRequested by remember { mutableStateOf(false) }

    LaunchedEffect(assistantActive) {
        if (assistantActive) {
            alarmSecondsElapsed = 0
            alarmRespondersCount = 0
            while (true) {
                delay(1000)
                alarmSecondsElapsed++
                if (alarmSecondsElapsed == 4) alarmRespondersCount = 1
                if (alarmSecondsElapsed == 9) alarmRespondersCount = 2
                if (alarmSecondsElapsed == 15) alarmRespondersCount = 3
            }
        }
    }

    // 2-Tap Micro-Report State
    var ptInitials by remember { mutableStateOf("") }
    var selectedBehavior by remember { mutableStateOf("") }
    var selectedIntervention by remember { mutableStateOf("") }
    var selectedResult by remember { mutableStateOf("") }
    var generatedReportText by remember { mutableStateOf("") }

    // DBT Cold Shock / Temperature Timer State
    var tTimerActive by remember { mutableStateOf(false) }
    var tSecondsLeft by remember { mutableStateOf(30) }
    LaunchedEffect(tTimerActive) {
        if (tTimerActive) {
            tSecondsLeft = 30
            while (tSecondsLeft > 0) {
                delay(1000)
                tSecondsLeft--
            }
            tTimerActive = false
        }
    }

    // Sensory reduction checklist state
    var sensoryLightsDipped by remember { mutableStateOf(false) }
    var sensoryNoiseClosed by remember { mutableStateOf(false) }
    var sensoryAudienceRemoved by remember { mutableStateOf(false) }
    var sensoryDistanceMaintained by remember { mutableStateOf(false) }

    // Case Simulations State
    var currentSimIndex by remember { mutableStateOf(0) }
    var selectedSimAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var simIsSubmitted by remember { mutableStateOf(false) }

    // Mini-Quizzes State
    var quizScore by remember { mutableStateOf(0) }
    var currentQuizIndex by remember { mutableStateOf(0) }
    var selectedQuizAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var quizIsSubmitted by remember { mutableStateOf(false) }

    // Team learning addition State
    var newLearningSit by remember { mutableStateOf("") }
    var newLearningWorked by remember { mutableStateOf("") }
    var newLearningRole by remember { mutableStateOf("Pflege") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Klinische Praxis & Co-Regulation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Interaktives Station-Zentrum für Co-Regulation, Teamalarme, 2-Tap Patientenberichte und Didaktik.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // --- PRIMARY CATEGORY TABS ---
        item {
            ScrollableTabRow(
                selectedTabIndex = when (mainTab) {
                    "COREG_SKILLS" -> 0
                    "TEAM_STATION" -> 1
                    "LEARN_REVIEWS" -> 2
                    else -> 0
                },
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).fillMaxWidth()
            ) {
                Tab(
                    selected = mainTab == "COREG_SKILLS",
                    onClick = { onToolsMainTabChange("COREG_SKILLS") },
                    text = { Text("Co-Reg & Skills", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = mainTab == "TEAM_STATION",
                    onClick = { onToolsMainTabChange("TEAM_STATION") },
                    text = { Text("Team & Station", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = mainTab == "LEARN_REVIEWS",
                    onClick = { onToolsMainTabChange("LEARN_REVIEWS") },
                    text = { Text("Reviews & Lernen", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        // --- SECONDARY Pills SubSelector Row ---
        item {
            SingleLineFlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                val subTabOptions = when (mainTab) {
                    "COREG_SKILLS" -> listOf(
                        Pair("BREATHING", "Atemcoaching"),
                        Pair("DBT_SENSORY", "DBT & Sensorik"),
                        Pair("VERBAL_SCRIPTS", "Gesprächsscripts")
                    )
                    "TEAM_STATION" -> listOf(
                        Pair("ASSIST_ALERT", "Team-Assist Alert"),
                        Pair("MICRO_REPORT", "2-Tap Bericht"),
                        Pair("UNIT_INFO", "Pflegeprozedere & Kontakte"),
                        Pair("CRISIS_PLANS", "Krisenpläne (DB)")
                    )
                    "LEARN_REVIEWS" -> listOf(
                        Pair("CASE_SIMS", "Fallsimulation"),
                        Pair("QUIZZES", "Wissensquiz"),
                        Pair("TEAM_LEARNING", "Team learning (DB)"),
                        Pair("INCIDENT_REVIEWS", "Review-Archiv")
                    )
                    else -> emptyList()
                }

                subTabOptions.forEach { (tabId, label) ->
                    val isSelected = subTab == tabId
                    Button(
                        onClick = { onToolsSubTabChange(tabId) },
                        modifier = Modifier.testTag("subtab_$tabId"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- CONTENT SCREEN DIRECT ROUTING ---
        when (subTab) {
            "BREATHING" -> {
                // PACED BREATHING COMPONENT WITH VERBAL CUES
                item {
                    BreathingGuideComponent(
                        breathingPhase = breathingPhase,
                        breathingSeconds = breathingSeconds,
                        breathingCycles = breathingCycles,
                        onStartBreathing = onStartBreathing,
                        onStopBreathing = onStopBreathing
                    )
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Co-Regulativer Audio-/Visueller Taktgeber", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nutzen Sie ein tiefes Sprechtempo und senken Sie Ihre Stimmlage synchron zur Ausatmungsphase ab (Pulsierender Ring).",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            "DBT_SENSORY" -> {
                // DBT SKILLS (STOP, TIPP) & SENSORY TOOLKITS
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "DBT TIPP-SKILL: Kälte / Temperatur", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ein plötzlicher Kältereiz im Gesicht (z.B. Eispack oder eiskaltes Wasser) löst sekundenschnell den Tauchreflex (Mammalian Dive Reflex) aus. Dies verlangsamt die Herzfrequenz sympathomimetisch, senkt extreme Anspannung schlagartig und stellt den Wise Mind her.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = { tTimerActive = !tTimerActive },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (tTimerActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(if (tTimerActive) Icons.Default.Close else Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (tTimerActive) "Skill abbrechen" else "Eispack-Timer starten (30s)")
                                }

                                if (tTimerActive) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(MaterialTheme.colorScheme.errorContainer, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$tSecondsLeft",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Sensory De-escalation & Reizreduktion", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Die Reduzierung externer Stressfaktoren schützt das Nervensystem in Phase GELB vor dem Kippen in Phase ROT (Amygdala-Hijack).",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("STANZ-REIZREDUKTIONS-CHECKLISTE:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = sensoryLightsDipped, onCheckedChange = { sensoryLightsDipped = it })
                                    Text("Beleuchtung dimmen / Deckenlichter ausschalten", fontSize = 12.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = sensoryNoiseClosed, onCheckedChange = { sensoryNoiseClosed = it })
                                    Text("Fenster schließen, Lärmpegel dämpfen", fontSize = 12.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = sensoryAudienceRemoved, onCheckedChange = { sensoryAudienceRemoved = it })
                                    Text("Zuschauer & andere Patienten aus dem Raum weisen", fontSize = 12.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = sensoryDistanceMaintained, onCheckedChange = { sensoryDistanceMaintained = it })
                                    Text("Seitlicher Abstand von 1,5m bis 2m dauerhaft gewahrt", fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("SENSORY-KIT INVENTAR (Schrankpflege):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            Text("• Gewichtsdecke (Schwere Decke für propriozeptiven Druck)\n• Rauschunterdrückende Kopfhörer (Noise-Cancelling)\n• Igelbälle, Stressbälle (Taktile Fokussierung)\n• Ätherische Öle (Olfaktorischer Reiz: Lavendel oder Ammoniak für Hochspannung)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            "VERBAL_SCRIPTS" -> {
                // VERBAL PHRASES & SCRIPTS FOR CRITICAL CLINICAL ENGAGEMENTS
                item {
                    Text("Deeskalation-Gesprächsscripts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                val verbalPhrases = listOf(
                    Triple(
                        "Extremer Erregungszustand",
                        "Für Jugendliche mit traumatischer Disregulation oder heftiger Panik.",
                        listOf(
                            "„Ich bin hier bei dir. Du bist auf Station und in Sicherheit.“",
                            "„Es ist gerade extrem viel für dich – wir atmen jetzt zusammen ein... und aus.“",
                            "„Ich tue dir nichts und ich weiche nicht zurück. Du schaffst das.“"
                        )
                    ),
                    Triple(
                        "Konfliktsituation & Abgrenzung",
                        "Zur Deeskalation von verbaler Feindseligkeit in Phase GELB.",
                        listOf(
                            "„Ich sehe, dass du kochend vor Wut bist. Lass uns in ein ruhiges Zimmer gehen und das klären.“",
                            "„Ich möchte dich verstehen, aber ich brauche, dass du in einem ruhigeren Ton mit mir sprichst.“",
                            "„Es geht mir nicht darum, dich zu bestrafen, sondern ich will, dass du sicher bist.“"
                        )
                    ),
                    Triple(
                        "Dissoziatives Abdriften",
                        "Bei einsetzender Starre oder Abwesenheit (Präventionsreiz).",
                        listOf(
                            "„Sprich mir nach: Ich stehe fest auf dem Boden...“",
                            "„Drücke deine Füße fest in den Boden. Spürst du die Fliesen?“",
                            "„Wenn du mich hören kannst, ball deine Hände ganz fest zusammen und lass wieder los.“"
                        )
                    )
                )

                items(verbalPhrases) { (title, subtitle, phrases) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            phrases.forEach { phrase ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText("Deeskalationsscript", phrase)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Satz in Zwischenablage kopiert!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = phrase, fontSize = 11.sp, fontWeight = FontWeight.Medium, fontStyle = FontStyle.Italic)
                                }
                            }
                        }
                    }
                }
            }
            "ASSIST_ALERT" -> {
                // NEED ASSISTANCE EMERGENCY ALERT PANEL
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (assistantActive) {
                                when (activeAlarmLevel) {
                                    "ROT" -> Color(0xFFFEE2E2)
                                    "ORANGE" -> Color(0xFFFFEDD5)
                                    else -> Color(0xFFFEF3C7)
                                }
                            } else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = if (assistantActive) 2.dp else 1.dp,
                            color = if (assistantActive) {
                                when (activeAlarmLevel) {
                                    "ROT" -> Color(0xFFEF4444)
                                    "ORANGE" -> Color(0xFFF97316)
                                    else -> Color(0xFFF59E0B)
                                }
                            } else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (assistantActive) Icons.Default.Warning else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (assistantActive) Color(0xFFB91C1C) else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (assistantActive) "TEAM-NOTRUF AKTIViert!" else "Team-Assistenz anfordern",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = if (assistantActive) Color(0xFF991B1B) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Der mobile Notruf simuliert eine verschlüsselte Peer-to-Peer Wlan-Notfallkette an das Stationspersonal. Sichert unaufgeregtes Nachrücken an den Krisenort.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (!assistantActive) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text("ALARMSTUFE AUSWÄHLEN:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            activeAlarmLevel = "GELB"
                                            assistantActive = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                        modifier = Modifier.weight(1f).testTag("alert_yellow")
                                    ) {
                                        Text("Gelb (Support)", fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = {
                                            activeAlarmLevel = "ORANGE"
                                            assistantActive = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                                        modifier = Modifier.weight(1f).testTag("alert_orange")
                                    ) {
                                        Text("Orange (Co-Reg)", fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = {
                                            activeAlarmLevel = "ROT"
                                            assistantActive = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                        modifier = Modifier.weight(1f).testTag("alert_red")
                                    ) {
                                        Text("ROT (NOTRUF)", fontSize = 10.sp)
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.height(16.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Typ: Alarmstufe $activeAlarmLevel", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF7F1D1D))
                                            Text("Aktiv seit: $alarmSecondsElapsed Sek.", fontSize = 12.sp, color = Color(0xFF991B1B))
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (alarmRespondersCount == 0) "Suche freie Einheiten..." else "$alarmRespondersCount Kollegen haben bestätigt und eilen herbei!",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF14532D)
                                            )
                                        }

                                        if (alarmSecondsElapsed >= 15) {
                                            Text("• Dr. Becker (Oberarzt-Dienst) wurde per Krisentelefon hinzugerufen.", fontSize = 11.sp, fontStyle = FontStyle.Italic)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { collapseRequested = !collapseRequested },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (collapseRequested) Color(0xFF1D4ED8) else Color(0xFF6B7280)
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (collapseRequested) "Ablösung gerufen!" else "Sprecher ablösen", fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = {
                                            assistantActive = false
                                            collapseRequested = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF166534)),
                                        modifier = Modifier.weight(1f).testTag("dismiss_alarm")
                                    ) {
                                        Text("Entwarnung / Reset", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "MICRO_REPORT" -> {
                // 2-TAP MICRO-REPORT TOOL
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "2-Tap Mikro-Bericht-Dokumentation", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Akutkräfte haben wenig Zeit. Tippen Sie das Verhalten des Jugendlichen und Ihre angewandte Deeskalations-Intervention an. Das Tool kompiliert sofort einen professionellen, DSGVO-konformen Verlaufsbericht für die Pflegedokumentation.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = ptInitials,
                                onValueChange = { ptInitials = it },
                                label = { Text("Kürzel Jugendliche(r) (Optional, z.B. J.K.)", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("1. VERHALTEN / AUSGANGSSITUATION (1-Tap):", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(
                                    "Starke innere Anspannung & unruhige Gereiztheit (GELB)",
                                    "Verbale Drohung und Grenzüberschreitung",
                                    "Dissoziative Erstarrung & Rückzug (ROT)",
                                    "Körperliche Aggression gegen Inventar",
                                    "Scham-Abwehr & laute Provokation"
                                ).forEach { behavior ->
                                    val isSel = selectedBehavior == behavior
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedBehavior = behavior }
                                            .testTag("report_behavior_${behavior.take(4).lowercase()}"),
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    ) {
                                        Text(text = behavior, fontSize = 11.sp, modifier = Modifier.padding(8.dp), fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("2. INTERVENTION DER AKUTKRAFT (2-Tap):", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(
                                    "Validation und schamsensibles GFK-Gespräch",
                                    "Co-regulatives Atmen & Stimmabsenkung",
                                    "Reizreduktion und physische Distanzgewährung",
                                    "Anleitung DBT STOP & TIPP Kältereiz (Eispack)",
                                    "Regulationsangebot über Sensory-Kit (Gewichtsdecke)"
                                ).forEach { inter ->
                                    val isSel = selectedIntervention == inter
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedIntervention = inter }
                                            .testTag("report_inter_${inter.take(4).lowercase()}"),
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    ) {
                                        Text(text = inter, fontSize = 11.sp, modifier = Modifier.padding(8.dp), fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("3. KLINISCHES ERGEBNIS:", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(
                                    "Erfolgreich co-reguliert, Jugendliche(r) in Phase GRÜN",
                                    "Situation beruhigt, engmaschige Begleitung fortgesetzt",
                                    "Arzt zur diagnostischen Klärung hinzugerufen"
                                ).forEach { res ->
                                    val isSel = selectedResult == res
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedResult = res },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    ) {
                                        Text(text = res, fontSize = 11.sp, modifier = Modifier.padding(8.dp), fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                                    val init = if (ptInitials.isNotBlank()) "bei Jugendliche(r) ($ptInitials)" else "beim Jugendlichen"
                                    generatedReportText = "Doku $timeStr Uhr: Akute Krise $init.\nReaktion: $selectedBehavior.\nKlinische Deeskalation: $selectedIntervention.\nVerlauf: $selectedResult. Keine freiheitsentziehenden Maßnahmen nötig."
                                },
                                enabled = selectedBehavior.isNotEmpty() && selectedIntervention.isNotEmpty() && selectedResult.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth().testTag("compile_microreport")
                            ) {
                                Text("Berichtsbaustein generieren")
                            }

                            if (generatedReportText.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("GENERIERTER VERLAUFSBERICHT:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = generatedReportText,
                                        fontSize = 11.sp,
                                        fontStyle = FontStyle.Italic,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Patientenbericht", generatedReportText)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Doku-Text in Zwischenablage kopiert!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Eintrag kopieren")
                                }
                            }
                        }
                    }
                }
            }
            "UNIT_INFO" -> {
                // PROCEDURAL CLINICAL PROCEDURES, CONTACT DIRECTORY & SBAR HANDOVER GUIDE
                item {
                    Text("Interne Station-Info & Prozedere", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("ZWANGSVERMEIDUNGS- & SCHUTZ-PROTOKOLL", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. Äußerste Ratio: Freiheitsentziehende Maßnahmen (Fixierung, Isolierung) sind die absolute Ausnahme bei unmittelbarer Eigen-/Fremdgefährdung.\n" +
                                        "2. Genehmigung: Ärztliche Anordnung unverzüglich einholen.\n" +
                                        "3. Dauer-Sitzwache: 1-to-1 Begleitung durch Pflegekraft unterbrochen herstellen.\n" +
                                        "4. Vitalwerte: Alle 15 Minuten Puls, Atmung, Bewusstseinslage messen und dokumentieren.\n" +
                                        "5. Debriefing: Nach jeder Zwangsmaßnahme Nachbesprechung mit Patient und Team innerhalb von 24 Std.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("SBAR-ÜBERGABE-SCHEIN (Strukturierte Visite)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "S – Situation: „Wer ist betroffen? Was ist der akute Zustand?“\n" +
                                        "B – Background: „Was ist der therapeutische Hintergrund? Trauma? ASS? Vorgeschichte?“\n" +
                                        "A – Assessment: „Wie schätze ich die Lage ein? In welcher Phase ist der Jugendliche?“\n" +
                                        "R – Recommendation: „Welcher Krisenplan gilt künftig? Welche Erregungstrigger vermeiden?“",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("ANSPRECHPARTNER & KLINISCHE NUMMERN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• KJP Arzt (Dienstzeit-Zentrale): Tel: 3012\n" +
                                    "• Oberarzt-Krisentelefon (Harburg): Tel: 3044\n" +
                                    "• Kriseninterventionsdienst (Mobil): Tel: 9110\n" +
                                    "• Pflegedirektion Akutstation: Tel: 4402\n" +
                                    "• Kälte-Eispack & Sensory-Zubehör: Dienstzimmer-Kühlschrank 1. Stock / Schrank 2A",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            "CRISIS_PLANS" -> {
                // CRISIS PLANS MANAGEMENT (Room DB integration)
                item {
                    CrisisPlanWorkspaceSection(
                        crisisPlans = crisisPlans,
                        onSaveCrisisPlan = onSaveCrisisPlan,
                        onDeleteCrisisPlan = onDeleteCrisisPlan,
                        icdDiagnoses = icdDiagnoses
                    )
                }
            }
            "CASE_SIMS" -> {
                // STRATEGIC SCENARIO DECISION PATH TRAINING
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Fall-Szenario-Entscheidungs-Training", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lösen Sie reale Akutfälle spielerisch. Ihre Entscheidungspfade deeskalieren oder verschlimmern die Erregung aus neurobiologischer Sicht.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Case Scenario Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Szenario ${currentSimIndex + 1} von 2", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                TextButton(onClick = {
                                    currentSimIndex = (currentSimIndex + 1) % 2
                                    selectedSimAnswerIndex = null
                                    simIsSubmitted = false
                                }) {
                                    Text("Nächstes Szenario →", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val currentScenario = if (currentSimIndex == 0) {
                                // Scenario 1
                                Triple(
                                    "Frühwarnung bei Autismus (ASS) u18",
                                    "Ein 15-jähriger Junge mit ASS steht vor dem verschlossenen Stationskiosk, schreit hysterisch und tritt aggressiv gegen das Gitter, weil er seine vertraute Limonadenmarke heute nicht erhält:",
                                    listOf(
                                        "A) Ihm ruhig und bestimmt erklären, dass die Kioskzeit abgelaufen ist und die Hausordnung für alle gleich gilt.",
                                        "B) Die Limonade sofort herausgeben, um die Aggression abzufangen, selbst wenn es regelfreie Ausnahmen erzeugt.",
                                        "C) Seine sensorische Überreizung validieren, den Fluchtweg unversperrt seitlich absichern, Reizquellen dämpfen und ihm eine visuell strukturierte Alternative anbieten."
                                    )
                                )
                            } else {
                                // Scenario 2
                                Triple(
                                    "EIPS Krisenregulation am Gang",
                                    "Eine 16-jährige Patientin mit EIPS (GELB) läuft weinend über den Stationsgang und schlägt sich leicht den Kopf an die Wand. Ein Kollege herrscht sie im Gang lautstark an, sofort in ihr Zimmer zu verschwinden:",
                                    listOf(
                                        "A) Sich lautstark einmischen und den Kollegen vor der Gruppe kritisieren, um die Patientin zu schützen.",
                                        "B) Die Patientin ruhig ansprechen, sie diskret in ein ruhiges Therapiezimmer begleiten, Gefühle validieren und ihr ein DBT TIPP Eispack-Kohlereiz anbieten.",
                                        "C) Den Kollegen gewähren lassen, da beziehungsorientierte Zuwendung in diesem Moment das selbstverletzende Verhalten ungewollt verstärken würde."
                                    )
                                )
                            }

                            Text(text = currentScenario.first, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = currentScenario.second, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 17.sp)

                            Spacer(modifier = Modifier.height(12.dp))

                            currentScenario.third.forEachIndexed { idx, ans ->
                                val isSelected = selectedSimAnswerIndex == idx
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            if (!simIsSubmitted) {
                                                selectedSimAnswerIndex = idx
                                            }
                                        },
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Text(text = ans, fontSize = 11.sp, modifier = Modifier.padding(10.dp))
                                }
                            }

                            if (selectedSimAnswerIndex != null && !simIsSubmitted) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { simIsSubmitted = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Entscheidung pathologische Auswertung")
                                }
                            }

                            if (simIsSubmitted) {
                                Spacer(modifier = Modifier.height(12.dp))
                                val explanation = if (currentSimIndex == 0) {
                                    when (selectedSimAnswerIndex) {
                                        0 -> "❌ Nicht optimal! Bei ASS blockiert extremer Stress die rationale Informationsverarbeitung. Das Beharren auf Paragraphen (Hausordnung) wirkt wie Verachtung und triggert massiven Amygdala Hijack."
                                        1 -> "⚠️ Nur bedinkter Teilerfolg! Das sofortige Nachgeben verhindert zwar die Eskalation, trainiert jedoch unbewusst instrumentelle Gewalt und begünstigt Splitting-Dynamiken auf Station."
                                        else -> "✓ Ausgezeichnete Deeskalation! Gefühlsvalidation nimmt die Not an. Durch die sensorische Abkühlung (Reizreduktion) und das visuelle Angebot wird der ventrale Vagus reaktiviert."
                                    }
                                } else {
                                    when (selectedSimAnswerIndex) {
                                        0 -> "❌ Fatal! Das Kritisieren des Kollegen spaltet das Therapeutenteam (Splitting-Falle). Dies verstärkt bei EIPS das Gefühl unzuverlässiger Bindungsgrenzen."
                                        1 -> "✓ Großartig reguliert! Die Verlegung in ein ruhiges Zimmer verhindert die schambeladene Flureskalation vor Dritten. Das Eispack-Kälte-TIPP-Angebot dämpft physiologische Übererregung augenblicklich."
                                        else -> "❌ Vorsicht! Die Annahme, dass emotionale Notfälle bei EIPS reine Manipulation zur Belohnung sind, ist neurobiologisch überholt. Unsichere Bindungsmuster brauchen feinfühlige Haltepunkte."
                                    }
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("FEEDBACK & ERKLÄRUNG:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = explanation, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer, lineHeight = 15.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "QUIZZES" -> {
                // MINI-QUIZZES FOR RETENTION CHECK
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Deeskalations-Wissens-Check", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Prüfen Sie Ihr Fachwissen über Safewards-Konzepte, Polyvagal-Theorie, GFK und DBT.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Frage ${currentQuizIndex + 1} von 3", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("Punkte: $quizScore", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            val quizQuestions = listOf(
                                Triple(
                                    "Wie lange dauert die biologische 'Kortisol-Latenz' nach heftigen emotionalen Krisen (KJP)?",
                                    listOf(
                                        "A) Ca. 2-5 Minuten, danach ist das System sofort wieder neutral",
                                        "B) Mindestens 20-60 Minuten, in denen Verhandlungen wegen Re-Eskalationsgefahr warten müssen",
                                        "C) Exakt 24 Stunden, in denen der Patient das Zimmer nicht verlassen sollte"
                                    ),
                                    1 // Correct Answer index
                                ),
                                Triple(
                                    "Welchen physiologischen Effekt bewirkt das DBT TIPP Temperatur-Element (Eispack im Gesicht)?",
                                    listOf(
                                        "A) Triggert den Tauchreflex, der den Puls augenblicklich drosselt",
                                        "B) Es erzeugt langanhaltende Schmerzen zur Reizüberlagerung",
                                        "C) Es blockiert die visuelle Perceptual Narrowing Seh-Einschränkung"
                                    ),
                                    0 // Correct index
                                ),
                                Triple(
                                    "Wie sollte Kritik an Jugendlichen geäußert werden, um extreme Scham-Wut-Spiralen abzufangen?",
                                    listOf(
                                        "A) Unmittelbar auf dem Gang, um ein klares Statement für alle Akteure zu setzen",
                                        "B) Unter vier Augen in einem separaten Rückzugsraum",
                                        "C) Durch konsequentes Ignorieren über den restlichen Tag"
                                    ),
                                    1 // Correct index
                                )
                            )

                            val qObj = quizQuestions[currentQuizIndex]
                            Text(text = qObj.first, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(8.dp))

                            qObj.second.forEachIndexed { index, ansOption ->
                                val isSelected = selectedQuizAnswerIndex == index
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            if (!quizIsSubmitted) {
                                                selectedQuizAnswerIndex = index
                                            }
                                        },
                                    color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Text(text = ansOption, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                                }
                            }

                            if (selectedQuizAnswerIndex != null && !quizIsSubmitted) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        quizIsSubmitted = true
                                        if (selectedQuizAnswerIndex == qObj.third) {
                                            quizScore += 10
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("submit_quiz_answer")
                                ) {
                                    Text("Antwort überprüfen")
                                }
                            }

                            if (quizIsSubmitted) {
                                Spacer(modifier = Modifier.height(10.dp))
                                val isCorrect = selectedQuizAnswerIndex == qObj.third
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCorrect) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = if (isCorrect) "✓ Richtig gelöst!" else "✗ Leider falsch!",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isCorrect) Color(0xFF166534) else Color(0xFF991B1B)
                                        )
                                        Text(
                                            text = "Wissenschaftlicher Hintergrund: " + if (currentQuizIndex == 0) {
                                                "Kortisol flutet im Blut extrem träge ab. Wer zu früh klärt, re-eskaliert unfreiwillig, da die Amygdala hochsensibel geschaltet bleibt."
                                            } else if (currentQuizIndex == 1) {
                                                "Der parasympathische Vagus-Ast wird durch Kälte-Thermorezeptoren getriggert – senkt die vegetative Grundspannung."
                                            } else {
                                                "Scham ist die schmerzhafteste Emotion. Kritik vor Dritten vernichtet das Selbstwertgefühl und mündet fast immer in Gegenangriffe."
                                            },
                                            fontSize = 10.sp,
                                            color = if (isCorrect) Color(0xFF14532D) else Color(0xFF7F1D1D)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        if (currentQuizIndex < 2) {
                                            currentQuizIndex++
                                        } else {
                                            currentQuizIndex = 0
                                            quizScore = 0
                                        }
                                        selectedQuizAnswerIndex = null
                                        quizIsSubmitted = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(if (currentQuizIndex < 2) "Nächste Frage" else "Quiz neustarten")
                                }
                            }
                        }
                    }
                }
            }
            "TEAM_LEARNING" -> {
                // TEAM LEARNING SYSTEM ("WAS HAT FUNKTIONIERT?")
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Kollegiales Best-Practice Board", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Was hat in einer eskalativen Situation funktioniert? Teilen Sie Ihre Erfahrungen unaufgeregt und offline-verfügbar für das gesamte Team.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = newLearningSit,
                                onValueChange = { newLearningSit = it },
                                label = { Text("Situation / Auslöser (z.B. Visite ADHS)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("learning_input_situation")
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = newLearningWorked,
                                onValueChange = { newLearningWorked = it },
                                label = { Text("Was hat geholfen? (z.B. Timebox visuell gestellt)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("learning_input_worked")
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Rolle:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                listOf("Pflege", "Arzt", "Therapeut").forEach { role ->
                                    val isSel = newLearningRole == role
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { newLearningRole = role },
                                        label = { Text(role, fontSize = 10.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    onSaveTeamLearning(newLearningSit, newLearningWorked, newLearningRole)
                                    newLearningSit = ""
                                    newLearningWorked = ""
                                    Toast.makeText(context, "Erfahrung kollegial geteilt!", Toast.LENGTH_SHORT).show()
                                },
                                enabled = newLearningSit.isNotBlank() && newLearningWorked.isNotBlank(),
                                modifier = Modifier.fillMaxWidth().testTag("save_team_learning_button")
                            ) {
                                Text("Auf Board posten")
                            }
                        }
                    }
                }

                if (teamLearnings.isEmpty()) {
                    item {
                        Text("Moderierte Best-Practices der Station:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                    val defaultPractices = listOf(
                        Triple("ASS Reizüberflutung", "Sinnvoll: Gewichtsdecke und Kopfhörer im Entspannungsraum unaufgefordert anbieten.", "Pflege"),
                        Triple("ADHS Entladung", "Sinnvoll: Klare, visuelle Sanduhr einsetzen und Bewegungsalternativen im KJP-Schulhof erlauben.", "Therapeut"),
                        Triple("EIPS Dissoziation", "Sinnvoll: Kältesensoren aktivieren (Eispack im Nacken) statt kognitiven Dialogen.", "Arzt")
                    )
                    items(defaultPractices) { (sit, helped, role) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Situation: $sit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(4.dp)) {
                                        Text(text = role, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Hilfreich: $helped", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    item {
                        Text("Kollegiale Beiträge (${teamLearnings.size}):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    items(teamLearnings) { learning ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Klient/Sit: ${learning.situation}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp)) {
                                            Text(text = learning.submittedByRole, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { onDeleteTeamLearning(learning.id) },
                                            modifier = Modifier.size(24.dp).testTag("delete_learning_${learning.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Löschen", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Hilfreich: ${learning.whatWorked}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            "INCIDENT_REVIEWS" -> {
                // ARCHIVED POST-INCIDENT REVIEWS (Room DB integration)
                item {
                    IncidentReviewWorkspaceSection(
                        reviews = incidentReviews,
                        onSaveIncidentReview = onSaveIncidentReview,
                        onDeleteIncidentReview = onDeleteIncidentReview
                    )
                }
            }
        }
    }
}

@Composable
fun SingleLineFlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    // Elegant fall-back container to layout children horizontally scrollable on mobile
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
        Spacer(modifier = Modifier.width(20.dp)) // horizontal breathing space
    }
}


// ══════════════════════════════════════════════════════
// paced breathing co-regulation guide
// ══════════════════════════════════════════════════════
@Composable
fun BreathingGuideComponent(
    breathingPhase: BreathingPhase,
    breathingSeconds: Int,
    breathingCycles: Int,
    onStartBreathing: () -> Unit,
    onStopBreathing: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("breathing_guide_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Paced Breathing Co-Regulation",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Aktiviert den ventralen Vagus parasympathisch über ein verlangsamtes Atemverhältnis (4s Einatmen, 8s Ausatmen). Perfekt als Co-Regulations-Modell.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Animated breathing circle scaling
            val scale by animateFloatAsState(
                targetValue = when (breathingPhase) {
                    BreathingPhase.INHALE -> 1.5f
                    BreathingPhase.EXHALE -> 0.8f
                    BreathingPhase.IDLE -> 1.0f
                },
                animationSpec = tween(
                    durationMillis = when (breathingPhase) {
                        BreathingPhase.INHALE -> 4000
                        BreathingPhase.EXHALE -> 8000
                        BreathingPhase.IDLE -> 300
                    },
                    easing = LinearEasing
                ),
                label = "breathingScale"
            )

            val circleColor by animateColorAsState(
                targetValue = when (breathingPhase) {
                    BreathingPhase.INHALE -> Color(0xFF93C5FD) // soft blue
                    BreathingPhase.EXHALE -> Color(0xFF86EFAC) // calm green
                    BreathingPhase.IDLE -> MaterialTheme.colorScheme.outlineVariant
                },
                animationSpec = tween(300),
                label = "circleColor"
            )

            Box(
                modifier = Modifier
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // outer pulsed background
                Box(
                    modifier = Modifier
                        .size((110 * scale).dp)
                        .clip(CircleShape)
                        .background(circleColor.copy(alpha = 0.25f))
                )

                // inner solid target
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(circleColor)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (breathingPhase != BreathingPhase.IDLE) {
                            Text(
                                text = "$breathingSeconds",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Sekunden",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Text Cues & Metronome helper text
            Text(
                text = when (breathingPhase) {
                    BreathingPhase.INHALE -> "EINATMEN... (Füllen)"
                    BreathingPhase.EXHALE -> "AUSATMEN... (Entspannen und senken)"
                    BreathingPhase.IDLE -> "Bereit zur Co-Regulation"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = when (breathingPhase) {
                    BreathingPhase.INHALE -> Color(0xFF1E40AF)
                    BreathingPhase.EXHALE -> Color(0xFF166534)
                    BreathingPhase.IDLE -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            if (breathingPhase != BreathingPhase.IDLE) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Zyklusanzahl: $breathingCycles geklappt",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (breathingPhase == BreathingPhase.IDLE) {
                    Button(
                        onClick = onStartBreathing,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .testTag("start_breathing_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Rhythmus starten", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onStopBreathing,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .testTag("stop_breathing_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Anhalten / Reset", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// Patient Crisis Plan section
// ══════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrisisPlanWorkspaceSection(
    crisisPlans: List<CrisisPlan>,
    onSaveCrisisPlan: (String, String, String, String, String, String) -> Unit,
    onDeleteCrisisPlan: (Int) -> Unit,
    icdDiagnoses: List<com.example.data.IcdDiagnosis>
) {
    var showForm by remember { mutableStateOf(false) }

    var initials by remember { mutableStateOf("") }
    var selectedDiagId by remember { mutableStateOf("EIPS") }
    var triggerText by remember { mutableStateOf("") }
    var warningText by remember { mutableStateOf("") }
    var calmingText by remember { mutableStateOf("") }
    var worseningText by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Individuelle Patienten-Krisenpläne",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = { showForm = !showForm },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("toggle_crisis_form_button")
            ) {
                Icon(
                    imageVector = if (showForm) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (showForm) "Schließen" else "Neu erstellen", fontSize = 11.sp)
            }
        }

        if (showForm) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("crisis_plan_form_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Krisenplan eintragen (WEISS/GRÜN Interaktion)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = initials,
                        onValueChange = { initials = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_initials"),
                        placeholder = { Text("Patienten-Kürzel (z.B. A.B.)") },
                        label = { Text("Initiale / Kürzel") },
                        singleLine = true
                    )

                    // Diagnostic selector input
                    Text(text = "Hauptdiagnose auswählen:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val itemsList = icdDiagnoses.map { it.codeOrId }.take(5).ifEmpty { listOf("ADHS", "EIPS", "PTBS", "ASS", "Psychose") }
                        
                        // Fallback default selection in case current selection isn't in dynamic list
                        if (selectedDiagId !in itemsList && itemsList.isNotEmpty()) {
                            selectedDiagId = itemsList.first()
                        }

                        itemsList.forEach { item ->
                            val active = selectedDiagId == item
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .clickable { selectedDiagId = item }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = triggerText,
                        onValueChange = { triggerText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_trigger"),
                        placeholder = { Text("z.B. Körperkontakt, grelles Licht, Ungewissheit...") },
                        label = { Text("Individuelle Trigger") }
                    )

                    OutlinedTextField(
                        value = warningText,
                        onValueChange = { warningText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_warning_signs"),
                        placeholder = { Text("z.B. ballt Fäuste, zieht Kapuze auf, atmet schnell...") },
                        label = { Text("Frühwarnzeichen (Phase GELB)") }
                    )

                    OutlinedTextField(
                        value = calmingText,
                        onValueChange = { calmingText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_calming"),
                        placeholder = { Text("z.B. Paced Breathing, Skill-Box, Rückzug...") },
                        label = { Text("Präferenz zur Co-Regulation") }
                    )

                    OutlinedTextField(
                        value = worseningText,
                        onValueChange = { worseningText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_worsening"),
                        placeholder = { Text("z.B. Festhalten, Diskutieren, Gruppe belehren...") },
                        label = { Text("Was die Krise verschlimmert (Absolute Don'ts)") }
                    )

                    Button(
                        onClick = {
                            if (initials.trim().isEmpty() || triggerText.trim().isEmpty()) {
                                // show simple toast if fields empty
                            } else {
                                onSaveCrisisPlan(
                                    initials,
                                    selectedDiagId,
                                    triggerText,
                                    warningText,
                                    calmingText,
                                    worseningText
                                )
                                // clear
                                initials = ""
                                triggerText = ""
                                warningText = ""
                                calmingText = ""
                                worseningText = ""
                                showForm = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_crisis_plan_button")
                    ) {
                        Text(text = "Speichern & im Team teilen", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (crisisPlans.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Keine individuellen Krisenpläne hinterlegt.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Nutzen Sie den Button oben, um individuelle State-Pläne zu hinterlegen.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            crisisPlans.forEach { plan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = plan.patientInitials.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Krisenplan: ${plan.patientInitials}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Hauptdiagnose: ${plan.mainDiagnosis}",
                                        fontSize = 11.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeleteCrisisPlan(plan.id) },
                                modifier = Modifier.testTag("delete_plan_${plan.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Plan löschen",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        InfoRowDetail(label = "Individuelle Trigger:", value = plan.individualTrigger)
                        InfoRowDetail(label = "Frühwarnung (GELB):", value = plan.earlyWarningSigns)
                        InfoRowDetail(label = "Hilft zur Co-Regulation:", value = plan.preferredCalming)

                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF5F5), RoundedCornerShape(4.dp))
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "VERSCHLIMMERT (DON'T): ${plan.whatVerschlimmert}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB91C1C)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// Post-Incident Debriefing section
// ══════════════════════════════════════════════════════
@Composable
fun IncidentReviewWorkspaceSection(
    reviews: List<IncidentReview>,
    onSaveIncidentReview: (String, String, String, String, String, String, String) -> Unit,
    onDeleteIncidentReview: (Int) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }

    var initials by remember { mutableStateOf("") }
    var incidentDate by remember { mutableStateOf("") }
    var descr by remember { mutableStateOf("") }
    var triggerText by remember { mutableStateOf("") }
    var teamStrengths by remember { mutableStateOf("") }
    var lessonsLearned by remember { mutableStateOf("") }
    var teamWellbeing by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ereignis-Nachbereitung (Debriefing)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = {
                    showForm = !showForm
                    incidentDate = dateFormat.format(Date())
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("toggle_review_form_button")
            ) {
                Icon(
                    imageVector = if (showForm) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (showForm) "Schließen" else "Eintragen", fontSize = 11.sp)
            }
        }

        if (showForm) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("incident_review_form_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Incident Review & Teambegleitung (Phase BLAU)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = initials,
                        onValueChange = { initials = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_initials"),
                        placeholder = { Text("Kürzel (z.B. L.M.)") },
                        label = { Text("Patienten-Initialen") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = incidentDate,
                        onValueChange = { incidentDate = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_date"),
                        placeholder = { Text("z.B. 03.06.2026") },
                        label = { Text("Datum des Vorfalls") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = descr,
                        onValueChange = { descr = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_description"),
                        placeholder = { Text("Was ist konkret vorgefallen? (Fakten)") },
                        label = { Text("Beschreibung des Vorfalls") }
                    )

                    OutlinedTextField(
                        value = triggerText,
                        onValueChange = { triggerText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_trigger"),
                        placeholder = { Text("Was war der eigentliche Auslöser? (Polyvagal)") },
                        label = { Text("Möglicher Trigger-Auslöser") }
                    )

                    OutlinedTextField(
                        value = teamStrengths,
                        onValueChange = { teamStrengths = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_strengths"),
                        placeholder = { Text("Was geland ausgezeichnet? (Sprecher-Rolle etc.)") },
                        label = { Text("Stärken des Teams") }
                    )

                    OutlinedTextField(
                        value = lessonsLearned,
                        onValueChange = { lessonsLearned = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_lessons"),
                        placeholder = { Text("Was machen wir nächstes Mal anders? (Plan-Update)") },
                        label = { Text("Lessons Learned (Kerneffekte)") }
                    )

                    OutlinedTextField(
                        value = teamWellbeing,
                        onValueChange = { teamWellbeing = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_wellbeing"),
                        placeholder = { Text("Wie geht es dem betroffenen Personal? Support benötigt?") },
                        label = { Text("Befinden & Selbstfürsorge des Teams") }
                    )

                    Button(
                        onClick = {
                            if (initials.trim().isEmpty() || descr.trim().isEmpty()) {
                                // invalid fields check
                            } else {
                                onSaveIncidentReview(
                                    initials,
                                    incidentDate,
                                    descr,
                                    triggerText,
                                    teamStrengths,
                                    lessonsLearned,
                                    teamWellbeing
                                )
                                // clear
                                initials = ""
                                incidentDate = ""
                                descr = ""
                                triggerText = ""
                                teamStrengths = ""
                                lessonsLearned = ""
                                teamWellbeing = ""
                                showForm = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_review_button")
                    ) {
                        Text(text = "Review speichern", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (reviews.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Keine Debriefing-Einträge vorhanden.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Nutzen Sie ein Post-Incident-Review, um das Lernen im Team zu verankern.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            reviews.forEach { r ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = r.patientInitials.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Review: ${r.patientInitials}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Vorfallsdatum: ${r.incidentDate}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeleteIncidentReview(r.id) },
                                modifier = Modifier.testTag("delete_review_${r.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eintrag löschen",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        InfoRowDetail(label = "Fakten & Ablauf:", value = r.description)
                        InfoRowDetail(label = "Auslöser (Trigger):", value = r.triggerSource)
                        InfoRowDetail(label = "Stärken im Team:", value = r.teamStrengths)
                        InfoRowDetail(label = "Lessons Learned:", value = r.lessonsLearned)
                        InfoRowDetail(label = "Befinden & Support:", value = r.teamWellbeing)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRowDetail(label: String, value: String) {
    if (value.isNotEmpty()) {
        Column(modifier = Modifier.padding(vertical = 3.dp)) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = value,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}

// ══════════════════════════════════════════════════════
// 5. CMS COMPONENT: CARD VIEW
// ══════════════════════════════════════════════════════
@Composable
fun CmsSectionCard(
    section: CmsSection,
    isAdminView: Boolean = false,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val accentColor = try {
        Color(android.graphics.Color.parseColor(section.accentColorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { if (!isAdminView) expanded = !expanded }
            .testTag("cms_section_card_${section.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, accentColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = accentColor,
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = section.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = section.description,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                if (isAdminView) {
                    Row {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.testTag("edit_cms_btn_${section.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editieren",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.testTag("delete_cms_btn_${section.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Löschen",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                } else {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Wechseln",
                        tint = accentColor
                    )
                }
            }

            if (section.imageUrl.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                coil.compose.AsyncImage(
                    model = section.imageUrl,
                    contentDescription = section.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            if (expanded || isAdminView) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = section.contentText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "Kanal: " + if (section.phaseId == "ALL") "Allgemeines Wissen" else "Phase ${section.phaseId}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                    Text(
                        text = "Aktualisiert: " + SimpleDateFormat("dd.MM, HH:mm", Locale.GERMAN).format(Date(section.createdAt)),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// 6. CMS COMPONENT: CMS ADMIN VIEW
// ══════════════════════════════════════════════════════
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminCmsScreen(
    cmsSections: List<CmsSection>,
    onSaveSection: (Int, String, String, String, String, String, String) -> Unit,
    onDeleteSection: (Int) -> Unit
) {
    var editingId by remember { mutableStateOf(0) }
    var titleInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }
    var imageUrlInput by remember { mutableStateOf("") }
    var chosenColorHex by remember { mutableStateOf("#1D4ED8") }
    var chosenPhaseId by remember { mutableStateOf("ALL") }

    val presetColors = listOf(
        "#1D4ED8" to "Klassisch Blau",
        "#10B981" to "Präventiv Grün",
        "#F59E0B" to "Frühwarn Gelb",
        "#EF4444" to "Alarm Rot",
        "#8B5CF6" to "Post-Vagal Violett",
        "#06B6D4" to "Mental Cyan"
    )

    val listPhasesOptions = listOf("ALL", "WEISS", "GRUEN", "GELB", "ROT", "BLAU")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Content Management System (CMS)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hier können Sie Inhalte bearbeiten, neue Fachartikel schreiben, Bilder verknüpfen und Abschnitte den Phasen zuteilen. Änderungen sind sofort aktiv.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer, contentColor = MaterialTheme.colorScheme.primaryContainer),
                        onClick = {
                            // Seed stunning high fidelity medical / clinician examples
                            onSaveSection(0, "Sensomotorische Deeskalation", "Körperorientierte Regulation in der Akutpsychiatrie", "Wenn verbale Interventionen nicht mehr greifen, hilft Co-Regulation durch Mimik, Proxemik, Atemfrequenz und Tiefensensibilität. Der Körper dient als resonanzfähiges Beruhigungs-Medium.", "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?q=80&w=400", "#10B981", "GELB")
                            onSaveSection(0, "Therapeutischer Schutzraum", "Safewards-gestützte Architektur des Interaktionsfeldes", "Der visuelle Fluss auf einer Krisenstation entscheidet maßgeblich über das Erregungsniveau. Nutzen Sie reizarme Zonen (Snoezelen), reduzieren Sie metallische Klickgeräusche und arrangieren Sie Stühle im schrägen 30-Grad-Winkel statt in konfrontativer Face-to-Face Anordnung.", "https://images.unsplash.com/photo-1516549655169-df83a0774514?q=80&w=400", "#1D4ED8", "ALL")
                            onSaveSection(0, "Posttraumatisches Debriefing", "Schutz vor sekundärer Traumatisierung des Pflegeteams", "Ein Übergriff oder eine Zwangsmaßnahme hinterlassen auch beim Fachpersonal physiologische Stressspuren. Jedes Ereignis erfordert ein strukturiertes kollegiales Debriefing innerhalb von 24 Stunden, um emotionale Abwehrstrategien wie Zynismus oder Rückzug präventiv abzubauen.", "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?q=80&w=400", "#8B5CF6", "BLAU")
                        },
                        modifier = Modifier.fillMaxWidth().testTag("seed_demo_cms")
                    ) {
                        Text("Demo-Inhalte laden (mit echten Bildern)")
                    }
                }
            }
        }

        // Editor Form Section Card
        item {
            Card(
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (editingId > 0) "Sektion editieren (ID: $editingId)" else "Neue Sektion anlegen",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Titel*") },
                        modifier = Modifier.fillMaxWidth().testTag("cms_form_title"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("Kurzbeschreibung / Subtitel*") },
                        modifier = Modifier.fillMaxWidth().testTag("cms_form_desc"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = contentInput,
                        onValueChange = { contentInput = it },
                        label = { Text("Ausführlicher Fließtext*") },
                        modifier = Modifier.fillMaxWidth().testTag("cms_form_content"),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = imageUrlInput,
                        onValueChange = { imageUrlInput = it },
                        label = { Text("Bild URL (z.B. von Unsplash / Coil)") },
                        placeholder = { Text("https://...") },
                        modifier = Modifier.fillMaxWidth().testTag("cms_form_image"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Phase association selector
                    Text(
                        text = "Kategorie / Deeskalation Phase:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listPhasesOptions.forEach { phase ->
                            val isSel = chosenPhaseId == phase
                            FilterChip(
                                selected = isSel,
                                onClick = { chosenPhaseId = phase },
                                label = { Text(if (phase == "ALL") "Allgemein" else phase) },
                                modifier = Modifier.testTag("phase_chip_$phase")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Color Accent Picker
                    Text(
                        text = "Akzentfarbe für das Leitlinien-Infoblatt:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetColors.forEach { (colorHex, text) ->
                            val colorVal = Color(android.graphics.Color.parseColor(colorHex))
                            val isSel = chosenColorHex == colorHex
                            Box(
                                modifier = Modifier
                                    .size(width = 76.dp, height = 32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(colorVal)
                                    .border(
                                        width = if (isSel) 3.dp else 1.dp,
                                        color = if (isSel) MaterialTheme.colorScheme.outline else Color.Transparent,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable { chosenColorHex = colorHex }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = text.take(6),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Cancel button if editing
                        if (editingId > 0) {
                            TextButton(
                                onClick = {
                                    editingId = 0
                                    titleInput = ""
                                    descInput = ""
                                    contentInput = ""
                                    imageUrlInput = ""
                                    chosenColorHex = "#1D4ED8"
                                    chosenPhaseId = "ALL"
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Abbrechen")
                            }
                        }

                        Button(
                            enabled = titleInput.isNotEmpty() && descInput.isNotEmpty() && contentInput.isNotEmpty(),
                            onClick = {
                                onSaveSection(
                                    editingId,
                                    titleInput,
                                    descInput,
                                    contentInput,
                                    imageUrlInput,
                                    chosenColorHex,
                                    chosenPhaseId
                                )
                                // Reset form
                                editingId = 0
                                titleInput = ""
                                descInput = ""
                                contentInput = ""
                                imageUrlInput = ""
                                chosenColorHex = "#1D4ED8"
                                chosenPhaseId = "ALL"
                            },
                            modifier = Modifier.weight(2f).testTag("save_cms_section_btn")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (editingId > 0) "Update speichern" else "CMS Beitrag erstellen")
                        }
                    }
                }
            }
        }

        // List of current CMS items
        item {
            Text(
                text = "Bestehende CMS Abschnitte (${cmsSections.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (cmsSections.isEmpty()) {
            item {
                Text(
                    text = "Noch keine eigenen Einträge in der SQLite Datenbank vorhanden. Nutzen Sie das Formular oben, um neue Leitlinien zu publizieren.",
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(cmsSections) { section ->
                CmsSectionCard(
                    section = section,
                    isAdminView = true,
                    onEdit = {
                        editingId = section.id
                        titleInput = section.title
                        descInput = section.description
                        contentInput = section.contentText
                        imageUrlInput = section.imageUrl
                        chosenColorHex = section.accentColorHex
                        chosenPhaseId = section.phaseId
                    },
                    onDelete = {
                        onDeleteSection(section.id)
                    }
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════
// 7. ICD-11 WORKSPACE COMPONENT
// ══════════════════════════════════════════════════════
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IcdSymptomWorkspaceScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    icdDiagnoses: List<com.example.data.IcdDiagnosis>,
    searchResults: List<com.example.data.IcdSearchEntity>,
    searchInProgress: Boolean,
    searchError: String?,
    onSaveDiagnosis: (String, String, String, String, String, String, String, String, Boolean) -> Unit,
    onDeleteDiagnosis: (String) -> Unit,
    onImportIcdEntity: (com.example.data.IcdSearchEntity) -> Unit
) {
    var queryInput by remember { mutableStateOf(searchQuery) }
    
    // Sync external query state changes (like clicking the quick KJP loader buttons) to the internal text field edit state
    LaunchedEffect(searchQuery) {
        queryInput = searchQuery
    }
    
    var selectedDiagIdForEditing by remember { mutableStateOf<String?>(null) }
    var showCreateCustomDialog by remember { mutableStateOf(false) }

    // Form editing states
    var editName by remember { mutableStateOf("") }
    var editCode by remember { mutableStateOf("") }
    var editDynamik by remember { mutableStateOf("") }
    var editAbsicherung by remember { mutableStateOf("") }
    var editKlaerung by remember { mutableStateOf("") }
    var editAufloesung by remember { mutableStateOf("") }
    var editNotes by remember { mutableStateOf("") }
    var editIsCustom by remember { mutableStateOf(false) }

    // New custom diagnosis creation states
    var createId by remember { mutableStateOf("") }
    var createCode by remember { mutableStateOf("") }
    var createName by remember { mutableStateOf("") }
    var createDynamik by remember { mutableStateOf("") }
    var createAbsicherung by remember { mutableStateOf("") }
    var createKlaerung by remember { mutableStateOf("") }
    var createAufloesung by remember { mutableStateOf("") }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome and API Header
        item {
            Text(
                text = "ICD-11 Diagnose- & Symptom-Workspace",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Suchen und deeskalieren Sie Krankheitsbilder direkt über den offiziellen ICD-Katalog der Weltgesundheitsorganisation (WHO).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // API Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WHO Live REST Schnittstelle Aktiv",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Um die Live-Klassifikation abzurufen, tragen Sie Ihren client_id und client_secret im AI Studio Secrets-Panel unter ICD_CLIENT_ID und ICD_CLIENT_SECRET ein. Ist kein Key hinterlegt, greift die App vollautomatisch auf eine qualifizierte lokale psychiatrische Symptom-Datenbank zurück. Alle importierten Einträge lassen sich maßgeschneidert auf Ihre Stationsregeln editieren und in Krisenplänen verlinken.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live Search Input Box
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Symptome oder ICD-Schlüsselwörter suchen",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = queryInput,
                        onValueChange = { queryInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("icd_search_input"),
                        placeholder = { Text("z.B. Depression, Trauma, ADHS, Unruhe, Angst, Delir...") },
                        singleLine = true,
                        trailingIcon = {
                            if (queryInput.isNotEmpty()) {
                                IconButton(onClick = { 
                                    queryInput = ""
                                    onSearchQueryChange("")
                                }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Leeren")
                                }
                            }
                        }
                    )
                    Button(
                        onClick = { onSearchQueryChange(queryInput) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("icd_search_submit")
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Suchen", fontSize = 12.sp)
                    }
                }
            }
        }

        // Kinder- & Jugendpsychiatrie (KJP) API Schnelllade-Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("kjp_api_promo_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
                ),
                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Kinder- und Jugendpsychiatrie",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fachbereich: Kinder- & Jugendpsychiatrie",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Laden Sie deeskalationsrelevante ICD-11 Krankheitsbilder wie ADHS, ASS, PTBS & Störungen des Sozialverhaltens direkt über die WHO-Schnittstelle herunter.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    queryInput = "Kinder und Jugendpsychiatrie"
                                    onSearchQueryChange("Kinder und Jugendpsychiatrie")
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("kjp_api_download_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("API-Inhalte laden", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            FilledTonalButton(
                                onClick = {
                                    queryInput = "6A05"
                                    onSearchQueryChange("6A05")
                                },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_quick_adhs")
                            ) {
                                Text("6A05 ADHS", fontSize = 10.sp)
                            }

                            FilledTonalButton(
                                onClick = {
                                    queryInput = "6A02"
                                    onSearchQueryChange("6A02")
                                },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_quick_ass")
                            ) {
                                Text("6A02 ASS", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Live Search Results
        if (searchInProgress) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Frage WHO ICD-11 API Register ab...", fontSize = 12.sp, fontStyle = FontStyle.Italic)
                }
            }
        }

        if (searchError != null) {
            item {
                Text(
                    text = searchError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        if (searchResults.isNotEmpty()) {
            item {
                Text(
                    text = "Gefundene WHO ICD-11 Register-Einträge (${searchResults.size}):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            items(searchResults) { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = result.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "ICD Code: ${result.theCode ?: "N/A"} | ID: ${result.id}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onImportIcdEntity(result)
                                    android.widget.Toast.makeText(context, "${result.title} erfolgreich importiert!", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("import_${result.id.lowercase()}")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Importieren", fontSize = 10.sp)
                            }
                        }
                        if (result.definition != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.definition,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f)
                            )
                        }
                    }
                }
            }
        }

        // Active treating profiles workspace
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aktive Behandlungsleitlinien auf Station (${icdDiagnoses.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick = { showCreateCustomDialog = !showCreateCustomDialog },
                    modifier = Modifier.testTag("btn_show_custom_diag")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eigene anlegen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Custom manual creation drawer card
        if (showCreateCustomDialog) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("custom_diag_form_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Eigene Behandlungsleitlinie erstellen", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        
                        OutlinedTextField(
                            value = createId,
                            onValueChange = { createId = it },
                            label = { Text("ID/Kürzel (z.B. DELIR)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = createCode,
                            onValueChange = { createCode = it },
                            label = { Text("ICD-Code (Optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = createName,
                            onValueChange = { createName = it },
                            label = { Text("Voller klinischer Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = createDynamik,
                            onValueChange = { createDynamik = it },
                            label = { Text("Klinische Eskalations-Dynamik") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = createAbsicherung,
                            onValueChange = { createAbsicherung = it },
                            label = { Text("Säule 1: Absicherung (Gelbe/Rote Phase)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = createKlaerung,
                            onValueChange = { createKlaerung = it },
                            label = { Text("Säule 2: Klärung (Gelbe/Grüne Phase)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = createAufloesung,
                            onValueChange = { createAufloesung = it },
                            label = { Text("Säule 3: Auflösung (Blaue Phase)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            TextButton(onClick = { showCreateCustomDialog = false }) {
                                Text("Abbrechen")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (createId.trim().isNotEmpty() && createName.trim().isNotEmpty()) {
                                        onSaveDiagnosis(
                                            createId,
                                            createCode,
                                            createName,
                                            createDynamik,
                                            createAbsicherung,
                                            createKlaerung,
                                            createAufloesung,
                                            "Selbst angelegte Leitlinie.",
                                            true
                                        )
                                        createId = ""
                                        createCode = ""
                                        createName = ""
                                        createDynamik = ""
                                        createAbsicherung = ""
                                        createKlaerung = ""
                                        createAufloesung = ""
                                        showCreateCustomDialog = false
                                        android.widget.Toast.makeText(context, "$createName gespeichert!", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.testTag("save_custom_diag")
                            ) {
                                Text("Erstellen & Speichern", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        if (icdDiagnoses.isEmpty()) {
            item {
                Text(
                    text = "Keine Behandlungsleitlinien im Katalog. Suchen Sie oben nach Symptomen aus dem ICD-11 oder legen Sie eigene an.",
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Edit profile dialog/form card inline below
            items(icdDiagnoses) { diag ->
                val isEditingThis = selectedDiagIdForEditing == diag.codeOrId

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEditingThis) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.2.dp, if (isEditingThis) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = diag.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (diag.isCustom) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text("EIGENE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    }
                                }
                                Text(
                                    text = "System-ID/Kürzel: ${diag.codeOrId} ${if (diag.code.isNotEmpty()) "| ICD-Code: " + diag.code else ""}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        if (isEditingThis) {
                                            selectedDiagIdForEditing = null
                                        } else {
                                            selectedDiagIdForEditing = diag.codeOrId
                                            editName = diag.name
                                            editCode = diag.code
                                            editDynamik = diag.dynamik
                                            editAbsicherung = diag.absicherung
                                            editKlaerung = diag.klaerung
                                            editAufloesung = diag.aufloesung
                                            editNotes = diag.customNotes
                                            editIsCustom = diag.isCustom
                                        }
                                    },
                                    modifier = Modifier.testTag("edit_trigger_${diag.codeOrId.lowercase()}")
                                ) {
                                    Icon(
                                        imageVector = if (isEditingThis) Icons.Default.Close else Icons.Default.Edit,
                                        contentDescription = "Editieren",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onDeleteDiagnosis(diag.codeOrId)
                                        android.widget.Toast.makeText(context, "${diag.name} gelöscht.", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("delete_trigger_${diag.codeOrId.lowercase()}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Entfernen",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        if (isEditingThis) {
                            // Render Editing Fields
                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Leitlinie Individualisieren & Anpassen", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)

                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = { Text("Name der Diagnose / des Symptoms") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("edit_input_name")
                            )

                            OutlinedTextField(
                                value = editCode,
                                onValueChange = { editCode = it },
                                label = { Text("ICD-11 Klassifikationscode (z.B. 6A05)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("edit_input_code")
                            )

                            OutlinedTextField(
                                value = editDynamik,
                                onValueChange = { editDynamik = it },
                                label = { Text("Typische Eskalations-Dynamiken") },
                                maxLines = 5,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("edit_input_dynamik")
                            )

                            OutlinedTextField(
                                value = editAbsicherung,
                                onValueChange = { editAbsicherung = it },
                                label = { Text("1. Säule - Absicherung (Reizminderung & Schutz)") },
                                maxLines = 5,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("edit_input_absicherung")
                            )

                            OutlinedTextField(
                                value = editKlaerung,
                                onValueChange = { editKlaerung = it },
                                label = { Text("2. Säule - Deeskalative Interaktion & Klärung") },
                                maxLines = 5,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("edit_input_klaerung")
                            )

                            OutlinedTextField(
                                value = editAufloesung,
                                onValueChange = { editAufloesung = it },
                                label = { Text("3. Säule - Wiederaufbau & Auflösung") },
                                maxLines = 5,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("edit_input_aufloesung")
                            )

                            OutlinedTextField(
                                value = editNotes,
                                onValueChange = { editNotes = it },
                                label = { Text("Individuelle Stationsnotizen / Patientenbesonderheiten") },
                                placeholder = { Text("z.B. 'Achtung: Luca reagiert auf laute Stimmen paradox mit Aggression. Unbedingt flüstern.'") },
                                maxLines = 5,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("edit_input_notes")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { selectedDiagIdForEditing = null }) {
                                    Text("Abbrechen")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        onSaveDiagnosis(
                                            diag.codeOrId,
                                            editCode,
                                            editName,
                                            editDynamik,
                                            editAbsicherung,
                                            editKlaerung,
                                            editAufloesung,
                                            editNotes,
                                            editIsCustom
                                        )
                                        selectedDiagIdForEditing = null
                                        android.widget.Toast.makeText(context, "Änderungen an ${editName} gespeichert!", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("save_edit_submit")
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Speichern", fontSize = 11.sp)
                                }
                            }

                        } else {
                            // Normal details view
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Klinische Dynamik & Symptome:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = diag.dynamik,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                ActionPillarCard(
                                    title = "SÄULE 1: ABSICHERUNG (CHRONISCH / JETZT)",
                                    content = diag.absicherung,
                                    bg = Color(0xFFFFFBEB),
                                    tc = Color(0xFFB45309),
                                    border = Color(0xFFFCD34D),
                                    icon = Icons.Default.Warning
                                )
                                ActionPillarCard(
                                    title = "SÄULE 2: KLÄRUNG (INTERAKTIV / DIAGNOSTISCH)",
                                    content = diag.klaerung,
                                    bg = Color(0xFFEFF6FF),
                                    tc = Color(0xFF1E40AF),
                                    border = Color(0xFF93C5FD),
                                    icon = Icons.Default.Info
                                )
                                ActionPillarCard(
                                    title = "SÄULE 3: AUFLÖSUNG (NACHBEREITUNG & EMPOWERMENT)",
                                    content = diag.aufloesung,
                                    bg = Color(0xFFF0FDF4),
                                    tc = Color(0xFF15803D),
                                    border = Color(0xFF86EFAC),
                                    icon = Icons.Default.Check
                                )
                            }

                            if (diag.customNotes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFFAF0), RoundedCornerShape(6.dp))
                                        .border(BorderStroke(1.dp, Color(0xFFFEE2E2).copy(alpha = 0.5f)), RoundedCornerShape(6.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "✍️ INDIVIDUELLE STATIONSNOTIZEN / TRADITIONS-LOGS:",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF9A3412)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = diag.customNotes,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp,
                                            color = Color(0xFF7C2D12)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
