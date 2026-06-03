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

