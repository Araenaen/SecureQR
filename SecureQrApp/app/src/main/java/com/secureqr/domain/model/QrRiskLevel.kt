package com.secureqr.domain.model

import androidx.compose.ui.graphics.Color

enum class QrRiskLevel {
    SEGURO,
    MALICIOSO,
    DESCONOCIDO;

    fun toColor(): Color {
        return when (this) {
            SEGURO -> Color(0xFF4CAF50)
            MALICIOSO -> Color(0xFFB00020)
            DESCONOCIDO -> Color(0xFF9E9E9E)
        }
    }
}