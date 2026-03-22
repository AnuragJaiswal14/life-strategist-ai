package com.aistrategist.app.presentation.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistrategist.app.domain.model.DailyLog
import com.aistrategist.app.domain.usecase.SubmitLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val submitLogUseCase: SubmitLogUseCase
) : ViewModel() {
    
    private val _energyLevel = MutableStateFlow(5f)
    val energyLevel = _energyLevel.asStateFlow()
    
    fun setEnergyLevel(level: Float) { _energyLevel.value = level }
    
    fun submitLog(habits: String, timeSpent: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val log = DailyLog(
                date = LocalDate.now().toString(),
                energyLevel = _energyLevel.value.toInt(),
                timeSpentJson = timeSpent,
                habitsJson = habits
            )
            submitLogUseCase(log)
            onComplete()
        }
    }
}
