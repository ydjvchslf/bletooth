package com.example.bledot.activity.before

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BeforeViewModel: ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading : StateFlow<Boolean>
        get() = _isLoading

    init {
        viewModelScope.launch {
            delay(1000)
            _isLoading.value = false
        }
    }
}