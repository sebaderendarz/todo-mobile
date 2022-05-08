package com.example.todo.activities


import android.content.Intent
import android.os.Bundle
import androidx.room.Room
import com.example.todo.DataObject
import com.example.todo.Entity
import com.example.todo.R
import com.example.todo.ToDoDatabase
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddTaskActivity : ActivityBase() {
    private lateinit var database: ToDoDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        database = Room.databaseBuilder(
            applicationContext, ToDoDatabase::class.java, "To_Do"
        ).build()
        save_button.setOnClickListener {
            if (addTitleInputText.text.toString().trim { it <= ' ' }.isNotEmpty()
                && addDescriptionInputText.text.toString().trim { it <= ' ' }.isNotEmpty()
            ) {
                var title = addTitleInputText.getText().toString()
                var priority = addDescriptionInputText.getText().toString()
                DataObject.setData(title, priority)
                GlobalScope.launch {
                    database.dao().insertTask(Entity(0, title, priority))

                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

}

