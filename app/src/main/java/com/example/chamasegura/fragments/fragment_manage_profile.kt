package com.example.chamasegura.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chamasegura.R
import com.example.chamasegura.data.entities.User
import com.example.chamasegura.data.entities.UserType
import com.example.chamasegura.data.entities.StateUser
import com.example.chamasegura.data.vm.UserViewModel
import com.example.chamasegura.utils.AuthManager
import com.example.chamasegura.utils.JwtUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class fragment_manage_profile : Fragment() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var authManager: AuthManager

    private lateinit var editTextFullName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextNif: EditText
    private lateinit var buttonConfirm: Button
    private lateinit var buttonChangePhoto: Button
    private lateinit var profileImage: ImageView
    private lateinit var textViewMemberSince: TextView
    private lateinit var textViewNumberOfBurnRequests: TextView
    private lateinit var buttonChangePassword: Button
    private lateinit var buttonDeleteProfile: Button

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private val PERMISSION_REQUEST_CODE = 1001

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
        buttonChangePhoto = view.findViewById(R.id.buttonChangePhoto)
        profileImage = view.findViewById(R.id.imageViewProfile)
        textViewMemberSince = view.findViewById(R.id.memberSince)
        textViewNumberOfBurnRequests = view.findViewById(R.id.numberOfBurnRequests)
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword)
        buttonDeleteProfile = view.findViewById(R.id.buttonDeleteProfile)

        authManager = AuthManager(requireContext())

        // Verificar e solicitar permissões
        checkAndRequestPermissions()

        val token = authManager.getToken()
        val userId = token?.let { JwtUtils.getUserIdFromToken(it) }

        if (userId != null) {
            userViewModel.getUser(userId).observe(viewLifecycleOwner, Observer { user ->
                if (user != null) {
                    // Atualize a interface do usuário com os dados do usuário
                    editTextFullName.setText(user.name)
                    editTextEmail.setText(user.email)
                    editTextNif.setText(user.nif?.toString())
                    textViewMemberSince.text = formatDate(user.createdAt)

                    // Carregar a imagem de perfil usando Glide
                    user.photo?.let {
                        val imageUrl = it
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.baseline_account_circle_24)
                            .into(profileImage)

                        profileImage.setOnClickListener {
                            val dialogFragment = fragment_expanded_image.newInstance(imageUrl)
                            dialogFragment.show(parentFragmentManager, "expandedImage")
                        }
                    }
                }
            })

            userViewModel.getNumberOfBurnRequests(userId)
            userViewModel.numberOfBurnRequests.observe(viewLifecycleOwner, Observer { count ->
                textViewNumberOfBurnRequests.text = count.toString()
            })

            buttonConfirm.setOnClickListener {
                val email = editTextEmail.text.toString()
                val nif = editTextNif.text.toString().toIntOrNull()

                // Verifique se o nif é válido
                if (nif == null) {
                    Toast.makeText(requireContext(), "NIF inválido.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                userViewModel.getUser(userId).observe(viewLifecycleOwner, Observer { user ->
                    user?.let {
                        val updatedUser = User(
                            id = userId,
                            name = editTextFullName.text.toString(),
                            email = email,
                            password = "",
                            photo = null,
                            type = it.type,
                            createdAt = it.createdAt,
                            updatedAt = it.updatedAt,
                            nif = nif,
                            state = it.state
                        )

                        userViewModel.updateUser(userId, updatedUser) { success, errorMessage ->
                            val message = when {
                                errorMessage?.contains("Email already in use by another user") == true -> getString(R.string.error_email_in_use)
                                errorMessage?.contains("NIF already in use by another user") == true -> getString(R.string.error_nif_in_use)
                                else -> getString(R.string.error_update_profile)
                            }
                            if (success) {
                                Toast.makeText(requireContext(), getString(R.string.profile_updated_success), Toast.LENGTH_LONG).show()
                                findNavController().navigate(R.id.action_fragment_manage_profile_to_fragment_home)
                            } else {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                })
            }

            buttonChangePhoto.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }

            buttonChangePassword.setOnClickListener {
                findNavController().navigate(R.id.action_fragment_manage_profile_to_fragment_change_password)
            }

            buttonDeleteProfile.setOnClickListener {
                showDeleteConfirmationDialog(userId)
            }

        } else {
            // Trate o caso onde o ID do usuário não pôde ser extraído
            Toast.makeText(requireContext(), getString(R.string.error_user_id_extraction), Toast.LENGTH_LONG).show()
            // Redirecionar para o fragmento de login
            findNavController().navigate(R.id.action_fragment_manage_profile_to_fragment_login)
        }
    }

    private fun showDeleteConfirmationDialog(userId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Profile")
            .setMessage("Are you sure you want to delete your profile?")
            .setPositiveButton("Yes") { dialog, which ->
                userViewModel.updateUserState(userId, StateUser.DISABLED) { success, errorMessage ->
                    if (success) {
                        Toast.makeText(requireContext(), "Profile has been deleted.", Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_fragment_manage_profile_to_fragment_login)
                    } else {
                        Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permissão concedida, continue com a ação
            } else {
                // Permissão negada, mostre uma mensagem ou tome outra ação apropriada
                Toast.makeText(requireContext(), "Permissões necessárias não foram concedidas.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            profileImage.setImageURI(selectedImageUri)

            val userId = authManager.getToken()?.let { JwtUtils.getUserIdFromToken(it) }
            if (userId != null && selectedImageUri != null) {
                val fileExtension = getFileExtension(selectedImageUri!!)
                if (fileExtension != null && (fileExtension == "jpeg" || fileExtension == "jpg" || fileExtension == "png" || fileExtension == "gif")) {
                    try {
                        val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri!!)
                        val file = File(requireContext().cacheDir, "temp_image.$fileExtension")
                        val outputStream = FileOutputStream(file)
                        inputStream.use { input ->
                            outputStream.use { output ->
                                input?.copyTo(output)
                            }
                        }
                        val requestFile = file.asRequestBody("image/$fileExtension".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

                        userViewModel.updatePhoto(userId, body) { success, errorMessage ->
                            if (success) {
                                Toast.makeText(requireContext(), "Foto de perfil atualizada com sucesso.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(requireContext(), "Erro ao atualizar foto de perfil: $errorMessage", Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Erro ao processar o arquivo: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro: Apenas são permitidos arquivos de imagem!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        val mimeTypeMap = android.webkit.MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun formatDate(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = sdf.parse(dateString)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return outputFormat.format(date)
    }
}