package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crisis_plans")
data class CrisisPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientInitials: String,
    val mainDiagnosis: String,
    val individualTrigger: String,
    val earlyWarningSigns: String,
    val preferredCalming: String,
    val whatVerschlimmert: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "incident_reviews")
data class IncidentReview(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientInitials: String,
    val incidentDate: String,
    val description: String,
    val triggerSource: String,
    val teamStrengths: String,
    val lessonsLearned: String,
    val teamWellbeing: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cms_sections")
data class CmsSection(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val contentText: String,
    val imageUrl: String = "",
    val accentColorHex: String = "#1D4ED8",
    val phaseId: String = "ALL", // Connects with one of the 5 phases or "ALL"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "team_learnings")
data class TeamLearning(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val situation: String,
    val whatWorked: String,
    val submittedByRole: String, // e.g., "Pflege", "Arzt", "Therapeut"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "icd_diagnoses")
data class IcdDiagnosis(
    @PrimaryKey val codeOrId: String, // e.g., "6D70" or "ADHS"
    val code: String,                // e.g., "6D70.1" or "F90.0"
    val name: String,                // e.g., "ADHS (Aufmerk..."
    val dynamik: String,             // Escalation dynamics
    val absicherung: String,         // Pillar 1 deescalation
    val klaerung: String,            // Pillar 2 deescalation
    val aufloesung: String,          // Pillar 3 deescalation
    val customNotes: String = "",    // Individual/user customized notes
    val isCustom: Boolean = false,   // True if custom-created, false if standard or live-API imported
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "handover_reports")
data class HandoverReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val phase: String,
    val diagnosis: String,
    val strategies: String,
    val result: String, // Max. 200 Zeichen
    val outcome: String, // Ja / Nein / OA informiert
    val roomNumber: String = "" // Optional, no names or birthdates to comply with DSGVO
)

@Entity(tableName = "strategy_ratings")
data class StrategyRating(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val strategyName: String,
    val isHelpful: Boolean,
    val stationId: String = "Allgemein",
    val timestamp: Long = System.currentTimeMillis()
)


