package com.example.notaria4.ui.home

import android.Manifest
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.notaria4.MainActivity
import com.example.notaria4.R
import com.example.notaria4.database.DatabaseHelper
import com.example.notaria4.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbHelper = DatabaseHelper(requireContext())

        val spinnerNotary: Spinner = binding.spinnerNotary
        val spinnerRoom: Spinner = binding.spinnerRoom
        val editTextDate: EditText = binding.editTextDate
        val editTextTime: EditText = binding.editTextTime
        val editTextDescription: EditText = binding.editTextDescription
        val buttonSubmit: Button = binding.buttonSubmit

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        buttonSubmit.setOnClickListener {
            val notary = spinnerNotary.selectedItem.toString()
            val room = spinnerRoom.selectedItem.toString()
            val date = editTextDate.text.toString()
            val time = editTextTime.text.toString()
            val description = editTextDescription.text.toString()
            if (notary.isNotEmpty() && room.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && description.isNotEmpty()) {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_NAME, notary)
                    put(DatabaseHelper.COLUMN_EMAIL, room)
                    put("date", date)
                    put("time", time)
                    put("description", description)
                }
                val newRowId = db.insert(DatabaseHelper.TABLE_USERS, null, values)
                if (newRowId != -1L) {
                    Toast.makeText(context, "Data saved successfully", Toast.LENGTH_SHORT).show()
                    sendNotification(notary, room, date, time, description)
                } else {
                    Toast.makeText(context, "Error saving data", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    private fun sendNotification(notary: String, room: String, date: String, time: String, description: String) {
        val title = "New Appointment Added"
        val content = "Notary: $notary, Room: $room, Date: $date, Time: $time, Description: $description"

        // Save notification to database
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, title)
            put(DatabaseHelper.COLUMN_CONTENT, content)
        }
        db.insert(DatabaseHelper.TABLE_NOTIFICATIONS, null, values)

        // Build and send notification
        val builder = NotificationCompat.Builder(requireContext(), MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_home_black_24dp)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(0, builder.build())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}