package com.example.simpletodo

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import org.apache.commons.io.FileUtils
import java.io.IOException
import java.nio.charset.Charset
import java.util.Date


class MainActivity : AppCompatActivity() {

    var listOfTasks = mutableListOf<String>()
    lateinit var adapter: TaskItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val onLongClickListener = object : TaskItemAdapter.OnLongClickListener {
            override fun onItemLongClicked(position: Int) {
                // remove item from list
                listOfTasks.removeAt(position)
                // notify adapter that data set changed
                adapter.notifyItemRemoved(position)

                saveItems()
            }
        }

        val editTaskListener = object : TaskItemAdapter.EditTaskListener {
            override fun editTask(position: Int) {
                // remove item from list
                val editText = listOfTasks.elementAt(position)
                Log.i("TaskItemAdapter", "Item $editText")

                val LAUNCH_SECOND_ACTIVITY = 1
                val intent = Intent(this@MainActivity, EditTask::class.java)
                intent.putExtra("Task", editText)
                intent.putExtra("Position", position)

                startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY)

            }
        }

        loadItems()

        // look up recyclerview in layout
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // create adapter passing in sample user data
        adapter = TaskItemAdapter(listOfTasks, onLongClickListener, editTaskListener)

        // attach adapter to recyclerview to populate items
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val inputField = findViewById<EditText>(R.id.addTaskField)

        // Detect when user clicks on Add button
        findViewById<Button>(R.id.button).setOnClickListener {
            // get text that user inputs into addTaskField
            val userInput = inputField.text.toString()

            // add string to list of tasks listOfTasks
            listOfTasks.add(userInput)
            // notify adapter that data changed
            adapter.notifyItemInserted(listOfTasks.size - 1)

            // reset text field
            inputField.setText("")

            saveItems()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val task: String = data!!.getStringExtra("Task").toString()
            val pos: Int = data.getIntExtra("Position", 0)
            Log.i("onActivityResult", "Item $task $pos")
            // update list
            listOfTasks[pos] = task

            // notify adapter that data set changed
            adapter.notifyItemChanged(pos)

            saveItems()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    // save data that user input
    // save data by writing and reading from file

    // get needed file
    fun getDataFile() : File {
        return File(filesDir, "data.txt")
    }

    // load items by reading every line in data file
    fun loadItems() {
        try {
            listOfTasks = FileUtils.readLines(getDataFile(), Charset.defaultCharset())
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    // save items by writing into data file
    fun saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), listOfTasks)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }
}