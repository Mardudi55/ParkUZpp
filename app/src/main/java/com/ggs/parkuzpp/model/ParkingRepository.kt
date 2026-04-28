package com.ggs.parkuzpp.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository responsible for managing parking spot data in Firebase Firestore.
 * It handles saving new spots and providing access to the "park-spots" collection.
 */
class ParkingRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val spotsCollection = firestore.collection("park-spots")

    /**
     * Saves a [ParkSpot] to Firestore.
     * If the [ParkSpot.userId] is empty, it automatically populates it
     * with the current authenticated user's ID.
     *
     * @param spot The parking spot data to save.
     * @return Result indicating success or the caught exception.
     */
    suspend fun saveParkingSpot(spot: ParkSpot): Result<Unit> {
        return try {
            // Automatyczne przypisanie ID użytkownika, jeśli nie zostało podane
            val spotToSave = if (spot.userId.isEmpty()) {
                spot.copy(userId = auth.currentUser?.uid ?: "")
            } else {
                spot
            }

            // Sprawdzenie czy mamy ID użytkownika (wymagane w Twojej strukturze)
            if (spotToSave.userId.isEmpty()) {
                return Result.failure(Exception("User not authenticated"))
            }

            // Zapis do Firestore (używamy .add() dla automatycznego ID dokumentu)
            spotsCollection.add(spotToSave).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Marks all previous parking spots as inactive for the current user.
     * Useful when starting a new parking session to ensure only one "active" spot exists.
     */
    suspend fun deactivatePreviousSpots(): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No user"))

        return try {
            val activeSpots = spotsCollection
                .whereEqualTo("user-id", uid)
                .whereEqualTo("active", true)
                .get()
                .await()

            firestore.runBatch { batch ->
                for (doc in activeSpots.documents) {
                    batch.update(doc.reference, "active", false)
                }
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getParkingHistory(): Flow<List<ParkSpot>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val subscription = spotsCollection
            .whereEqualTo("user-id", uid)
            // Jeśli chcesz sortować po dacie, odznacz poniższą linię (wymaga indeksu w Firebase)
            // .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("🔥 BŁĄD FIREBASE (Historia): ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    try {
                        val spots = snapshot.toObjects(ParkSpot::class.java)
                        println("🔥 Pobrano dokumentów: ${spots.size}")
                        trySend(spots)
                    } catch (e: Exception) {
                        println("🔥 BŁĄD MAPOWANIA DANYCH: ${e.message}")
                    }
                }
            }

        awaitClose { subscription.remove() }
    }

    /**
     * Usuwa wpis o parkowaniu na podstawie jego ID.
     */
    suspend fun deleteParkingSpot(documentId: String): Result<Unit> {
        return try {
            spotsCollection.document(documentId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}