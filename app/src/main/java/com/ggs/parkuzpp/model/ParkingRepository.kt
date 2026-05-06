package com.ggs.parkuzpp.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repozytorium zarządza danymi parkowania w Firestore.
 * System wspiera zasadę "Single Active Spot" – tylko jedno parkowanie na raz może być aktywne.
 */
class ParkingRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val spotsCollection = firestore.collection("park-spots")

    private val currentUid: String?
        get() = auth.currentUser?.uid

    /**
     * Strumień, który dostarcza JEDYNY aktywny punkt parkowania użytkownika.
     * Używamy go na mapie do wyświetlania pinezki i sterowania przyciskiem.
     */
    fun getActiveSpotFlow(): Flow<ParkSpot?> = callbackFlow {
        val uid = currentUid ?: run {
            trySend(null)
            return@callbackFlow
        }

        val registration = spotsCollection
            .whereEqualTo("user-id", uid)
            .whereEqualTo("active", true)
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
     * Zapisuje nowe miejsce i automatycznie dezaktywuje wszystkie poprzednie.
     */
    suspend fun saveParkingSpot(spot: ParkSpot): Result<Unit> {
        val uid = currentUid ?: return Result.failure(Exception("Brak autoryzacji"))

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
     * Aktywuje konkretny punkt z historii (np. po kliknięciu "Mapa" w HistoryScreen).
     */
    suspend fun activateSpot(documentId: String): Result<Unit> {
        return try {
            deactivatePreviousSpots().getOrThrow()
            spotsCollection.document(documentId).update("active", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Ustawia active = false dla wszystkich punktów użytkownika.
     * Wywoływane przy kliknięciu "Zakończ parkowanie" lub przed nowym zapisem.
     */
    suspend fun deactivatePreviousSpots(): Result<Unit> {
        val uid = currentUid ?: return Result.failure(Exception("Brak użytkownika"))

        return try {
            val activeSpots = spotsCollection
                .whereEqualTo("user-id", uid)
                .whereEqualTo("active", true)
                .get()
                .await()

            if (!activeSpots.isEmpty) {
                firestore.runBatch { batch ->
                    for (doc in activeSpots.documents) {
                        batch.update(doc.reference, "active", false)
                    }
                }.await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Pobiera całą historię. Mapuje ID dokumentów, aby działało usuwanie/aktywacja.
     */
    fun getParkingHistory(): Flow<List<ParkSpot>> = callbackFlow {
        val uid = currentUid ?: run {
            trySend(emptyList())
            return@callbackFlow
        }

        val subscription = spotsCollection
            .whereEqualTo("user-id", uid)
            // .orderBy("timestamp", Query.Direction.DESCENDING) // Włącz, jeśli masz indeks
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val spots = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ParkSpot::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(spots)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun deleteParkingSpot(documentId: String): Result<Unit> {
        return try {
            spotsCollection.document(documentId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}