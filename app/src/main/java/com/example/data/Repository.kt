package com.example.data

import kotlinx.coroutines.flow.Flow

class DeeskalationRepository(private val dao: DeeskalationDao) {
    val allCrisisPlans: Flow<List<CrisisPlan>> = dao.getAllCrisisPlans()
    val allIncidentReviews: Flow<List<IncidentReview>> = dao.getAllIncidentReviews()
    val allCmsSections: Flow<List<CmsSection>> = dao.getAllCmsSections()
    val allTeamLearnings: Flow<List<TeamLearning>> = dao.getAllTeamLearnings()

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
}
