package com.example.notaria4.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notaria4.R
import com.example.notaria4.database.DatabaseHelper

data class Reservation(
    val id: Long,
    val notary: String,
    val room: String,
    val date: String,
    val time: String,
    val description: String
)

class DashboardReservationAdapter(
    private var reservations: MutableList<Reservation>,
    private val dbHelper: DatabaseHelper
) : RecyclerView.Adapter<DashboardReservationAdapter.ReservationViewHolder>() {

    class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNotary: TextView = itemView.findViewById(R.id.textViewNotary)
        val textViewRoom: TextView = itemView.findViewById(R.id.textViewRoom)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_reservas, parent, false)
        return ReservationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.textViewNotary.text = reservation.notary
        holder.textViewRoom.text = reservation.room
        holder.textViewDate.text = reservation.date
        holder.textViewTime.text = reservation.time
        holder.textViewDescription.text = reservation.description

        holder.buttonDelete.setOnClickListener {
            deleteReservation(reservation.id)
            reservations.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, reservations.size)
        }
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    private fun deleteReservation(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_USERS, "${DatabaseHelper.COLUMN_ID} = ?", arrayOf(id.toString()))
    }
}