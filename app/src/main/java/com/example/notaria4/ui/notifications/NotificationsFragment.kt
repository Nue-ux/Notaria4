package com.example.notaria4.ui.notifications

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notaria4.database.DatabaseHelper
import com.example.notaria4.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private var dbHelper: DatabaseHelper? = null
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbHelper = DatabaseHelper(requireContext())

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 2) // 2 columnas
        notificationAdapter = NotificationAdapter(getAllNotifications())
        recyclerView.adapter = notificationAdapter

        return root
    }

    private fun getAllNotifications(): List<Notification> {
        val notificationList = mutableListOf<Notification>()
        val db = dbHelper?.readableDatabase ?: return notificationList // Check if dbHelper is null

        val cursor: Cursor? = try {
            db.query(
                DatabaseHelper.TABLE_NOTIFICATIONS,
                arrayOf(DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_CONTENT),
                null, null, null, null, "${DatabaseHelper.COLUMN_TITLE} ASC"
            )
        } catch (e: Exception) {
            Log.e("NotificationsFragment", "Error querying database", e)
            null
        }

        cursor?.use {
            while (it.moveToNext()) {
                val title = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE))
                val content = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT))
                notificationList.add(Notification(title, content))
            }
        } ?: run {
            Log.e("NotificationsFragment", "Error: Cursor is null")
            Toast.makeText(requireContext(), "Error loading notifications", Toast.LENGTH_SHORT).show()
        }
        return notificationList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
