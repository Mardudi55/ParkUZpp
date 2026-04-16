package com.ggs.parkuzpp.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.ggs.parkuzpp.R

class MapFragment : Fragment(R.layout.fragment_map) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<Button>(R.id.btnHistory).setOnClickListener {
            findNavController().navigate(R.id.action_map_to_history)
        }

        view.findViewById<Button>(R.id.btnAccount).setOnClickListener {
            findNavController().navigate(R.id.action_map_to_account)
        }
    }
}