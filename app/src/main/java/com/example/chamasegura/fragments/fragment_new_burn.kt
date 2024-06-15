package com.example.chamasegura.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
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
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chamasegura.MapsActivity
import com.example.chamasegura.R
import com.example.chamasegura.data.entities.Burn
import com.example.chamasegura.data.entities.BurnType
import com.example.chamasegura.data.vm.BurnViewModel
import com.example.chamasegura.utils.AuthManager
import com.example.chamasegura.utils.JwtUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class fragment_new_burn : Fragment() {

    private lateinit var burnViewModel: BurnViewModel
    private lateinit var selectedLocationText: TextView
    private var latitude: Float = 0f
    private var longitude: Float = 0f
    private val MAP_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_burn, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        burnViewModel = ViewModelProvider(this).get(BurnViewModel::class.java)

        val buttonBack = view.findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val editTextDate = view.findViewById<EditText>(R.id.editTextDate)
        editTextDate.setOnClickListener {
            showDatePickerDialog(editTextDate)
        }

        val spinnerType = view.findViewById<Spinner>(R.id.spinnerType)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.burn_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerType.adapter = adapter
        }

        val editTextReason = view.findViewById<EditText>(R.id.editTextReason)
        val editTextOtherData = view.findViewById<EditText>(R.id.editTextOtherData)
        selectedLocationText = view.findViewById(R.id.selectedLocation)

        val buttonOpenMap = view.findViewById<Button>(R.id.buttonOpenMap)
        buttonOpenMap.setOnClickListener {
            openMap()
        }

        val buttonConfirm = view.findViewById<Button>(R.id.buttonConfirm)
        buttonConfirm.setOnClickListener {
            createBurn(editTextDate, spinnerType, editTextReason, editTextOtherData)
        }

        // Observar o resultado da criação do Burn
        burnViewModel.createBurnResult.observe(viewLifecycleOwner) { result ->
            val (success, message) = result
            if (success) {
                Toast.makeText(requireContext(), "Burn criado com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Erro ao criar burn: $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                editText.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    private fun createBurn(
        editTextDate: EditText,
        spinnerType: Spinner,
        editTextReason: EditText,
        editTextOtherData: EditText
    ) {
        val dateString = editTextDate.text.toString()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdf.parse(dateString)

        val type = when (spinnerType.selectedItem.toString()) {
            "REGCLEAN" -> BurnType.REGCLEAN
            "PARTICULAR" -> BurnType.PARTICULAR
            else -> BurnType.REGCLEAN
        }

        val reason = editTextReason.text.toString()
        val otherData = editTextOtherData.text.toString()

        val authManager = AuthManager(requireContext())
        val token = authManager.getToken()
        val userId = token?.let { JwtUtils.getUserIdFromToken(it) }

        if (userId != null && date != null) {
            val burn = Burn(
                id = 0,
                date = date.toString(),
                reason = reason,
                latitude = latitude, // Placeholder - você deve coletar latitude real
                longitude = longitude, // Placeholder - você deve coletar longitude real
                otherData = otherData,
                createdAt = "",
                updatedAt = "",
                userId = userId,
                type = type
            )
            burnViewModel.createBurn(burn)
        } else {
            Toast.makeText(requireContext(), "Erro ao criar queima.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun openMap() {
        val intent = Intent(requireContext(), MapsActivity::class.java)
        startActivityForResult(intent, MAP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MAP_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            latitude = data.getFloatExtra("latitude", 0f)
            longitude = data.getFloatExtra("longitude", 0f)
            selectedLocationText.text = "Lat: $latitude, Lng: $longitude"
            selectedLocationText.visibility = View.VISIBLE
        }
    }
}