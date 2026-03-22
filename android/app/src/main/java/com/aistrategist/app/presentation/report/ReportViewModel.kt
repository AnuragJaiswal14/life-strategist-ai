package com.aistrategist.app.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistrategist.app.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    private val _reportData = MutableStateFlow<String>("Loading AI report...")
    val reportData = _reportData.asStateFlow()
    
    init {
        generateReport()
    }
    
    private fun generateReport() {
        viewModelScope.launch {
            try {
                val response = apiService.generateWeeklyReport()
                _reportData.value = response.toString() 
            } catch (e: Exception) {
                _reportData.value = "Failed to load report. Ensure backend is running.\n\nError: ${e.message}"
            }
        }
    }
}
