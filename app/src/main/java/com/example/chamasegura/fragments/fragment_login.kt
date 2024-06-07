package com.example.chamasegura.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.chamasegura.R

class fragment_login : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Find the button and set an OnClickListener
        val loginButton: Button = view.findViewById(R.id.login_confirm_button)
        loginButton.setOnClickListener {
            // Navigate to the fragment_home
            findNavController().navigate(R.id.action_fragment_login_to_fragment_home)
        }

        return view
    }
}