package com.aistrategist.app.presentation.audit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistrategist.app.data.repository.AppUsageRepository
import com.aistrategist.app.domain.model.AppUsageData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuditViewModel @Inject constructor(
    private val appUsageRepository: AppUsageRepository
) : ViewModel() {

    private val _usageData = MutableStateFlow<List<AppUsageData>?>(null)
    val usageData = _usageData.asStateFlow()

    private val _energyRoiData = MutableStateFlow<FloatArray?>(null)
    val energyRoiData = _energyRoiData.asStateFlow()

    fun loadUsageData(daysBack: Int = 7) {
        viewModelScope.launch {
            _usageData.value = appUsageRepository.getTopAppUsages(daysBack)
            _energyRoiData.value = appUsageRepository.get24HourEnergyROI()
        }
    }
}
