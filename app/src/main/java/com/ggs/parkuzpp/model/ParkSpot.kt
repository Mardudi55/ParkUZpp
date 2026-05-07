package com.ggs.parkuzpp.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

/**
 * Represents a parking spot entity stored in Firestore.
 *
 * @property id The unique document ID automatically injected by Firestore.
 * @property active Indicates whether the parking spot is currently active.
 * @property coordinates The geographical location of the parking spot.
 * @property label A descriptive name or label for the parking spot.
 * @property photos A list of URLs or paths pointing to images of the spot.
 * @property timestamp The creation or modification time in milliseconds, used for displaying dates.
 * @property userId The ID of the user who created or reported this parking spot.
 */
data class ParkSpot(
    @DocumentId
    val id: String = "",
    val active: Boolean = true,
    val coordinates: Coordinates = Coordinates(),
    val label: String = "",
    val photos: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),

    @get:PropertyName("user-id")
    @set:PropertyName("user-id")
    var userId: String = ""
)

/**
 * Represents geographical coordinates for a parking spot.
 *
 * @property lat The latitude value.
 * @property lng The longitude value.
 */
data class Coordinates(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)