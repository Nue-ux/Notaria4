package com.example.notaria4.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.notaria4.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupSpinners()

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

        // Inicializar el Spinner de días con el mes actual
        updateDaySpinner(1)

        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMonth = position + 1 // Los meses están indexados desde 1
                updateDaySpinner(selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }

    private fun updateDaySpinner(month: Int) {
        val days = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> (1..31).map { it.toString() }
            4, 6, 9, 11 -> (1..30).map { it.toString() }
            2 -> (1..28).map { it.toString() } // Para simplificar, asumimos que febrero siempre tiene 28 días
            else -> (1..31).map { it.toString() }
        }
        setupSpinner(binding.spinnerDay, days)
    }

    private fun setupSpinner(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
