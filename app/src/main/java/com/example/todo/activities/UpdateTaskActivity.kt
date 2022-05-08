package com.example.todo.activities

import android.content.Intent
import android.os.Bundle
import androidx.room.Room
import com.example.todo.DataObject
import com.example.todo.Entity
import com.example.todo.R
import com.example.todo.ToDoDatabase
import kotlinx.android.synthetic.main.activity_update_task.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UpdateTaskActivity : ActivityBase() {
    private lateinit var database: ToDoDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_task)
        database = Room.databaseBuilder(
            applicationContext, ToDoDatabase::class.java, "To_Do"
        ).build()
        val pos = intent.getIntExtra("id", -1)
        if (pos != -1) {
            val title = DataObject.getData(pos).title
            val priority = DataObject.getData(pos).priority
            updateTitleInputText.setText(title)
            updateDescriptionInputText.setText(priority)

            deleteButton.setOnClickListener {
                DataObject.deleteData(pos)
                GlobalScope.launch {
                    database.dao().deleteTask(
                        Entity(
                            pos + 1,
                            updateTitleInputText.text.toString(),
                            updateDescriptionInputText.text.toString()
                        )
                    )
                }
                myIntent()
            }

            updateButton.setOnClickListener {
                DataObject.updateData(
                    pos,
                    updateTitleInputText.text.toString(),
                    updateDescriptionInputText.text.toString()
                )
                GlobalScope.launch {
                    database.dao().updateTask(
                        Entity(
                            pos + 1, updateTitleInputText.text.toString(),
                            updateDescriptionInputText.text.toString()
                        )
                    )
                }
                myIntent()
            }

        }
    }

    private fun myIntent() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}