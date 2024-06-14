package com.example.chamasegura.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.chamasegura.R
import com.example.chamasegura.data.entities.User
import com.example.chamasegura.data.entities.UserType
import com.example.chamasegura.data.vm.UserViewModel
import com.example.chamasegura.utils.AuthManager
import com.example.chamasegura.utils.JwtUtils

class fragment_manage_profile : Fragment() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var authManager: AuthManager

    private lateinit var editTextFullName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextNif: EditText
    private lateinit var buttonConfirm: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manage_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.title = ""

        // Inicialize as visualizações
        editTextFullName = view.findViewById(R.id.editTextFullName)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        editTextNif = view.findViewById(R.id.editTextNif)
        buttonConfirm = view.findViewById(R.id.buttonConfirm)

        authManager = AuthManager(requireContext())

        val token = authManager.getToken()
        val userId = token?.let { JwtUtils.getUserIdFromToken(it) }

        if (userId != null) {
            userViewModel.getUser(userId)
            userViewModel.user.observe(viewLifecycleOwner, Observer { user ->
                if (user != null) {
                    // Atualize a interface do usuário com os dados do usuário
                    editTextFullName.setText(user.name)
                    editTextEmail.setText(user.email)
                    editTextNif.setText(user.nif?.toString())
                    // Outras atualizações de UI, se necessário
                }
            })

            buttonConfirm.setOnClickListener {
                val email = editTextEmail.text.toString()
                val nif = editTextNif.text.toString().toIntOrNull()

                // Verifique se o nif é válido
                if (nif == null) {
                    Toast.makeText(requireContext(), "NIF inválido.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val updatedUser = User(
                    id = userId,
                    name = editTextFullName.text.toString(),
                    email = email,
                    password = "",
                    photo = null,
                    type = UserType.REGULAR,
                    createdAt = "",
                    updatedAt = "",
                    nif = nif
                )

                userViewModel.updateUser(userId, updatedUser) { success, errorMessage ->
                    val message = when {
                        errorMessage?.contains("Email already in use by another user") == true -> getString(R.string.error_email_in_use)
                        errorMessage?.contains("NIF already in use by another user") == true -> getString(R.string.error_nif_in_use)
                        else -> getString(R.string.error_update_profile)
                    }
                    if (success) {
                        Toast.makeText(requireContext(), getString(R.string.profile_updated_success), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            // Caso ID do utilizador não pode ser extraído
            Toast.makeText(requireContext(), getString(R.string.error_user_id_extraction), Toast.LENGTH_LONG).show()
            // Ir para o fragmento de login
            findNavController().navigate(R.id.action_fragment_manage_profile_to_fragment_login)
        }
    }
}