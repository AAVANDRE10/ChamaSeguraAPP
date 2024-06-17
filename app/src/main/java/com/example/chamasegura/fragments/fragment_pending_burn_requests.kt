package com.example.chamasegura.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chamasegura.R
import com.example.chamasegura.data.vm.BurnViewModel

class fragment_pending_burn_requests : Fragment() {

    private lateinit var burnViewModel: BurnViewModel
    private lateinit var burnAdapter: BurnPendingRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pending_burn_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        burnAdapter = BurnPendingRequest()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_pending_burn_requests)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = burnAdapter

        burnViewModel = ViewModelProvider(this).get(BurnViewModel::class.java)
        burnViewModel.pendingBurns.observe(viewLifecycleOwner, Observer {
            burnAdapter.setBurns(it)
        })

        burnViewModel.getPendingBurns()
    }
}