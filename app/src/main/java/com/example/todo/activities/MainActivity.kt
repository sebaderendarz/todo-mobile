package com.example.todo.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.todo.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


// TaskEntity - table
// dao - queries
// database

// TODO
// 1. Update CardInfo object. Add more fields. Think over if we need DataObject at all.
// 2. Implement a logic that will update the list of tasks automatically.
// 3. Add sharedPreferences logic and update of tasks list after click on menu.


class MainActivity : ActivityBase() {
    private lateinit var database: ToDoDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = Room.databaseBuilder(
            applicationContext, ToDoDatabase::class.java, "ToDo"
        ).build()
        GlobalScope.launch {
            DataObject.listdata = database.dao().getTasks() as MutableList<CardInfo>
        }

        setRecycler()
        configureBinding()
    }

    private fun setRecycler() {
        recycler_view.adapter = TasksAdapter(DataObject.getAllData())
        recycler_view.layoutManager = LinearLayoutManager(this)
    }

    private fun configureBinding(){
        addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        textInputLayout.setEndIconOnClickListener {
            val imm: InputMethodManager =
                textInputText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(textInputText.windowToken, 0)
            }
            textInputLayout.clearFocus()
            val taskTitle =textInputText.text.toString()
            println("Input text: $taskTitle")
            // TODO add search tasks by title logic
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.your_tasks_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // TODO Update sharedPreferences here and trigger update of RecycleView to apply changes.
        when (item.itemId) {
            R.id.urgentFirstItem -> {
                item.isChecked = !item.isChecked
            }
            R.id.hideDoneItem -> {
                item.isChecked = !item.isChecked
            }
            R.id.categoryAll -> {

            }
            R.id.categoryHome -> {

            }
            R.id.categoryWork -> {

            }
            R.id.categoryHobby -> {

            }
            R.id.categoryOther -> {

            }
            R.id.notification5Minutes -> {

            }
            R.id.notification15Minutes -> {

            }
            R.id.notification30Minutes -> {

            }
            R.id.notification60Minutes -> {

            }
        }

        // TODO make sure you need to return the result of `super.` call.
        return super.onOptionsItemSelected(item)
    }
}
