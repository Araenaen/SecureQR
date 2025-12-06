package com.secureqr.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.secureqr.domain.model.QrRiskLevel

@Entity(tableName = "scan_history")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val urlHash: String,
    val riskLevel: QrRiskLevel,
    val scanTimestamp: Long,
    val lastValidationTimestamp: Long? = null
)