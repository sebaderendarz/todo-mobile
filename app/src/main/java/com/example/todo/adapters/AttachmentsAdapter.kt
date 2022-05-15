package com.example.todo.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import kotlinx.android.synthetic.main.attachment_view.view.*

class AttachmentsAdapter(private val context: Context): RecyclerView.Adapter<AttachmentsAdapter.AttachmentsViewHolder>() {
    private var attachmentsList = mutableListOf<String>()

    class AttachmentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentsViewHolder {
        return AttachmentsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.attachment_view, parent, false))
    }

    override fun onBindViewHolder(holder: AttachmentsViewHolder, position: Int) {
        val currentItem = attachmentsList[position]
        holder.itemView.fileNameTextView.text = currentItem
        holder.itemView.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            // TODO make sure `type` is fine.
            intent.setDataAndType(Uri.parse(currentItem), "*/*")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener { v ->
            val popupMenu = PopupMenu(context, v)
            popupMenu.menu.add("DELETE")
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { item ->
                if (item.title == "DELETE"){
                    // delete item from the list of folders
                    println("DELETE item from the list")
                    attachmentsList.removeAt(position)
                    notifyDataSetChanged()
                }
                true
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return attachmentsList.size
    }

    fun setData(attachments: MutableList<String>){
        attachmentsList = attachments
        notifyDataSetChanged()
    }
}