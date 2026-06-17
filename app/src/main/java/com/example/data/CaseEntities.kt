package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val summary: String,
    val environment: String, // e.g., "POLICE_STATION", "COURTROOM", "CYBER_CRIME"
    val status: String,     // e.g., "UNDER_INVESTIGATION", "PROSECUTION", "RESOLVED"
    val confidence: Int,    // 0-100 representation of case conviction readiness
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "timeline_items")
data class TimelineItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseId: Int,
    val type: String,        // "EVENT" (Past), "RIGHT" (Unlock), "EVIDENCE" (Collect), "PROCEDURE" (Present), "ACTION" (Future)
    val title: String,
    val description: String,
    val relativeTime: String, // e.g., "Day 1 - Occurrence", "Day 3 - Legal notice", "Day 15 - Complaint"
    val statusText: String,   // Status indicator: "UNLOCKED", "PENDING", "COMPLETED"
    val lawSource: String? = null // Specific legal citation: e.g., "Section 154 CrPC"
)

@Entity(tableName = "evidence_items")
data class EvidenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caseId: Int,
    val name: String,
    val description: String,
    val category: String,       // "Primary", "Secondary", "Corroborative"
    val classification: String, // "Documentary", "Digital", "Witness Statement", "Physical"
    val admissibility: String,   // e.g., "Section 65B Electronics Certificate Required"
    val statusText: String,     // "FOUND", "MISSING", "SUSPECTED"
    val assignedNodeX: Float = 100f, // For visual graph interface positioning
    val assignedNodeY: Float = 120f
)
