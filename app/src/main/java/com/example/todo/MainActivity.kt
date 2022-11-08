package com.example.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var customAdapter: CustomAdapter
    private val itemsList = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val db = DBHelper(this, null)
        customAdapter = CustomAdapter(this, itemsList, db)
        prepareItemsFirst(db)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter

        binding.fab.setOnClickListener { view ->
            createTodoForm()
        }
    }

    fun createTodoForm() {
        val db = DBHelper(this, null)
        val builder = AlertDialog.Builder(this)
        val dialogLayout = layoutInflater.inflate(R.layout.todo_form, null)
        val taskInput = dialogLayout.findViewById<EditText>(R.id.taskInput)
        val dateInput = dialogLayout.findViewById<EditText>(R.id.dateInput)
        val doneInput = dialogLayout.findViewById<CheckBox>(R.id.doneInput)
        dateInput.setOnClickListener {
            pickDateTime(dateInput)
        }
        val categoryInput = dialogLayout.findViewById<Spinner>(R.id.categoryInput)
        val list = getResources().getStringArray(R.array.categories)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, list)
        categoryInput.adapter = adapter

        with(builder) {
            setTitle("Add a Task")
            setPositiveButton("OK") { dialog, which ->
                Log.d("Main", "OK Clicked")
                val task = taskInput.text.toString()
                val date = dateInput.text.toString()
                val done = doneInput.isChecked
                val category = categoryInput.getSelectedItem().toString()
                if (task == "" || date == "" || category == "") {
                    AlertDialog.Builder(context)
                        .setTitle("Please complete all sections")
                        .setMessage("Enter your task, due date, status, and category.")
                        .setPositiveButton("OK"
                        ) { dialog, which ->
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                } else {
                    db.addTask(task, date, done, category)
                    prepareItems(db)
                }
            }
            setNegativeButton("Cancel") { dialog, which ->
                Log.d("Main", "Cancel Clicked")
            }
            setView(dialogLayout)
            show()
        }
    }

    private fun prepareItemsFirst(db: DBHelper) {
        val cursor = db.getTasks()
        if (cursor!!.moveToFirst()) {
            while(!cursor.isAfterLast()) {
                val idColIndex = cursor.getColumnIndex(DBHelper.ID_COL)
                val taskColIndex = cursor.getColumnIndex(DBHelper.TASK_COL)
                val dueColIndex = cursor.getColumnIndex(DBHelper.DUE_COL)
                val doneColIndex = cursor.getColumnIndex(DBHelper.DONE_COL)
                val categoryColIndex = cursor.getColumnIndex(DBHelper.CATEGORY_COL)
                if (idColIndex > -1 && taskColIndex > -1 && dueColIndex > -1 && doneColIndex > -1 && categoryColIndex > -1) {
                    val id = cursor.getInt(idColIndex)
                    val task = cursor.getString(taskColIndex)
                    val due = cursor.getString(dueColIndex)
                    val done = cursor.getString(doneColIndex)
                    var isDone = false
                    if (done.equals("1")) {
                        isDone = true
                    }
                    val category = cursor.getString(categoryColIndex)
                    val newTask = Task(id, task, due, isDone, category)
                    itemsList.add(newTask)
                    cursor.moveToNext()
                }
            }
            cursor.close()
        } else {
            itemsList.clear()
            cursor.close()
        }
        customAdapter.notifyDataSetChanged()
    }

    private fun prepareItems(db: DBHelper) {
        val cursor = db.getTasks()
        if (cursor!!.moveToLast()) {
            while(!cursor.isAfterLast()) {
                val idColIndex = cursor.getColumnIndex(DBHelper.ID_COL)
                val taskColIndex = cursor.getColumnIndex(DBHelper.TASK_COL)
                val dueColIndex = cursor.getColumnIndex(DBHelper.DUE_COL)
                val doneColIndex = cursor.getColumnIndex(DBHelper.DONE_COL)
                val categoryColIndex = cursor.getColumnIndex(DBHelper.CATEGORY_COL)
                if (idColIndex > -1 && taskColIndex > -1 && dueColIndex > -1 && doneColIndex > -1 && categoryColIndex > -1) {
                    val id = cursor.getInt(idColIndex)
                    val task = cursor.getString(taskColIndex)
                    val due = cursor.getString(dueColIndex)
                    val done = cursor.getString(doneColIndex)
                    var isDone = false
                    if (done.equals("1")) {
                        isDone = true
                    }
                    val category = cursor.getString(categoryColIndex)
                    val newTask = Task(id, task, due, isDone, category)
                    itemsList.add(newTask)
                    cursor.moveToNext()
                }
            }
            cursor.close()
        } else {
            itemsList.clear()
            cursor.close()
        }
        customAdapter.notifyDataSetChanged()
    }

    private fun pickDateTime(dateInput: EditText) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(this, { _, year, month, day ->
            TimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                val datetime = year.toString() + "-" + month.toString() + "-" + day + " " + hour + ":" + minute
                dateInput.setText(datetime)
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.clearAll -> onClearAllSelected()
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onClearAllSelected(): Boolean {
        val db = DBHelper(this, null)
        db.removeAllTasks()
        prepareItems(db)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}