package com.example.memorama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(
    private val items: List<Item>,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCard: ImageView = itemView.findViewById(R.id.ivCard)

        init {
            itemView.setOnClickListener {
                onCardClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        if (item.isFlipped || item.isMatched) {
            holder.imgCard.setImageResource(item.imageResId)
        } else {
            holder.imgCard.setImageResource(R.drawable.ic_card_face_down)
        }
    }

    override fun getItemCount(): Int = items.size
}
