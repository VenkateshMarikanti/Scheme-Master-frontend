package com.simats.schememasters.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.schememasters.R
import com.simats.schememasters.models.StudentScheme

class SchemeAdapter(
    private var schemes: List<StudentScheme>,
    private val onClick: (StudentScheme) -> Unit
) : RecyclerView.Adapter<SchemeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvPostMatricTitle)
        val subtitle: TextView = view.findViewById(R.id.tvSubtitle)
        val icon: ImageView = view.findViewById(R.id.ivSchemeIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scheme_dynamic, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scheme = schemes[position]
        holder.title.text = scheme.schemeName
        holder.subtitle.text = "${scheme.casteName} • Scholarship"
        
        // Custom icon for student schemes
        holder.icon.setImageResource(R.drawable.ic_student)
        
        holder.itemView.setOnClickListener { onClick(scheme) }
    }

    override fun getItemCount() = schemes.size

    fun updateList(newList: List<StudentScheme>) {
        schemes = newList
        notifyDataSetChanged()
    }
}
