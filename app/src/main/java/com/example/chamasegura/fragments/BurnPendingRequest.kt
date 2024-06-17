package com.example.chamasegura.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamasegura.R
import com.example.chamasegura.data.entities.Burn

class BurnPendingRequest : RecyclerView.Adapter<BurnPendingRequest.BurnViewHolder>() {
    private var burns: List<Burn> = listOf()

    fun setBurns(burns: List<Burn>) {
        this.burns = burns
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BurnViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_pending_burn_requests, parent, false)
        return BurnViewHolder(view)
    }

    override fun onBindViewHolder(holder: BurnViewHolder, position: Int) {
        val burn = burns[position]
        holder.bind(burn)
    }

    override fun getItemCount(): Int = burns.size

    class BurnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val reason: TextView = itemView.findViewById(R.id.reason)
        private val location: TextView = itemView.findViewById(R.id.location)
        private val type: TextView = itemView.findViewById(R.id.type)
        private val date: TextView = itemView.findViewById(R.id.date)

        fun bind(burn: Burn) {
            reason.text = burn.reason
            location.text = "${burn.distrito}, ${burn.concelho}, ${burn.freguesia}"
            type.text = burn.type.toString()
            date.text = burn.date
        }
    }
}