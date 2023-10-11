package com.example.notessafe.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.notessafe.DataModle.NotesDataModel
import com.example.notessafe.R

class NotesAdapter(
    val requireContext: Context,
    val requestedAppointment: ArrayList<NotesDataModel>,
    val parentFragment: Fragment
) :
    RecyclerView.Adapter<NotesAdapter.RequestedAppointmentHolder>()
    {

        inner class RequestedAppointmentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val notesHeader = itemView.findViewById<TextView>(R.id.notesHeader)
            val notes = itemView.findViewById<TextView>(R.id.notes)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): NotesAdapter.RequestedAppointmentHolder {
            return RequestedAppointmentHolder(
                LayoutInflater.from(requireContext)
                    .inflate(R.layout.layout_notes, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RequestedAppointmentHolder, position: Int) {

            val appointment = requestedAppointment[position]

            holder.notesHeader.text = appointment.title
            holder.notes.text = appointment.notes

            holder.itemView.setOnClickListener {

                val nTitle = appointment.title
                val nFilter = appointment.filter
                val nNotes = appointment.notes

                val bundle = Bundle()
                bundle.putString("nTitle", nTitle)
                bundle.putString("nFilter", nFilter)
                bundle.putString("nNotes", nNotes)
                bundle.putString("checker", "true")

                NavHostFragment.findNavController(parentFragment)
                    .navigate(R.id.action_homeFragment_to_editNotesScreen, bundle)

            }

            holder.itemView.setOnLongClickListener {

                Toast.makeText(requireContext, "long pressed", Toast.LENGTH_SHORT).show()

                true
            }

        }

        override fun getItemCount(): Int {
            return requestedAppointment.size
        }
    }