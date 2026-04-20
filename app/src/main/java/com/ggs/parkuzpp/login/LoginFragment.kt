package com.ggs.parkuzpp.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ggs.parkuzpp.R
import com.ggs.parkuzpp.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var authRepository: AuthRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRepository = AuthRepository()

        val email = view.findViewById<EditText>(R.id.etEmail)
        val password = view.findViewById<EditText>(R.id.etPassword)
        val loginBtn = view.findViewById<Button>(R.id.btnLogin)
        val registerText = view.findViewById<TextView>(R.id.tvRegister)

        loginBtn.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(requireContext(), "Uzupełnij dane", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginBtn.isEnabled = false

            authRepository.login(requireActivity(), emailText, passwordText) { success, error ->

                loginBtn.isEnabled = true

                if (success) {
                    Toast.makeText(requireContext(), "Login OK", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_login_to_map)
                } else {
                    // 🔥 pełny error do debugowania
                    Toast.makeText(
                        requireContext(),
                        "ERROR: ${error ?: "Nieznany błąd"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        registerText.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }
}