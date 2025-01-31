package com.example.notaria4.ui.dashboard

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notaria4.database.DatabaseHelper
import com.example.notaria4.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var dashboardReservationAdapter: DashboardReservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbHelper = DatabaseHelper(requireContext())

        val textView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        dashboardReservationAdapter = DashboardReservationAdapter(getAllReservations(), dbHelper)
        recyclerView.adapter = dashboardReservationAdapter

        return root
    }

    private fun getAllReservations(): MutableList<Reservation> {
        val reservationList = mutableListOf<Reservation>()
        val db = dbHelper.readableDatabase

        val cursor: Cursor? = try {
            db.query(
                DatabaseHelper.TABLE_USERS,
                arrayOf(DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_EMAIL, DatabaseHelper.COLUMN_DATE, DatabaseHelper.COLUMN_TIME, DatabaseHelper.COLUMN_DESCRIPTION),
                null, null, null, null, "${DatabaseHelper.COLUMN_DATE} ASC"
            )
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error querying database", e)
            null
        }

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val notary = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val room = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL))
                val date = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE))
                val time = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME))
                val description = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
                reservationList.add(Reservation(id, notary, room, date, time, description))
            }
        } ?: run {
            Log.e("DashboardFragment", "Error: Cursor is null")
            Toast.makeText(requireContext(), "Error loading reservations", Toast.LENGTH_SHORT).show()
        }
        return reservationList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}