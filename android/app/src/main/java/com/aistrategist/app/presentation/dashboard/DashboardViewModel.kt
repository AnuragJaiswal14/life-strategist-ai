package com.aistrategist.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistrategist.app.domain.model.DailyLog
import com.aistrategist.app.domain.usecase.GetLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.aistrategist.app.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getLogsUseCase: GetLogsUseCase,
    val authRepository: AuthRepository
) : ViewModel() {

    val currentUser = authRepository.getCurrentUser()

    val logs = getLogsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}
