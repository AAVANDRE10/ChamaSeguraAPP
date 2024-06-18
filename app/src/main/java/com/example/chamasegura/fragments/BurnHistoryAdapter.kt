package com.example.chamasegura.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamasegura.R
import com.example.chamasegura.data.entities.Burn
import java.text.SimpleDateFormat
import java.util.Locale

class BurnHistoryAdapter(private val onItemClick: (Burn) -> Unit) : RecyclerView.Adapter<BurnHistoryAdapter.BurnViewHolder>() {

    private var burns: List<Burn> = listOf()

    fun setBurns(burns: List<Burn>) {
        this.burns = burns
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BurnViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_burn_history, parent, false)
        return BurnViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: BurnViewHolder, position: Int) {
        val burn = burns[position]
        holder.bind(burn)
    }

    override fun getItemCount(): Int = burns.size

    class BurnViewHolder(itemView: View, private val onItemClick: (Burn) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.name)
        private val location: TextView = itemView.findViewById(R.id.location)
        private val type: TextView = itemView.findViewById(R.id.type)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val state: TextView = itemView.findViewById(R.id.state)

        fun bind(burn: Burn) {
            name.text = burn.reason
            location.text = "${burn.distrito}, ${burn.concelho}, ${burn.freguesia}"
            type.text = burn.type.toString()
            date.text = formatDate(burn.date)
            state.text = burn.state.toString()
            itemView.setOnClickListener { onItemClick(burn) }
        }

        private fun formatDate(dateStr: String): String {
            val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = isoDateFormat.parse(dateStr)
            return date?.let { outputDateFormat.format(it) } ?: dateStr
        }
    }
}