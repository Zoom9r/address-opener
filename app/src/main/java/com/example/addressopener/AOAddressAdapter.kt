package com.example.addressopener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AOAddressAdapter(
    private val addresses: List<Map<String, String>>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<AOAddressAdapter.AddressViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_address_ao, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresses[position]
        val street = address["street"] ?: "unknown"
        val house = address["house"] ?: "unknown"
        val apartment = address["apartment"]?.takeIf { it.isNotEmpty() } ?: "—"

        holder.streetTextView.text = street
        holder.houseTextView.text = house
        holder.apartmentTextView.text = apartment
    }

    override fun getItemCount(): Int {
        return addresses.size
    }

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val streetTextView: TextView = itemView.findViewById(R.id.streetTextViewAO)
        val houseTextView: TextView = itemView.findViewById(R.id.houseTextViewAO)
        val apartmentTextView: TextView = itemView.findViewById(R.id.apartmentTextViewAO)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButtonAO)

        init {
            deleteButton.setOnClickListener {
                onDeleteClick(adapterPosition)
            }
        }
    }
}

