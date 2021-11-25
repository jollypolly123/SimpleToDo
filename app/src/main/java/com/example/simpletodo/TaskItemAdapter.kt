package com.example.simpletodo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


// Tells recyclerview how to display data given
class TaskItemAdapter(
    private val listOfItems: List<String>,
    val longClickListener: OnLongClickListener,
    val onEditTask: EditTaskListener) :
RecyclerView.Adapter<TaskItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // store references to elements in layout view
        val taskName: TextView = itemView.findViewById(android.R.id.title)
        val taskDate: TextView = itemView.findViewById(android.R.id.text1)
        val taskDesc: TextView = itemView.findViewById(android.R.id.text2)

        init {
            itemView.setOnLongClickListener {
                longClickListener.onItemLongClicked(adapterPosition)
                Log.i("TaskItemAdapter", "Item deleted: $adapterPosition")
                true
            }
            itemView.setOnClickListener {
                onEditTask.editTask(adapterPosition)
            }
        }

    }

    interface OnLongClickListener {
        fun onItemLongClicked(position: Int)
    }

    interface EditTaskListener {
        fun editTask(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val contactView = inflater.inflate(R.layout.task_list_item, parent, false)

        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // get data model based on position
        val item = listOfItems[position].split("|~|")

        holder.taskName.text = item[0].trim()
        holder.taskDate.text = item[1].trim()
        holder.taskDesc.text = item[2].trim()
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }
}