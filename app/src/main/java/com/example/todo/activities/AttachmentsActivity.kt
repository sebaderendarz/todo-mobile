package com.example.todo.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.todo.R

class AttachmentsActivity : ActivityBase() {
    private var pickUpFileRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attachments)
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
            Toast.makeText(applicationContext, uri!!.path, Toast.LENGTH_SHORT).show()
        }
    }
}