package com.secureqr.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secureqr.domain.model.QrRiskLevel
import com.secureqr.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: ScanRepository
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanUiState>(ScanUiState.Scanning)
    val scanState: StateFlow<ScanUiState> = _scanState.asStateFlow()


    fun onQrCodeDetected(url: String) {
        if (_scanState.value is ScanUiState.Scanning) {
            _scanState.value = ScanUiState.Loading(url)
            analyzeUrl(url)
        }
    }

    private fun analyzeUrl(url: String) {
        viewModelScope.launch {
            val risk = repository.validateUrl(url)
            _scanState.value = ScanUiState.Result(url, risk)
        }
    }

    fun resetScan() {
        _scanState.value = ScanUiState.Scanning
    }
}

sealed class ScanUiState {
    object Scanning : ScanUiState()
    data class Loading(val url: String) : ScanUiState()
    data class Result(val url: String, val risk: QrRiskLevel) : ScanUiState()
}