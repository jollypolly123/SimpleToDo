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

class MainActivity : AppCompatActivity() {

    var listOfTasks = mutableListOf<String>() // mutableListOf<MutableList<String>>()
    var taskObject = mutableListOf<String>()
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
            val view = inflater.inflate(R.layout.date_editor,null)

            // Initialize a new instance of popup window
            val popupWindow = PopupWindow(
                    view, // Custom view to show in popup window
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                    LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            // Set an elevation for the popup window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 10.0F
            }


            // If API level 23 or higher then execute the code
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // Create a new slide animation for popup window enter transition
                val slideIn = Slide()
                slideIn.slideEdge = Gravity.TOP
                popupWindow.enterTransition = slideIn

                // Slide animation for popup window exit transition
                val slideOut = Slide()
                slideOut.slideEdge = Gravity.RIGHT
                popupWindow.exitTransition = slideOut

            }

            // Get the widgets reference from custom view
            val buttonPopup = view.findViewById<Button>(R.id.button_popup)
            val dateSelect = view.findViewById<DatePicker>(R.id.datePicker)

            // Set a click listener for popup's button widget
            buttonPopup.setOnClickListener{
                // Dismiss the popup window
//                val userInput = dateSelect
                val day: Int = dateSelect.dayOfMonth
                val month: Int = dateSelect.month
                val year: Int = dateSelect.year

                val calendar: Calendar = Calendar.getInstance()
                calendar.set(year, month, day)

                taskItemDate = SimpleDateFormat("dd/MM/yyyy").format(calendar.time).toString()

                Log.i("buttonPopup", "Item $taskItemDate")

                popupWindow.dismiss()
            }

            // Set a dismiss listener for popup window
            popupWindow.setOnDismissListener {
                // notify user that date has been added
                val mySnackbar = Snackbar.make(findViewById(R.id.mainActivity),
                    "Date recorded",
                    Snackbar.LENGTH_SHORT)
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
//        val dateField = findViewById<EditText>(R.id.editTextDate)

        // Detect when user clicks on Add button
        findViewById<Button>(R.id.button).setOnClickListener {
            // get text that user inputs into addTaskField
            val userInput = inputField.text.toString()

            val taskItem = mutableListOf(userInput, taskItemDate)
            Log.i("add", "Item $taskItem")

            // add string to list of tasks listOfTasks
            listOfTasks.add(userInput)
            // notify adapter that data changed
            adapter.notifyItemInserted(listOfTasks.size - 1)

            // notify user that item has been added
            val mySnackbar = Snackbar.make(findViewById(R.id.mainActivity),
                    "Task added: $userInput",
                    Snackbar.LENGTH_SHORT)
            mySnackbar.show()

            // reset text field
            inputField.setText("")
            taskItemDate = ""

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
            Log.i("ok", FileUtils.readLines(getDataFile(), Charset.defaultCharset()).toString())
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }
}