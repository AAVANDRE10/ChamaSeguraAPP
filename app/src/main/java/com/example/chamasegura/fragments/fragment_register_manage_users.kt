package com.example.chamasegura.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.chamasegura.R
import com.example.chamasegura.data.entities.LoginResponse
import com.example.chamasegura.data.entities.User
import com.example.chamasegura.data.entities.UserType
import com.example.chamasegura.data.entities.StateUser
import com.example.chamasegura.data.vm.MunicipalityViewModel
import com.example.chamasegura.data.vm.UserViewModel
import com.example.chamasegura.utils.JwtUtils

class fragment_register_manage_users : Fragment() {
    private val userViewModel: UserViewModel by viewModels()
    private val municipalityViewModel: MunicipalityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register_manage_users, container, false)

        val fullNameEditText: EditText = view.findViewById(R.id.full_name)
        val emailEditText: EditText = view.findViewById(R.id.email)
        val passwordEditText: EditText = view.findViewById(R.id.password)
        val confirmPasswordEditText: EditText = view.findViewById(R.id.confirm_password)
        val nifEditText: EditText = view.findViewById(R.id.nif)
        val userTypeSpinner: Spinner = view.findViewById(R.id.user_type_spinner)
        val municipalitySpinner: Spinner = view.findViewById(R.id.municipality_spinner)
        val confirmButton: Button = view.findViewById(R.id.confirm_button)
        val backButton = view.findViewById<ImageButton>(R.id.backButton)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Observing municipalities data
        municipalityViewModel.getMunicipalities()
        municipalityViewModel.municipalities.observe(viewLifecycleOwner) { municipalities ->
            val municipalityNames = municipalities.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, municipalityNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            municipalitySpinner.adapter = adapter
        }

        confirmButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val nif = nifEditText.text.toString().toIntOrNull()
            val userType = when (userTypeSpinner.selectedItem.toString()) {
                "Regular" -> UserType.REGULAR
                "CM" -> UserType.CM
                else -> UserType.REGULAR
            }
            val selectedMunicipality = municipalitySpinner.selectedItem.toString()

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword && nif != null) {
                if (nif == null) {
                    Toast.makeText(requireContext(), "NIF inválido.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                municipalityViewModel.municipalities.value?.let { municipalities ->
                    val municipality = municipalities.find { it.name == selectedMunicipality }
                    if (municipality != null) {
                        val newUser = User(
                            id = 0,
                            name = fullName,
                            email = email,
                            password = password,
                            photo = null,
                            nif = nif,
                            type = userType,
                            createdAt = "",
                            updatedAt = "",
                            state = StateUser.ENABLED // Default to ENABLED state
                        )
                        userViewModel.signUp(newUser) { response ->
                            val token = response?.token
                            if (token != null) {
                                val userId = JwtUtils.getUserIdFromToken(token)
                                if (userId != null) {
                                    municipalityViewModel.updateMunicipalityResponsible(municipality.id, userId)
                                    findNavController().navigate(R.id.fragment_manage_users)
                                } else {
                                    Toast.makeText(requireContext(), "Failed to get user ID from token", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(requireContext(), "Sign-up failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Municipality not found", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}