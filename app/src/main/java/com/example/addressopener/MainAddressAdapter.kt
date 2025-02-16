package com.example.addressopener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainAddressAdapter(
    private val addresses: List<Map<String, String>>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<MainAddressAdapter.AddressViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresses[position]
        holder.streetTextView.text = "${address["street"]}, буд. ${address["number"]}"

        // Відображення підказки (близькі АО-адреси)
        val hint = address["hint"]
        if (!hint.isNullOrEmpty()) {
            holder.hintTextView.text = "Поруч:\n$hint"
            holder.hintTextView.visibility = View.VISIBLE
        } else {
            holder.hintTextView.visibility = View.GONE
        }

        // Обробка кнопки видалення
        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = addresses.size

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val streetTextView: TextView = itemView.findViewById(R.id.streetTextView)
        val hintTextView: TextView = itemView.findViewById(R.id.hintTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }
}

