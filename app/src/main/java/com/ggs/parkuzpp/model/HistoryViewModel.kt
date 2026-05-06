package com.ggs.parkuzpp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryViewModel : ViewModel() {

    private val repository = ParkingRepository()

    private val _historyItems = MutableStateFlow<List<ParkSpot>>(emptyList())
    val historyItems: StateFlow<List<ParkSpot>> = _historyItems.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        fetchHistory()
    }

    private fun fetchHistory() {
        viewModelScope.launch {
            repository.getParkingHistory().collect { spots ->
                _historyItems.value = spots.sortedByDescending { it.timestamp }
            }
        }
    }

    /**
     * NOWOŚĆ: Aktywuje wybrany punkt z historii i przenosi użytkownika na mapę.
     */
    fun activateAndNavigateToMap(documentId: String, onNavigate: () -> Unit) {
        viewModelScope.launch {
            val result = repository.activateSpot(documentId)
            if (result.isSuccess) {
                onNavigate()
            } else {
                println("🔥 Błąd aktywacji punktu: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun deleteItem(documentId: String) {
        viewModelScope.launch {
            repository.deleteParkingSpot(documentId)
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchHistory()
            delay(1000)
            _isRefreshing.value = false
        }
    }
}