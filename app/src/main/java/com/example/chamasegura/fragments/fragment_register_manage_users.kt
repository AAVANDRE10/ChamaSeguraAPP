package com.example.chamasegura.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.chamasegura.R
import com.example.chamasegura.data.entities.User
import com.example.chamasegura.data.entities.UserType
import com.example.chamasegura.data.entities.StateUser
import com.example.chamasegura.data.vm.MunicipalityViewModel
import com.example.chamasegura.data.vm.UserViewModel
import com.example.chamasegura.utils.JwtUtils
import com.toptoche.searchablespinnerlibrary.SearchableSpinner

class fragment_register_manage_users : Fragment() {
    private val userViewModel: UserViewModel by viewModels()
    private val municipalityViewModel: MunicipalityViewModel by viewModels()
    private var passwordVisible = false
    private var confirmPasswordVisible = false

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
        val municipalitySpinner: SearchableSpinner = view.findViewById(R.id.municipality_spinner)
        val confirmButton: Button = view.findViewById(R.id.confirm_button)
        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        val togglePasswordVisibilityButton = view.findViewById<ImageButton>(R.id.toggle_password_visibility)
        val toggleConfirmPasswordVisibilityButton = view.findViewById<ImageButton>(R.id.toggle_confirm_password_visibility)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        togglePasswordVisibilityButton.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordVisibilityButton.setImageResource(R.drawable.baseline_visibility_24)
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordVisibilityButton.setImageResource(R.drawable.baseline_visibility_off_24)
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        toggleConfirmPasswordVisibilityButton.setOnClickListener {
            confirmPasswordVisible = !confirmPasswordVisible
            if (confirmPasswordVisible) {
                confirmPasswordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                toggleConfirmPasswordVisibilityButton.setImageResource(R.drawable.baseline_visibility_24)
            } else {
                confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                toggleConfirmPasswordVisibilityButton.setImageResource(R.drawable.baseline_visibility_off_24)
            }
            confirmPasswordEditText.setSelection(confirmPasswordEditText.text.length)
        }

        // Observing municipalities data
        municipalityViewModel.getMunicipalities()
        municipalityViewModel.municipalities.observe(viewLifecycleOwner) { municipalities ->
            val municipalityNames = municipalities.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, municipalityNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            municipalitySpinner.adapter = adapter
            municipalitySpinner.setTitle("Select Municipality")
            municipalitySpinner.setPositiveButton("OK")
        }

        // Setting up the user type spinner to disable municipality spinner if type is REGULAR
        userTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedUserType = userTypeSpinner.selectedItem.toString()
                if (selectedUserType == "REGULAR") {
                    municipalitySpinner.isEnabled = false
                    municipalitySpinner.setSelection(0) // Optionally clear the selection
                } else {
                    municipalitySpinner.isEnabled = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        confirmButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val nif = nifEditText.text.toString().toIntOrNull()
            val userType = when (userTypeSpinner.selectedItem.toString()) {
                "REGULAR" -> UserType.REGULAR
                "CM" -> UserType.CM
                else -> UserType.REGULAR
            }
            val selectedMunicipality = if (municipalitySpinner.isEnabled) municipalitySpinner.selectedItem.toString() else ""

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword && nif != null) {
                if (nif == null) {
                    Toast.makeText(requireContext(), "NIF inválido.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                municipalityViewModel.municipalities.value?.let { municipalities ->
                    val municipality = municipalities.find { it.name == selectedMunicipality }
                    if (municipality != null || userType == UserType.REGULAR) {
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
                            state = StateUser.ENABLED
                        )
                        userViewModel.signUp(newUser) { response ->
                            val token = response?.token
                            if (token != null) {
                                val userId = JwtUtils.getUserIdFromToken(token)
                                if (userId != null) {
                                    if (userType == UserType.CM && municipality != null) {
                                        municipalityViewModel.updateMunicipalityResponsible(municipality.id, userId)
                                    }
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