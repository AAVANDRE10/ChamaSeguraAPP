package com.example.chamasegura.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chamasegura.R
import com.example.chamasegura.data.entities.BurnType
import com.example.chamasegura.data.vm.BurnViewModel

class fragment_pending_burn_requests : Fragment() {

    private lateinit var burnViewModel: BurnViewModel
    private lateinit var burnAdapter: BurnPendingRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pending_burn_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.title = ""

        burnAdapter = BurnPendingRequestAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_pending_burn_requests)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = burnAdapter

        burnViewModel = ViewModelProvider(this).get(BurnViewModel::class.java)
        burnViewModel.pendingBurns.observe(viewLifecycleOwner, Observer {
            burnAdapter.setBurns(it)
        })

        burnViewModel.getPendingBurns()

        val sortButton = view.findViewById<TextView>(R.id.sort_button)
        sortButton.setOnClickListener {
            burnViewModel.getPendingBurnsOrderedByDate()
        }

        val spinnerType = view.findViewById<Spinner>(R.id.spinnerType)
        val types = resources.getStringArray(R.array.burn_types)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = when (position) {
                    1 -> BurnType.REGCLEAN
                    2 -> BurnType.PARTICULAR
                    else -> null
                }
                if (selectedType != null) {
                    burnViewModel.getPendingBurnsByType(selectedType)
                } else {
                    burnViewModel.getPendingBurns()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                burnViewModel.getPendingBurns()
            }
        }
    }
}