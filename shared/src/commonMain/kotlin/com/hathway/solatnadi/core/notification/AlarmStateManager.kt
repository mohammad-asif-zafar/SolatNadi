package com.hathway.solatnadi.core.notification

import com.hathway.solatnadi.presentation.components.ActiveAlarmData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AlarmStateManager {
    private val _activeAlarm = MutableStateFlow<ActiveAlarmData?>(null)
    val activeAlarm: StateFlow<ActiveAlarmData?> = _activeAlarm.asStateFlow()

    fun triggerAlarm(data: ActiveAlarmData) {
        _activeAlarm.value = data
    }

    fun clearActiveAlarm() {
        _activeAlarm.value = null
    }
}
