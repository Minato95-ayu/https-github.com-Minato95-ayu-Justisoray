package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases ORDER BY createdAt DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE id = :id")
    suspend fun getCaseById(id: Int): CaseEntity?

    @Query("SELECT * FROM timeline_items WHERE caseId = :caseId ORDER BY id ASC")
    fun getTimelineByCaseId(caseId: Int): Flow<List<TimelineItemEntity>>

    @Query("SELECT * FROM evidence_items WHERE caseId = :caseId")
    fun getEvidenceByCaseId(caseId: Int): Flow<List<EvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(case: CaseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimelineItem(item: TimelineItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidenceItem(item: EvidenceEntity)

    @Update
    suspend fun updateCase(case: CaseEntity)

    @Update
    suspend fun updateTimelineItem(item: TimelineItemEntity)

    @Update
    suspend fun updateEvidenceItem(item: EvidenceEntity)

    @Query("DELETE FROM cases WHERE id = :id")
    suspend fun deleteCase(id: Int)

    @Query("DELETE FROM timeline_items WHERE caseId = :caseId")
    suspend fun deleteTimelinesByCaseId(caseId: Int)

    @Query("DELETE FROM evidence_items WHERE caseId = :caseId")
    suspend fun deleteEvidenceByCaseId(caseId: Int)
}
