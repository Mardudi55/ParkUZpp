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

/**
 * ViewModel responsible for managing the state and business logic of the parking history screen.
 */
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
     * Activates a specific parking spot from the user's history and triggers navigation to the map.
     *
     * @param documentId The Firestore document ID of the parking spot to activate.
     * @param onNavigate Callback executed upon successful activation to handle navigation.
     */
    fun activateAndNavigateToMap(documentId: String, onNavigate: () -> Unit) {
        viewModelScope.launch {
            val result = repository.activateSpot(documentId)
            if (result.isSuccess) {
                onNavigate()
            } else {
                println("🔥 Error activating spot: ${result.exceptionOrNull()?.message}")
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