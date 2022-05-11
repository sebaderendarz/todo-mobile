package com.example.todo.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.*
import com.example.todo.adapters.TasksAdapter
import com.example.todo.database.ToDoDatabase
import com.example.todo.repositories.TaskRepository
import kotlinx.android.synthetic.main.activity_main.*


// TODO
// 1. Add sharedPreferences logic and update of tasks list after click on menu.
// 2. Implement the logic that will store a text from input fields when orientation changes.
// 3. Update task card styling in MainActivity.

class MainActivity : ActivityBase() {
    private lateinit var taskRepository: TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskRepository = TaskRepository(ToDoDatabase.getDatabase(applicationContext).taskDao())

        setRecycler()
        configureBinding()
    }

    private fun setRecycler() {
        val adapter = TasksAdapter()
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        taskRepository.getAllTasks.observe(this, Observer { tasks ->
            adapter.setData(tasks)
        })
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
            val taskTitle = textInputText.text.toString()
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
