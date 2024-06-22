package com.example.chamasegura.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chamasegura.R
import com.example.chamasegura.data.entities.StateUser
import com.example.chamasegura.data.entities.User
import com.example.chamasegura.data.entities.UserType
import com.example.chamasegura.data.vm.UserViewModel

class fragment_register : Fragment() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Find views
        val fullNameEditText: EditText = view.findViewById(R.id.full_name)
        val emailEditText: EditText = view.findViewById(R.id.email)
        val passwordEditText: EditText = view.findViewById(R.id.password)
        val confirmPasswordEditText: EditText = view.findViewById(R.id.confirm_password)
        val nifEditText: EditText = view.findViewById(R.id.nif)
        val confirmButton: Button = view.findViewById(R.id.confirm_button)
        val showPasswordButton: ImageButton = view.findViewById(R.id.show_password_button)
        val showConfirmPasswordButton: ImageButton = view.findViewById(R.id.show_confirm_password_button)

        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        showPasswordButton.setOnClickListener {
            togglePasswordVisibility(passwordEditText, showPasswordButton)
        }

        showConfirmPasswordButton.setOnClickListener {
            togglePasswordVisibility(confirmPasswordEditText, showConfirmPasswordButton)
        }

        confirmButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val nifString = nifEditText.text.toString().trim()
            val nif = nifString.toIntOrNull()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && nif != null && password == confirmPassword) {
                val newUser = User(
                    id = 0,
                    name = fullName,
                    email = email,
                    password = password,
                    photo = null,
                    nif = nif,
                    type = UserType.REGULAR,
                    createdAt = "",
                    updatedAt = "",
                    state = StateUser.ENABLED
                )
                userViewModel.signUp(newUser) { loginResponse ->
                    val user = loginResponse?.name
                    if (user != null) {
                        findNavController().navigate(R.id.action_fragment_register_to_fragment_first_screen2)
                    } else {
                        // Sign-up failed
                        Toast.makeText(requireContext(), "Sign-up failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun togglePasswordVisibility(editText: EditText, button: ImageButton) {
        if (editText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            button.setImageResource(R.drawable.baseline_visibility_24)
        } else {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            button.setImageResource(R.drawable.baseline_visibility_off_24)
        }
        editText.setSelection(editText.text.length)
    }
}