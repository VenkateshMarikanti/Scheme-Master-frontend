package com.simats.schememasters.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.simats.schememasters.R
import com.simats.schememasters.models.ValidationItem

class ValidationResultAdapter(private val items: List<ValidationItem>) :
    RecyclerView.Adapter<ValidationResultAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.cardResult)
        val icon: ImageView = view.findViewById(R.id.ivStatusIcon)
        val tvName: TextView = view.findViewById(R.id.tvDocName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatusText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_validation_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.documentName
        
        if (item.status == "uploaded") {
            holder.tvStatus.text = "Verified"
            holder.tvStatus.setTextColor(Color.parseColor("#15803D"))
            holder.card.setCardBackgroundColor(Color.parseColor("#F0FDF4"))
            holder.icon.setImageResource(android.R.drawable.checkbox_on_background)
            holder.icon.setColorFilter(Color.parseColor("#15803D"))
        } else {
            holder.tvStatus.text = "Missing"
            holder.tvStatus.setTextColor(Color.parseColor("#B91C1C"))
            holder.card.setCardBackgroundColor(Color.parseColor("#FEF2F2"))
            holder.icon.setImageResource(android.R.drawable.ic_delete)
            holder.icon.setColorFilter(Color.parseColor("#B91C1C"))
        }
    }

    override fun getItemCount() = items.size
}