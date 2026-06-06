package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeeskalationDao {
    @Query("SELECT * FROM crisis_plans ORDER BY createdAt DESC")
    fun getAllCrisisPlans(): Flow<List<CrisisPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrisisPlan(plan: CrisisPlan)

    @Query("DELETE FROM crisis_plans WHERE id = :id")
    suspend fun deleteCrisisPlanById(id: Int)

    @Query("SELECT * FROM incident_reviews ORDER BY createdAt DESC")
    fun getAllIncidentReviews(): Flow<List<IncidentReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncidentReview(review: IncidentReview)

    @Query("DELETE FROM incident_reviews WHERE id = :id")
    suspend fun deleteIncidentReviewById(id: Int)

    // CMOS Content Management System APIs
    @Query("SELECT * FROM cms_sections ORDER BY createdAt DESC")
    fun getAllCmsSections(): Flow<List<CmsSection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCmsSection(section: CmsSection)

    @Query("DELETE FROM cms_sections WHERE id = :id")
    suspend fun deleteCmsSectionById(id: Int)

    // Team Learnings ("Was hat funktioniert?") APIs
    @Query("SELECT * FROM team_learnings ORDER BY createdAt DESC")
    fun getAllTeamLearnings(): Flow<List<TeamLearning>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamLearning(learning: TeamLearning)

    @Query("DELETE FROM team_learnings WHERE id = :id")
    suspend fun deleteTeamLearningById(id: Int)

    // ICD Diagnosis / Symptom Customization APIs
    @Query("SELECT * FROM icd_diagnoses ORDER BY createdAt DESC")
    fun getAllIcdDiagnoses(): Flow<List<IcdDiagnosis>>

    @Query("SELECT * FROM icd_diagnoses WHERE codeOrId = :codeOrId LIMIT 1")
    suspend fun getIcdDiagnosisById(codeOrId: String): IcdDiagnosis?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIcdDiagnosis(diagnosis: IcdDiagnosis)

    @Query("DELETE FROM icd_diagnoses WHERE codeOrId = :codeOrId")
    suspend fun deleteIcdDiagnosisById(codeOrId: String)
}
