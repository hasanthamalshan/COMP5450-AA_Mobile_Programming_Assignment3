package com.example.todolistapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class TodoListActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var buttonBack: Button

    private lateinit var adapter: ArrayAdapter<String>
    private var itemList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        listView = findViewById(R.id.listView)
        buttonBack = findViewById(R.id.buttonBack)

        itemList = intent.getStringArrayListExtra("ITEM_LIST") ?: arrayListOf()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = itemList[position]
            itemList.removeAt(position)
            adapter.notifyDataSetChanged()
            // Send a notification for deleting an item
            sendNotification("Deleted: $item")
        }

        buttonBack.setOnClickListener {
            finish()
        }

        createNotificationChannel() // Call this once, typically in onCreate or onResume
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TodoChannel"
            val descriptionText = "Channel for to-do notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("TODO_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(message: String) {
        val builder = NotificationCompat.Builder(this, "TODO_CHANNEL")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("To-Do List Update")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify((0..1000).random(), builder.build())  // Generate a random ID to avoid collision
        }
    }
}

