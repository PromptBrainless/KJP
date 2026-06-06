package com.example.data

import kotlinx.coroutines.flow.Flow

class DeeskalationRepository(private val dao: DeeskalationDao) {
    val allCrisisPlans: Flow<List<CrisisPlan>> = dao.getAllCrisisPlans()
    val allIncidentReviews: Flow<List<IncidentReview>> = dao.getAllIncidentReviews()
    val allCmsSections: Flow<List<CmsSection>> = dao.getAllCmsSections()
    val allTeamLearnings: Flow<List<TeamLearning>> = dao.getAllTeamLearnings()
    val allIcdDiagnoses: Flow<List<IcdDiagnosis>> = dao.getAllIcdDiagnoses()

    suspend fun getIcdDiagnosisById(codeOrId: String): IcdDiagnosis? {
        return dao.getIcdDiagnosisById(codeOrId)
    }

    suspend fun insertIcdDiagnosis(diagnosis: IcdDiagnosis) {
        dao.insertIcdDiagnosis(diagnosis)
    }

    suspend fun deleteIcdDiagnosisById(codeOrId: String) {
        dao.deleteIcdDiagnosisById(codeOrId)
    }

    suspend fun insertCrisisPlan(plan: CrisisPlan) {
        dao.insertCrisisPlan(plan)
    }

    suspend fun deleteCrisisPlanById(id: Int) {
        dao.deleteCrisisPlanById(id)
    }

    suspend fun insertIncidentReview(review: IncidentReview) {
        dao.insertIncidentReview(review)
    }

    suspend fun deleteIncidentReviewById(id: Int) {
        dao.deleteIncidentReviewById(id)
    }

    suspend fun insertCmsSection(section: CmsSection) {
        dao.insertCmsSection(section)
    }

    suspend fun deleteCmsSectionById(id: Int) {
        dao.deleteCmsSectionById(id)
    }

    suspend fun insertTeamLearning(learning: TeamLearning) {
        dao.insertTeamLearning(learning)
    }

    suspend fun deleteTeamLearningById(id: Int) {
        dao.deleteTeamLearningById(id)
    }

    val allHandoverReports: Flow<List<HandoverReport>> = dao.getAllHandoverReports()
    val allStrategyRatings: Flow<List<StrategyRating>> = dao.getAllStrategyRatings()

    suspend fun insertHandoverReport(report: HandoverReport) {
        dao.insertHandoverReport(report)
    }

    suspend fun deleteHandoverReportById(id: Int) {
        dao.deleteHandoverReportById(id)
    }

    suspend fun insertStrategyRating(rating: StrategyRating) {
        dao.insertStrategyRating(rating)
    }
}

