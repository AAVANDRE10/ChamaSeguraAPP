package com.example.chamasegura.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.chamasegura.R

class fragment_burn_info : Fragment() {
    private val args: fragment_burn_infoArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_burn_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val burn = args.burn
        val backButton = view.findViewById<ImageButton>(R.id.backButton)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<TextView>(R.id.from).text = "From: ${burn.userId}"
        view.findViewById<TextView>(R.id.status).text = "Status: ${burn.state}"
        view.findViewById<TextView>(R.id.type).text = "Type: ${burn.type}"
        view.findViewById<TextView>(R.id.reason).text = "Reason: ${burn.reason}"
        view.findViewById<TextView>(R.id.date).text = "Date: ${burn.date}"
        view.findViewById<TextView>(R.id.location).text = "Location: ${burn.distrito}, ${burn.concelho}, ${burn.freguesia}"
    }
}