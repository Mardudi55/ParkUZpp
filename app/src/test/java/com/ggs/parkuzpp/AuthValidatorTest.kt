package com.ggs.parkuzpp

import com.ggs.parkuzpp.auth.AuthValidator
import org.junit.Assert.*
import org.junit.Test

class AuthValidatorTest {

    // 🔥 LOGIN

    @Test
    fun login_emptyEmail_shouldFail() {
        val result = AuthValidator.isLoginValid("", "123456")
        assertFalse(result)
    }

    @Test
    fun login_emptyPassword_shouldFail() {
        val result = AuthValidator.isLoginValid("test@test.com", "")
        assertFalse(result)
    }

    @Test
    fun login_validData_shouldPass() {
        val result = AuthValidator.isLoginValid("test@test.com", "123456")
        assertTrue(result)
    }

    // 🔥 REGISTER

    @Test
    fun register_emptyFields_shouldFail() {
        val result = AuthValidator.isRegisterValid("", "", "")
        assertFalse(result)
    }

    @Test
    fun register_passwordsNotMatching_shouldFail() {
        val result = AuthValidator.isRegisterValid("test@test.com", "123", "456")
        assertFalse(result)
    }

    @Test
    fun register_validData_shouldPass() {
        val result = AuthValidator.isRegisterValid("test@test.com", "123456", "123456")
        assertTrue(result)
    }

    // 🔥 CAPTCHA

    @Test
    fun captcha_correctSelection_shouldPass() {
        val selected = setOf(1, 2, 3)
        val correct = setOf(1, 2, 3)

        val result = AuthValidator.isCaptchaValid(selected, correct)
        assertTrue(result)
    }

    @Test
    fun captcha_wrongSelection_shouldFail() {
        val selected = setOf(1, 2)
        val correct = setOf(1, 2, 3)

        val result = AuthValidator.isCaptchaValid(selected, correct)
        assertFalse(result)
    }
}