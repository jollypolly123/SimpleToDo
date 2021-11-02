package com.example.simpletodo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Tells recyclerview how to display data given
class TaskItemAdapter(val listOfItems: List<String>,
                      val longClickListener: OnLongClickListener) :
    RecyclerView.Adapter<TaskItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // store references to elements in layout view
        val textView: TextView = itemView.findViewById(android.R.id.text1)

        init {
            itemView.setOnLongClickListener {
                longClickListener.onItemLongClicked(adapterPosition)
                Log.i("bestie", "hey bestie")
                true
            }
        }

    }

    interface OnLongClickListener {
        fun onItemLongClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val contactView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)

        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // get data model based on position
        val item = listOfItems.get(position)

        holder.textView.text = item
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }
}