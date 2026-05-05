package com.ggs.parkuzpp.location

import android.location.Location
import java.math.RoundingMode

/**
 * Utility object for handling operations shared by GPS Services.
 */
object GPSUtils {
    /**
     * Formats the geographic coordinates of a given [Location] to 5 decimal places.
     * This provides accuracy up to roughly 1.1 meters while preventing unnecessary precision floating-point issues.
     *
     * @param location The raw [Location] object.
     * @return The [Location] object with rounded latitude and longitude, or null if the input was null.
     */
    @JvmStatic
    fun formatLocation(location: Location?): Location? {
        return if (location == null) {
            null
        } else {
            val decimals = 5
            location.latitude = location.latitude.toBigDecimal().setScale(decimals, RoundingMode.HALF_EVEN).toDouble()
            location.longitude = location.longitude.toBigDecimal().setScale(decimals, RoundingMode.HALF_EVEN).toDouble()
            location
        }
    }
}