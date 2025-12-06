package com.secureqr.data.local

import androidx.room.TypeConverter
import com.secureqr.domain.model.QrRiskLevel

class Converters {
    @TypeConverter
    fun fromRiskLevel(value: QrRiskLevel): String {
        return value.name
    }

    @TypeConverter
    fun toRiskLevel(value: String): QrRiskLevel {
        return try {
            QrRiskLevel.valueOf(value)
        } catch (e: IllegalArgumentException) {
            QrRiskLevel.DESCONOCIDO
        }
    }
}