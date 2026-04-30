package com.ggs.parkuzpp.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class ParkSpot(
    @DocumentId
    val id: String = "", // Firebase automatycznie wstrzyknie tu ID dokumentu
    val active: Boolean = true,
    val coordinates: Coordinates = Coordinates(),
    val label: String = "",
    val photos: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(), // Do wyświetlania daty

    @get:PropertyName("user-id")
    @set:PropertyName("user-id")
    var userId: String = ""
)

data class Coordinates(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)