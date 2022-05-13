package com.example.todo.adapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.activities.UpdateTaskActivity
import com.example.todo.database.entities.TaskEntity
import com.example.todo.utils.TimeHandler
import kotlinx.android.synthetic.main.task_view.view.*


class TasksAdapter : RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {
    private var tasksList = emptyList<TaskEntity>()
    private val timeHandler: TimeHandler = TimeHandler()

    class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        return TasksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = tasksList[position]
        holder.itemView.title.text = currentItem.title
        holder.itemView.dueDate.text = timeHandler.generateTimeStringFromEpoch(currentItem.dueDate)
        holder.itemView.status.text = if (currentItem.isActive) "PENDING" else "DONE"
        holder.itemView.description.text = currentItem.description
        if (!currentItem.hasAttachments){
            holder.itemView.attachments.isVisible = false
        }
        if (currentItem.sendNotification){
            holder.itemView.notification.isVisible = false
        }

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