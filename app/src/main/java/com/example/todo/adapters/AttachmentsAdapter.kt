package com.example.todo.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.data.Attachment
import kotlinx.android.synthetic.main.attachment_view.view.*


class AttachmentsAdapter(
    private val context: Context,
    private val deleteAttachmentInterface: OnDeleteAttachmentInterface
) : RecyclerView.Adapter<AttachmentsAdapter.AttachmentsViewHolder>() {
    private var attachmentsList = mutableListOf<Attachment>()

    class AttachmentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentsViewHolder {
        return AttachmentsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.attachment_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AttachmentsViewHolder, position: Int) {
        val currentItem = attachmentsList[position]
        holder.itemView.fileNameTextView.text = currentItem.name
        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val mimeType: MimeTypeMap = MimeTypeMap.getSingleton()
            val fileType: String? = mimeType.getMimeTypeFromExtension(currentItem.extension)
            if (fileType != null && fileType != ""){
                intent.setDataAndType(currentItem.path.toUri(), fileType)
            } else {
                intent.setDataAndType(currentItem.path.toUri(), "*/*")
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // or Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener { v ->
            val popupMenu = PopupMenu(context, v)
            popupMenu.menu.add("DELETE")
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { item ->
                if (item.title == "DELETE") {
                    deleteAttachmentInterface.deleteAttachmentOnClick(position)
                }
                true
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return attachmentsList.size
    }

    fun setData(attachments: MutableList<Attachment>) {
        attachmentsList = attachments
        notifyDataSetChanged()
    }

    interface OnDeleteAttachmentInterface {

        fun deleteAttachmentOnClick(position: Int)

    }

}