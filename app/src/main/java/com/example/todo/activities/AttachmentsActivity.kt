package com.example.todo.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.R
import com.example.todo.adapters.AttachmentsAdapter
import com.example.todo.data.Attachment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


const val attachmentsListIntentKey = "attachmentsList"


class AttachmentsActivity : ActivityBase() {
    private val pickUpFileRequestCode = 1
    private var attachmentsList = mutableListOf<Attachment>()
    private lateinit var onDeleteAttachmentInterface: AttachmentsAdapter.OnDeleteAttachmentInterface
    private lateinit var recyclerAdapter: AttachmentsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attachments)

        getAttachmentsListFromIntent()
        setOnDeleteAttachmentInterface()
        setRecycler()
        setResult(RESULT_OK, intent)
    }

    private fun getAttachmentsListFromIntent() {
        val attachmentsListAsString = intent.getStringExtra(attachmentsListIntentKey)
        attachmentsList = try {
            val gson = Gson()
            val itemType = object : TypeToken<MutableList<Attachment>>() {}.type
            gson.fromJson(attachmentsListAsString, itemType)
        } catch (e: java.lang.Exception) {
            mutableListOf()
        }
    }

    private fun saveAttachmentsListToIntent() {
        val gson = Gson()
        val attachmentsListAsString = gson.toJson(attachmentsList).toString()
        intent.putExtra(attachmentsListIntentKey, attachmentsListAsString)
        setResult(RESULT_OK, intent)
    }

    private fun setOnDeleteAttachmentInterface() {
        onDeleteAttachmentInterface = object : AttachmentsAdapter.OnDeleteAttachmentInterface {
            override fun deleteAttachmentOnClick(position: Int) {
                attachmentsList.removeAt(position)
                saveAttachmentsListToIntent()
                recyclerAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setRecycler() {
        recyclerAdapter = AttachmentsAdapter(applicationContext, onDeleteAttachmentInterface)
        recycler_view.adapter = recyclerAdapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        recyclerAdapter.setData(attachmentsList)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.attachments_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.pickUpFile) {
            openPickUpFileActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openPickUpFileActivity() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        startActivityForResult(intent, pickUpFileRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (pickUpFileRequestCode == requestCode && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }

            val uri: Uri? = data.data
            if (uri != null && uri.path != null) {
                val file = File(uri.path)
                applicationContext.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                attachmentsList.add(Attachment(uri.toString(), file.name, file.extension))
                saveAttachmentsListToIntent()
                recyclerAdapter.notifyDataSetChanged()

            }
        }
    }
}