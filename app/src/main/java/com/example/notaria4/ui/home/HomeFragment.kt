package com.example.notaria4.ui.home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.notaria4.R
import com.example.notaria4.database.DatabaseHelper
import com.example.notaria4.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var dbHelper: DatabaseHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbHelper = DatabaseHelper(requireContext())
        setupSpinners()

        binding.buttonSubmit.setOnClickListener {
            saveAppointment()
        }

        return root
    }

    private fun setupSpinners() {
        val months = (1..12).map { it.toString() }
        val years = (2025..2050).map { it.toString() }
        val hours = (9..18).map { it.toString().padStart(2, '0') }
        val minutes = listOf("00", "15", "30", "45")

        setupSpinner(binding.spinnerMonth, months)
        setupSpinner(binding.spinnerYear, years)
        setupSpinner(binding.spinnerHour, hours)
        setupSpinner(binding.spinnerMinute, minutes)

        updateDaySpinner(1)

        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMonth = position + 1
                updateDaySpinner(selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateDaySpinner(month: Int) {
        val days = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> (1..31).map { it.toString() }
            4, 6, 9, 11 -> (1..30).map { it.toString() }
            2 -> (1..28).map { it.toString() }
            else -> (1..31).map { it.toString() }
        }
        setupSpinner(binding.spinnerDay, days)
    }

    private fun setupSpinner(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun saveAppointment() {
        val notary = binding.spinnerNotary.selectedItem.toString()
        val room = binding.spinnerRoom.selectedItem.toString()
        val month = binding.spinnerMonth.selectedItem.toString()
        val year = binding.spinnerYear.selectedItem.toString()
        val day = binding.spinnerDay.selectedItem.toString()
        val hour = binding.spinnerHour.selectedItem.toString()
        val minute = binding.spinnerMinute.selectedItem.toString()
        val date = "$year-$month-$day"
        val time = "$hour:$minute"
        val description = binding.editTextDescription.text.toString()

        val db = dbHelper?.readableDatabase
        val cursor = db?.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COLUMN_ID),
            "${DatabaseHelper.COLUMN_DATE} = ? AND ${DatabaseHelper.COLUMN_TIME} = ?",
            arrayOf(date, time),
            null, null, null
        )

        if (cursor != null && cursor.count > 0) {
            Toast.makeText(requireContext(), "Ya existe una cita a esta hora", Toast.LENGTH_SHORT).show()
            cursor.close()
            return
        }
        cursor?.close()

        val writableDb = dbHelper?.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, notary)
            put(DatabaseHelper.COLUMN_EMAIL, room)
            put(DatabaseHelper.COLUMN_DATE, date)
            put(DatabaseHelper.COLUMN_TIME, time)
            put(DatabaseHelper.COLUMN_DESCRIPTION, description)
        }

        val newRowId = writableDb?.insert(DatabaseHelper.TABLE_USERS, null, values)
        if (newRowId != -1L) {
            Toast.makeText(requireContext(), "Cita guardada", Toast.LENGTH_SHORT).show()
            sendNotification("Cita guardada", "La cita con $notary en la sala $room ha sido guardada.")
            saveNotification("Cita guardada", "La cita con $notary en la sala $room ha sido guardada.")
        } else {
            Toast.makeText(requireContext(), "Error al guardar la cita", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveNotification(title: String, content: String) {
        val db = dbHelper?.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, title)
            put(DatabaseHelper.COLUMN_CONTENT, content)
        }
        db?.insert(DatabaseHelper.TABLE_NOTIFICATIONS, null, values)
    }

    private fun sendNotification(title: String, content: String) {
        val channelId = "appointment_channel"
        val channelName = "Appointment Notifications"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for appointment notifications"
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(notificationId, builder.build())
        }
    }

}