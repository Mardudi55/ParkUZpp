package com.ggs.parkuzpp

import android.app.Activity
import com.ggs.parkuzpp.auth.AuthRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [AuthRepository].
 * Uses Mockito to mock Firebase Authentication dependencies and simulate
 * successful and failed login and registration scenarios.
 */
class AuthRepositoryTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        firebaseAuth = mock()
        repository = AuthRepository(firebaseAuth)
    }

    @Test
    fun `login success`() {
        val task = mock<Task<AuthResult>>()
        whenever(firebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(task)
        whenever(task.isSuccessful).thenReturn(true)

        val listenerCaptor = argumentCaptor<OnCompleteListener<AuthResult>>()
        var successResult: Boolean? = null

        repository.login(mock<Activity>(), "test@test.com", "123456") { success, _ ->
            successResult = success
        }

        verify(task).addOnCompleteListener(any<Activity>(), listenerCaptor.capture())
        listenerCaptor.firstValue.onComplete(task)

        assertTrue("Login should be successful", successResult == true)
    }

    @Test
    fun `login failure`() {
        val task = mock<Task<AuthResult>>()
        whenever(firebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(task)
        whenever(task.isSuccessful).thenReturn(false)
        whenever(task.exception).thenReturn(Exception("error"))

        val listenerCaptor = argumentCaptor<OnCompleteListener<AuthResult>>()
        var errorResult: String? = null

        repository.login(mock<Activity>(), "test@test.com", "123456") { success, error ->
            if (!success) errorResult = error
        }

        verify(task).addOnCompleteListener(any<Activity>(), listenerCaptor.capture())
        listenerCaptor.firstValue.onComplete(task)

        assertEquals("error", errorResult)
    }

    @Test
    fun `register success`() {
        val task = mock<Task<AuthResult>>()
        whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(task)
        whenever(task.addOnSuccessListener(any())).thenReturn(task)
        whenever(task.addOnFailureListener(any())).thenReturn(task)

        val successCaptor = argumentCaptor<OnSuccessListener<AuthResult>>()
        var successResult: Boolean? = null

        repository.register("test@test.com", "123456") { success, _ ->
            successResult = success
        }

        verify(task).addOnSuccessListener(successCaptor.capture())
        successCaptor.firstValue.onSuccess(mock())

        assertTrue("Registration should be successful", successResult == true)
    }

    @Test
    fun `register failure`() {
        val task = mock<Task<AuthResult>>()
        whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(task)
        whenever(task.addOnSuccessListener(any())).thenReturn(task)
        whenever(task.addOnFailureListener(any())).thenReturn(task)

        val failureCaptor = argumentCaptor<OnFailureListener>()
        var errorResult: String? = null

        repository.register("test@test.com", "123456") { success, error ->
            if (!success) errorResult = error
        }

        verify(task).addOnFailureListener(failureCaptor.capture())
        failureCaptor.firstValue.onFailure(Exception("fail"))

        assertEquals("fail", errorResult)
    }
}