package com.ggs.parkuzpp.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.ggs.parkuzpp.R

/**
 * Repository responsible for managing parking data within Firestore.
 * Implements the "Single Active Spot" policy, ensuring only one parking spot is active at a time.
 */
class ParkingRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val spotsCollection = firestore.collection(COLLECTION_PARK_SPOTS)

    private val currentUid: String?
        get() = auth.currentUser?.uid

    /**
     * Provides a stream of the user's currently active parking spot.
     * Yields null if no active spot exists or if the user is unauthenticated.
     *
     * @return A [Flow] emitting the single active [ParkSpot] or null.
     */
    fun getActiveSpotFlow(): Flow<ParkSpot?> = callbackFlow {
        val uid = currentUid ?: run {
            trySend(null)
            return@callbackFlow
        }

        val registration = spotsCollection
            .whereEqualTo(FIELD_USER_ID, uid)
            .whereEqualTo(FIELD_ACTIVE, true)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val spot = snapshot?.documents?.firstOrNull()?.let { doc ->
                    doc.toObject(ParkSpot::class.java)?.copy(id = doc.id)
                }
                trySend(spot)
            }

        awaitClose { registration.remove() }
    }

    /**
     * Saves a new parking spot and automatically deactivates all previously active spots.
     *
     * @param spot The [ParkSpot] to be saved.
     * @return A [Result] indicating success or containing an exception on failure.
     */
    suspend fun saveParkingSpot(spot: ParkSpot): Result<Unit> {
        val uid = currentUid ?: return Result.failure(Exception(R.string.error_no_auth.toString()))

        return try {
            deactivatePreviousSpots().getOrThrow()

            val spotToSave = spot.copy(userId = uid, active = true)

            spotsCollection.add(spotToSave).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Activates a specific parking spot from the user's history.
     *
     * @param documentId The Firestore document ID of the parking spot to activate.
     * @return A [Result] indicating success or containing an exception on failure.
     */
    suspend fun activateSpot(documentId: String): Result<Unit> {
        return try {
            deactivatePreviousSpots().getOrThrow()
            spotsCollection.document(documentId).update(FIELD_ACTIVE, true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deactivates all currently active parking spots for the authenticated user.
     *
     * @return A [Result] indicating success or containing an exception on failure.
     */
    suspend fun deactivatePreviousSpots(): Result<Unit> {
        val uid = currentUid ?: return Result.failure(Exception(R.string.error_no_user.toString()))

        return try {
            val activeSpots = spotsCollection
                .whereEqualTo(FIELD_USER_ID, uid)
                .whereEqualTo(FIELD_ACTIVE, true)
                .get()
                .await()

            if (!activeSpots.isEmpty) {
                firestore.runBatch { batch ->
                    for (doc in activeSpots.documents) {
                        batch.update(doc.reference, FIELD_ACTIVE, false)
                    }
                }.await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves the complete parking history for the authenticated user.
     * Maps document IDs to enable subsequent updates or deletions.
     *
     * @return A [Flow] emitting a list of [ParkSpot] items.
     */
    fun getParkingHistory(): Flow<List<ParkSpot>> = callbackFlow {
        val uid = currentUid ?: run {
            trySend(emptyList())
            return@callbackFlow
        }

        val subscription = spotsCollection
            .whereEqualTo(FIELD_USER_ID, uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val spots = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ParkSpot::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(spots)
            }

        awaitClose { subscription.remove() }
    }

    /**
     * Deletes a specific parking spot from Firestore.
     *
     * @param documentId The Firestore document ID of the parking spot to delete.
     * @return A [Result] indicating success or containing an exception on failure.
     */
    suspend fun deleteParkingSpot(documentId: String): Result<Unit> {
        return try {
            spotsCollection.document(documentId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val COLLECTION_PARK_SPOTS = "park-spots"
        private const val FIELD_USER_ID = "user-id"
        private const val FIELD_ACTIVE = "active"
    }
}