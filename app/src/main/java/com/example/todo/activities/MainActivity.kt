package com.example.todo.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.*
import com.example.todo.adapters.TasksAdapter
import com.example.todo.database.ToDoDatabase
import com.example.todo.database.entities.TaskEntity
import com.example.todo.repositories.TaskRepository
import com.example.todo.utils.SettingsHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// TODO
// 1. Implement the logic that will store a text from input fields, list of tasks and other data when orientation changes.
// 2. Update task card styling in MainActivity. Get rid of search box. Add search icon to menu instead?
//      At least for horizontal view.
// 3. Add notifications logic.

class MainActivity : ActivityBase() {
    private lateinit var taskRepository: TaskRepository
    private lateinit var settings: SettingsHandler
    private lateinit var recyclerAdapter: TasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskRepository = TaskRepository(ToDoDatabase.getDatabase(applicationContext).taskDao())
        settings = SettingsHandler(applicationContext)

        configureBinding()
        setRecycler()
    }

    private fun setRecycler() {
        recyclerAdapter = TasksAdapter()
        recycler_view.adapter = recyclerAdapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        updateTasksList(textInputText.text.toString())
    }

    private fun configureBinding() {

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
            updateTasksList(textInputText.text.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.your_tasks_menu, menu)
        val urgentFirst: MenuItem = menu!!.findItem(R.id.urgentFirstItem)
        val hideDone: MenuItem = menu.findItem(R.id.hideDoneItem)
        urgentFirst.isChecked = settings.getShowUrgentTasksFirst()
        hideDone.isChecked = settings.getHideDoneTasks()
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.urgentFirstItem -> {
                item.isChecked = !item.isChecked
                settings.setShowUrgentTasksFirst(item.isChecked)
                updateTasksList(textInputText.text.toString())
            }
            R.id.hideDoneItem -> {
                item.isChecked = !item.isChecked
                settings.setHideDoneTasks(item.isChecked)
                updateTasksList(textInputText.text.toString())
            }
            R.id.categoryAll -> {
                settings.setTasksCategory("All")
                updateTasksList(textInputText.text.toString())
            }
            R.id.categoryHome -> {
                settings.setTasksCategory("Home")
                updateTasksList(textInputText.text.toString())
            }
            R.id.categoryWork -> {
                settings.setTasksCategory("Work")
                updateTasksList(textInputText.text.toString())
            }
            R.id.categoryHobby -> {
                settings.setTasksCategory("Hobby")
                updateTasksList(textInputText.text.toString())
            }
            R.id.categoryOther -> {
                settings.setTasksCategory("Other")
                updateTasksList(textInputText.text.toString())
            }
            R.id.notification5Minutes -> {
                settings.setNotificationTime(5)
            }
            R.id.notification15Minutes -> {
                settings.setNotificationTime(15)
            }
            R.id.notification30Minutes -> {
                settings.setNotificationTime(30)
            }
            R.id.notification60Minutes -> {
                settings.setNotificationTime(60)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateTasksList(title: String) {
        var tasksList: List<TaskEntity>
        val ref = this
        lifecycleScope.launch(Dispatchers.IO) {
            if (settings.getHideDoneTasks()) {
                tasksList = if (settings.getTasksCategory() == "All") {
                    if (settings.getShowUrgentTasksFirst()) {
                        taskRepository.getActiveTasksOrderedByDueDate(title)
                    } else {
                        taskRepository.getActiveTasks(title)
                    }
                } else {
                    if (settings.getShowUrgentTasksFirst()) {
                        taskRepository.getActiveTasksByCategoryOrderedByDueDate(
                            title,
                            settings.getTasksCategory()
                        )
                    } else {
                        taskRepository.getActiveTasksByCategory(title, settings.getTasksCategory())
                    }
                }
            } else {
                tasksList = if (settings.getTasksCategory() == "All") {
                    if (settings.getShowUrgentTasksFirst()) {
                        taskRepository.getTasksOrderedByDueDate(title)
                    } else {
                        taskRepository.getTasks(title)
                    }
                } else {
                    if (settings.getShowUrgentTasksFirst()) {
                        taskRepository.getTasksByCategoryOrderedByDueDate(
                            title,
                            settings.getTasksCategory()
                        )
                    } else {
                        taskRepository.getTasksByCategory(title, settings.getTasksCategory())
                    }
                }
            }
            ref.runOnUiThread {
                recyclerAdapter.setData(tasksList)
            }
        }
    }
}
