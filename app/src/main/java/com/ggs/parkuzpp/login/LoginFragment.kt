package com.ggs.parkuzpp.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.ggs.parkuzpp.R

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<Button>(R.id.btnLogin).setOnClickListener {
            findNavController().navigate(R.id.action_login_to_map)
        }

        view.findViewById<TextView>(R.id.tvRegister).setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }
}