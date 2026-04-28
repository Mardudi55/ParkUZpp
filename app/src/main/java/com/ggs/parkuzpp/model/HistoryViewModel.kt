package com.ggs.parkuzpp.model


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                // Sortowanie lokalne (od najnowszych), jeśli nie używamy orderBy w Firestore
                _historyItems.value = spots.sortedByDescending { it.timestamp }
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

            kotlinx.coroutines.delay(1000)
            _isRefreshing.value = false
        }
    }

}