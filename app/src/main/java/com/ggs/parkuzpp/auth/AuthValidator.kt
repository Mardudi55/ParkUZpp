package com.ggs.parkuzpp.auth

object AuthValidator {

    fun isLoginValid(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    fun isRegisterValid(email: String, password: String, confirmPassword: String): Boolean {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) return false
        if (password != confirmPassword) return false
        return true
    }

    fun isCaptchaValid(selected: Set<Int>, correct: Set<Int>): Boolean {
        return selected == correct
    }
}