package com.example.chamasegura.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.chamasegura.R
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
        val confirmButton: Button = view.findViewById(R.id.confirm_button)

        confirmButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                val newUser = User(
                    id = 0,
                    name = fullName,
                    email = email,
                    password = password,
                    photo = null,
                    nif = null,
                    type = UserType.REGULAR,
                    createdAt = "",
                    updatedAt = ""
                )
                userViewModel.signUp(newUser)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        userViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                //open fragment login
                findNavController().navigate(R.id.action_fragment_register_to_fragment_first_screen2)
            } else {
                // Sign-up failed
                Toast.makeText(requireContext(), "Sign-up failed", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }
}