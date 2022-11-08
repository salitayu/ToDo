package com.example.todo

data class Task(
    val id: Int,
    val task: String,
    val due: String,
    val done: Boolean,
    val category: String
)