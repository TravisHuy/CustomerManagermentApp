package com.nhathuy.customermanagementapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.customermanagementapp.adapter.AppointmentAdapter
import com.nhathuy.customermanagementapp.databinding.FragmentAppointmentBinding
import com.nhathuy.customermanagementapp.viewmodel.AppointmentViewModel
import com.nhathuy.customermanagementapp.viewmodel.CustomerViewModel


class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!


    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var appointmentAdapter: AppointmentAdapter
    private lateinit var customerViewModel: CustomerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appointmentViewModel= ViewModelProvider(this).get(AppointmentViewModel::class.java)
        customerViewModel=ViewModelProvider(this).get(CustomerViewModel::class.java)
        setupRecylerView()
        observerViewModel()
    }
    private fun observerViewModel() {
        appointmentViewModel.getAllAppointment().observe(viewLifecycleOwner,{
                appointments -> appointments?.let {
            appointmentAdapter.setData(it)
            }
        })
    }

    private fun setupRecylerView() {
        appointmentAdapter= AppointmentAdapter(requireContext(), emptyList(),customerViewModel)
        binding.recAppointment.apply {
            layoutManager= LinearLayoutManager(requireContext())
            adapter=appointmentAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        appointmentViewModel.getAllAppointment()
    }
}