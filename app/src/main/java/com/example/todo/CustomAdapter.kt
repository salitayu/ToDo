package com.example.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.widget.ArrayAdapter
import java.util.*

internal class CustomAdapter(private var context: Context, private var itemsList: List<Task>, private var db: DBHelper) :
        RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
            internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                var idView: TextView? = view.findViewById(R.id.idView)
                var itemTaskView: TextView? = view.findViewById(R.id.itemTaskView)
                var itemDueView: TextView? = view.findViewById(R.id.itemDueView)
                var itemDoneView: TextView? = view.findViewById(R.id.itemDoneView)
                var itemLayout: View? = view.findViewById(R.id.itemLayout)

                init {
                    view.setOnClickListener {
                        val builder = AlertDialog.Builder(context)
                        val dialogLayout =
                            LayoutInflater.from(context).inflate(R.layout.todo_form, null)
                        val taskInput = dialogLayout.findViewById<EditText>(R.id.taskInput)
                        val taskInputValue = view.findViewById<TextView>(R.id.itemTaskView).text.toString()
                        taskInput.setText(taskInputValue)
                        val dateInput = dialogLayout.findViewById<EditText>(R.id.dateInput)
                        dateInput.setText(view.findViewById<TextView>(R.id.itemDueView).text.toString())
                        val doneInput = dialogLayout.findViewById<CheckBox>(R.id.doneInput)
                        var check = false
                        if (view.findViewById<TextView>(R.id.itemDoneView).text.toString() == "Yes") {
                            check = true
                        }
                        doneInput.isChecked = check
                        dateInput.setOnClickListener {
                            pickDateTime(dateInput)
                        }
                        val categoryInput = dialogLayout.findViewById<Spinner>(R.id.categoryInput)
                        val list = context.resources.getStringArray(R.array.categories)
                        val adapter = ArrayAdapter(context, R.layout.spinner_item, list)
                        categoryInput.adapter = adapter
                        val id = Integer.parseInt(idView!!.text.toString())

                        with(builder) {
                            setTitle("Edit Task")
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
                                        .setPositiveButton(
                                            "OK"
                                        ) { dialog, which ->
                                            dialog.dismiss()
                                        }
                                        .setNegativeButton("Cancel", null)
                                        .show()
                                } else {
                                    val taskToEdit = Task(id, task, date, done, category)
                                    db.editTaskById(id, taskToEdit)
                                    itemTaskView!!.setText(task)
                                    itemDueView!!.setText(date)
                                    var color = "#ffffff"
                                    if (category == "Health") {
                                        color = "#c5f55d"
                                    } else if (category == "Work") {
                                        color = "#f0f8ff"
                                    } else if (category == "Leisure") {
                                        color = "#00e5ee"
                                    } else if (category == "Learning") {
                                        color = "#e6e6fa"
                                    } else if (category == "Groceries") {
                                        color = "#fad505"
                                    } else if (category == "Appointments") {
                                        color = "#ff9203"
                                    } else {
                                        color = "#f0f8ff"
                                    }
                                    itemLayout!!.setBackgroundColor(Color.parseColor(color))
                                    if (done) {
                                        itemDoneView!!.text = "Yes"
                                    } else {
                                        itemDoneView!!.text = "No"
                                    }
                                }
                            }
                            setNegativeButton("Delete Task") { dialog, which ->
                                Log.d("Main", "Delete Task Clicked")
                                db.deleteTaskById(id)
                                itemsList.filter{ i -> i.id == id }
                                itemLayout!!.visibility = GONE
                            }
                            setView(dialogLayout)
                            show()
                        }
                    }
                }
            }

            private fun pickDateTime(dateInput: EditText) {
                val currentDateTime = Calendar.getInstance()
                val startYear = currentDateTime.get(Calendar.YEAR)
                val startMonth = currentDateTime.get(Calendar.MONTH)
                val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
                val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
                val startMinute = currentDateTime.get(Calendar.MINUTE)

                DatePickerDialog(context, { _, year, month, day ->
                    TimePickerDialog(context, TimePickerDialog.THEME_HOLO_LIGHT, { _, hour, minute ->
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(year, month, day, hour, minute)
                        val datetime = year.toString() + "-" + month.toString() + "-" + day + " " + hour + ":" + minute
                        dateInput.setText(datetime)
                    }, startHour, startMinute, false).show()
                }, startYear, startMonth, startDay).show()
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
                return MyViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: MyViewHolder, position:Int) {
                    val item = itemsList[position]
                    holder.idView!!.text = item.id.toString()
                    holder.itemTaskView!!.text = item.task
                    holder.itemDueView!!.text = item.due
                    var color = "#ffffff"
                    val itemCategory = item.category
                    if (itemCategory == "Health") {
                        color = "#c5f55d"
                    } else if (itemCategory == "Work") {
                        color = "#f0f8ff"
                    } else if (itemCategory == "Leisure") {
                        color = "#00e5ee"
                    } else if (itemCategory == "Learning") {
                        color = "#e6e6fa"
                    } else if (itemCategory == "Groceries") {
                        color = "#fad505"
                    } else if (itemCategory == "Appointments") {
                        color = "#ff9203"
                    } else {
                        color = "#f0f8ff"
                    }
                    holder.itemLayout!!.setBackgroundColor(Color.parseColor(color))
                    var done = "No"
                    if (item.done) {
                        done = "Yes"
                    }
                    holder.itemDoneView!!.text = done
            }

            override fun getItemCount(): Int {
                return itemsList.size
            }

            override fun getItemViewType(position: Int): Int {
                return R.layout.item
            }
        }
