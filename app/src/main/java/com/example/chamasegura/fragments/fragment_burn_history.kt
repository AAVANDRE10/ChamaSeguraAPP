package com.example.chamasegura.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chamasegura.R
import com.example.chamasegura.data.entities.BurnType
import com.example.chamasegura.data.vm.BurnViewModel

class fragment_burn_history : Fragment() {
    private lateinit var burnViewModel: BurnViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BurnHistoryAdapter
    private var userId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_burn_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.title = ""

        recyclerView = view.findViewById(R.id.recycler_view_burn_history)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = BurnHistoryAdapter { burn ->
            val action = fragment_burn_historyDirections.actionFragmentBurnHistoryToFragmentBurnInfo(burn)
            findNavController().navigate(action)
        }
        recyclerView.adapter = adapter

        burnViewModel = ViewModelProvider(this).get(BurnViewModel::class.java)

        burnViewModel.getBurns()

        burnViewModel.burns.observe(viewLifecycleOwner) { burns ->
            if (burns != null) {
                adapter.setBurns(burns)
            }
        }

        val sortButton = view.findViewById<TextView>(R.id.sort_button)
        sortButton.setOnClickListener {
            burnViewModel.getBurnsOrderedByDate()
        }

        val spinnerType = view.findViewById<Spinner>(R.id.spinnerType)
        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = when (position) {
                    1 -> BurnType.REGCLEAN
                    2 -> BurnType.PARTICULAR
                    else -> null
                }
                if (selectedType != null) {
                    burnViewModel.getBurnsByType(selectedType)
                } else {
                    burnViewModel.getBurns()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                burnViewModel.getBurns()
            }
        }
    }
}