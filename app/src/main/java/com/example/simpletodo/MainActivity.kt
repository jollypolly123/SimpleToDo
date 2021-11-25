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
import com.google.android.material.snackbar.Snackbar
import java.io.File
import org.apache.commons.io.FileUtils
import java.io.IOException
import java.nio.charset.Charset

import android.view.LayoutInflater
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.widget.*
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import java.text.SimpleDateFormat
import java.util.*
import androidx.recyclerview.widget.GridLayoutManager




class MainActivity : AppCompatActivity() {

    var listOfTasks = mutableListOf<String>()
    lateinit var adapter: TaskItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var taskItemDate = ""

        // Set a click listener for date widget
        findViewById<ImageButton>(R.id.dateBtn).setOnClickListener{
            // Initialize a new layout inflater instance
            val inflater:LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.date_editor, null)

            // Initialize a new instance of popup window
            val popupWindow = PopupWindow(
                    view, // Custom view to show in popup window
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                    LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )
            popupWindow.elevation = 10.0F

            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            // Slide animation for popup window exit transition
            val slideOut = Slide()
            slideOut.slideEdge = Gravity.TOP
            popupWindow.exitTransition = slideOut

            // Get the widgets reference from custom view
            val buttonPopup = view.findViewById<Button>(R.id.button_popup)
            val dateSelect = view.findViewById<DatePicker>(R.id.datePicker)

            // Set a click listener for popup's button widget
            buttonPopup.setOnClickListener{
                val day: Int = dateSelect.dayOfMonth
                val month: Int = dateSelect.month
                val year: Int = dateSelect.year

                val calendar: Calendar = Calendar.getInstance()
                calendar.set(year, month, day)

                taskItemDate = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(calendar.time).toString()

                // Dismiss the popup window
                popupWindow.dismiss()
            }

            // Set a dismiss listener for popup window
            popupWindow.setOnDismissListener {
                // notify user that date has been added
                val mySnackbar = Snackbar.make(findViewById(R.id.mainActivity),
                    "Date recorded",
                    Snackbar.LENGTH_SHORT)
                mySnackbar.anchorView = findViewById(R.id.button)
                mySnackbar.show()
            }

            // Finally, show the popup window on app
            TransitionManager.beginDelayedTransition(findViewById(R.id.mainActivity))
            popupWindow.showAtLocation(
                    findViewById(R.id.mainActivity), // Location to display popup window
                    Gravity.CENTER, // Exact position of layout to display popup
                    0, // X offset
                    0 // Y offset
            )
        }

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

                val editingTask = 1
                val intent = Intent(this@MainActivity, EditTask::class.java)
                intent.putExtra("Task", editText)
                intent.putExtra("Position", position)

                startActivityForResult(intent, editingTask)
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
        val descField = findViewById<EditText>(R.id.taskDescField)
        // Detect when user clicks on Add button
        findViewById<Button>(R.id.button).setOnClickListener {
            // get text that user inputs into addTaskField
            val userInput = inputField.text.toString()
            val userDesc = descField.text.toString()

            val taskItem = "$userInput |~| $taskItemDate |~| $userDesc"

            // add string to list of tasks listOfTasks
            listOfTasks.add(taskItem)
            // notify adapter that data changed
            adapter.notifyItemInserted(listOfTasks.size - 1)

            // notify user that item has been added
            val mySnackbar = Snackbar.make(findViewById(R.id.mainActivity),
                    "Task added: $userInput",
                    Snackbar.LENGTH_SHORT)
            mySnackbar.anchorView = findViewById(R.id.button)
            mySnackbar.show()

            // reset text field
            inputField.setText("")
            taskItemDate = ""
            descField.setText("")

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
    private fun getDataFile() : File {
        return File(filesDir, "data.txt")
    }

    // load items by reading every line in data file
    private fun loadItems() {
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