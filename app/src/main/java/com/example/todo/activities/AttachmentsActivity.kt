package com.example.todo.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.R
import com.example.todo.adapters.AttachmentsAdapter
import kotlinx.android.synthetic.main.activity_main.*

// TODO
// 1. Take initial list of files from the intent
// 2. Hold the current list of files when orientation changes. onCreate is called each time.
//      Maybe simply saving the current state of mutable list to intent before orientation change
//      is enough and the parent activity will know the current state to?

class AttachmentsActivity : ActivityBase() {
    private var attachmentsList = mutableListOf<String>()
    private var pickUpFileRequestCode = 1
    private lateinit var onDeleteAttachmentInterface: AttachmentsAdapter.OnDeleteAttachmentInterface
    private lateinit var recyclerAdapter: AttachmentsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attachments)


        setOnDeleteAttachmentInterface()
        setRecycler()
    }

    private fun setOnDeleteAttachmentInterface(){
        onDeleteAttachmentInterface = object : AttachmentsAdapter.OnDeleteAttachmentInterface {
            override fun deleteAttachmentOnClick(position: Int){
                attachmentsList.removeAt(position)
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

    private fun openPickUpFileActivity(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, pickUpFileRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (pickUpFileRequestCode == requestCode && resultCode == Activity.RESULT_OK){
            if (data == null){
                return
            }

            val uri: Uri? = data.data
            if (uri!!.path != null){
                attachmentsList.add(uri.path.toString())
                recyclerAdapter.notifyDataSetChanged()
            }
            Toast.makeText(applicationContext, uri.path, Toast.LENGTH_SHORT).show()
        }
    }
}