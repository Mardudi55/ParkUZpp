package com.ggs.parkuzpp.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.auth.AuthRepository

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var authRepository: AuthRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRepository = AuthRepository()

        val email = view.findViewById<EditText>(R.id.etEmail)
        val password = view.findViewById<EditText>(R.id.etPassword)
        val registerBtn = view.findViewById<Button>(R.id.btnRegister)

        registerBtn.setOnClickListener {

            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(requireContext(), "Uzupełnij dane", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authRepository.register(emailText, passwordText) { success, error ->

                if (success) {
                    Toast.makeText(requireContext(), "Rejestracja OK", Toast.LENGTH_SHORT).show()

                    findNavController().navigate(R.id.action_register_to_login)
                } else {
                    Toast.makeText(requireContext(), error ?: "Błąd rejestracji", Toast.LENGTH_SHORT).show()
                }
            }
        }

        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }
}