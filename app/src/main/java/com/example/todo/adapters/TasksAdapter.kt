package com.example.todo.adapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.activities.UpdateTaskActivity
import com.example.todo.database.entities.TaskEntity
import kotlinx.android.synthetic.main.task_view.view.*


class TasksAdapter : RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {
    private var tasksList = emptyList<TaskEntity>()

    class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        return TasksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = tasksList[position]
        holder.itemView.taskLayout.setBackgroundColor(Color.parseColor("#00917C"))
        holder.itemView.title.text = currentItem.title
        holder.itemView.description.text = currentItem.description

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateTaskActivity::class.java)
            intent.putExtra("taskId", currentItem.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    fun setData(tasks: List<TaskEntity>) {
        tasksList = tasks
        notifyDataSetChanged()
    }
}