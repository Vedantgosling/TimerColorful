package com.example.timer1.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel:ViewModel() {
    private val _timer = MutableStateFlow(0L)
    val timer = _timer.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> =_isLoading

    private var timerJob: Job? = null

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {

            while (true) {
                _isLoading.value = true
                delay(1000)
                _timer.value++
            }
        }
    }

    fun pauseTimer() {
        _isLoading.value = false
        timerJob?.cancel()

    }

    fun stopTimer() {
        _timer.value = 0
        timerJob?.cancel()
        _isLoading.value = false

    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        _isLoading.value = false
    }
}