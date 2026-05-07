package com.ggs.parkuzpp.auth

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth

/**
 * Repository responsible for handling Firebase Authentication operations.
 *
 * @property auth The Firebase Authentication instance to be used. Defaults to the default instance.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /**
     * Registers a new user with the provided email and password.
     *
     * @param email The user's email address.
     * @param password The user's chosen password.
     * @param onResult Callback triggered upon completion, returning a success flag and an optional error message.
     */
    fun register(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }

    /**
     * Authenticates an existing user using their email and password.
     *
     * @param activity The activity scoping the Firebase asynchronous task.
     * @param email The user's email address.
     * @param password The user's password.
     * @param onResult Callback triggered upon completion, returning a success flag and an optional error message.
     */
    fun login(
        activity: Activity,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    /**
     * Signs out the currently authenticated user.
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Checks whether a user is currently logged in.
     *
     * @return True if a user is authenticated, false otherwise.
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}