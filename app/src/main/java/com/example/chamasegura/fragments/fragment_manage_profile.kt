package com.example.chamasegura.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
                    // Outras atualizações de UI, se necessário
                }
            })

            buttonConfirm.setOnClickListener {
                val updatedUser = User(
                    id = userId,
                    name = editTextFullName.text.toString(),
                    email = editTextEmail.text.toString(),
                    password = "", // Deixe a senha em branco, pois não será atualizada aqui
                    photo = null,
                    type = UserType.REGULAR, // Obtenha o tipo do usuário da maneira apropriada
                    createdAt = "", // Use o valor real
                    updatedAt = ""  // Use o valor real
                )

                userViewModel.updateUser(userId, updatedUser)
            }
        } else {
            // Trate o caso onde o ID do usuário não pôde ser extraído
        }
    }
}