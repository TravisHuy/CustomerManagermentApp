package com.nhathuy.nextmeet.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.nhathuy.nextmeet.R
import com.nhathuy.nextmeet.databinding.FragmentTimeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TimeFragment : Fragment() {

    private var _binding: FragmentTimeBinding ?= null
    val binding get() = _binding!!


    private val calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private lateinit var dayAdapter: ArrayAdapter<String>
    private lateinit var hourAdapter: ArrayAdapter<String>
    private lateinit var repeatAdapter: ArrayAdapter<String>

    private val dayOptions = mutableListOf<String>()
    private val hourOptions = mutableListOf<String>()
    private val repeatOptions = mutableListOf<String>()

    private var repeatInterval: Int = 0
    private var repeatUnit: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentTimeBinding.inflate(inflater,container,false)
        val view=binding.root


        setupSpinners()
        setupSpinnerListeners()

        return view
    }


    // get selectdatetime
    fun getSelectDateTime() : Pair<String ,String>{
        return Pair(dateFormat.format(selectedTime.time),
            timeFormat.format(selectedTime.time))
    }
    // getRepeateInfo
    fun getRepeatInfo():Pair<Int,String>{
        return Pair(repeatInterval,repeatUnit)
    }
    private fun setupSpinners() {
        setupDaySpinner()
        setupHourSpinner()
        setupRepeatSpinner()
    }

    private fun setupRepeatSpinner() {
        repeatOptions.addAll(resources.getStringArray(R.array.repeat_options))
        repeatAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, repeatOptions)
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAgain.adapter = repeatAdapter
    }

    private fun setupHourSpinner() {
        hourOptions.addAll(resources.getStringArray(R.array.hour_options))
        hourAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hourOptions)
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHour.adapter = hourAdapter
    }

    private fun setupDaySpinner() {
        dayOptions.addAll(resources.getStringArray(R.array.day_options))
        dayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dayOptions)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDay.adapter = dayAdapter
    }

    private fun setupSpinnerListeners() {
        binding.spinnerDay.onItemSelectedListener= object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                when(position){
                    0-> {
                        selectedTime= Calendar.getInstance()
                        updateHourOptionsForToday()
                    }
                    1-> {
                        selectedTime =Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR,1)
                        }
                        resetHourOptions()
                    }
                    2 -> {
                        selectedTime =Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR,7)
                        }
                        resetHourOptions()
                    }
                    3 ->  showDatePickerDialog()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
        binding.spinnerHour.onItemSelectedListener= object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                binding.errorTime.visibility = View.GONE
                when{
                    hourOptions[position] == "Morning (8:00)" -> selectedTime.set(Calendar.HOUR_OF_DAY, 8)
                    hourOptions[position] == "Afternoon (13:00)" -> selectedTime.set(Calendar.HOUR_OF_DAY, 13)
                    hourOptions[position] == "Evening (18:00)" -> selectedTime.set(Calendar.HOUR_OF_DAY, 18)
                    hourOptions[position] == "Night (20:00)" -> selectedTime.set(Calendar.HOUR_OF_DAY, 20)
                    hourOptions[position] == "Pick an Hour..." -> showTimePickerDialog()
                    else ->{
                        val customTime= hourOptions[position]
                        val timeParts= customTime.split(":")
                        if(timeParts.size==2){
                            selectedTime.set(Calendar.HOUR_OF_DAY,timeParts[0].toInt())
                            selectedTime.set(Calendar.MINUTE,timeParts[1].toInt())
                        }
                    }
                }
                checkIfSelectedTimeHasPassed()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        binding.spinnerAgain.onItemSelectedListener= object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                when(position){
                    0->{
                        repeatInterval=0
                        repeatUnit=""
                    }
                    1 -> {
                        repeatInterval=1
                        repeatUnit="day"
                    }
                    2 -> {
                        repeatInterval=1
                        repeatUnit="week"
                    }
                    3 -> {
                        repeatInterval=1
                        repeatUnit="month"
                    }
                    4 -> {
                        repeatInterval=1
                        repeatUnit="year"
                    }
                    5->  showCustomerRepeatDialog()
                }


                if(position!=5){
                    updateRepeatSpinnerWithNextDate()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun checkIfSelectedTimeHasPassed() {
        val currentTime = Calendar.getInstance()
        if (selectedTime.before(currentTime)) {
            binding.errorTime.visibility = View.VISIBLE
        } else {
            binding.errorTime.visibility = View.GONE
        }
    }

    private fun showDatePickerDialog() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(today)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setStart(today)
                    .setValidator(DateValidatorPointForward.from(today))
                    .build()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Calendar.getInstance()
            selectedDate.timeInMillis = selection
            selectedTime.set(
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            updateSpinnerWithCustomDate()
            resetHourOptions()
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun showTimePickerDialog() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select time")
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            selectedTime.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            selectedTime.set(Calendar.MINUTE, timePicker.minute)
            updateSpinnerWithCustomTime()
        }

        timePicker.show(parentFragmentManager, "TIME_PICKER")
    }

    private fun updateHourOptionsForToday() {
        val currentTime = Calendar.getInstance()
        hourOptions.clear()

        if (currentTime.get(Calendar.HOUR_OF_DAY) < 8) {
            hourOptions.add("Morning (8:00)")
        }
        if (currentTime.get(Calendar.HOUR_OF_DAY) < 13) {
            hourOptions.add("Afternoon (13:00)")
        }
        if (currentTime.get(Calendar.HOUR_OF_DAY) < 18) {
            hourOptions.add("Evening (18:00)")
        }
        if (currentTime.get(Calendar.HOUR_OF_DAY) < 20) {
            hourOptions.add("Night (20:00)")
        }
        hourOptions.add("Pick an Hour...")

        hourAdapter.notifyDataSetChanged()
        binding.spinnerHour.setSelection(0)
    }
    private fun resetHourOptions() {
        hourOptions.clear()
        hourOptions.addAll(resources.getStringArray(R.array.hour_options))
        hourAdapter.notifyDataSetChanged()
    }

    //Show repeat dialog
    private fun showCustomerRepeatDialog() {
        val builder =AlertDialog.Builder(requireContext())
        val inflater= requireActivity().layoutInflater
        val dialogView= inflater.inflate(R.layout.dialog_customer_repeat,null)

        val ed_interval= dialogView.findViewById<EditText>(R.id.ed_interval)
        val spinnerUnit = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerUnit)


        val units= arrayOf("day","week","month","year")
        val unitAdapter= ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerUnit.adapter=unitAdapter

        builder.setView(dialogView)
            .setPositiveButton("Ok")
            {
                    _,_ ->
                val interval = ed_interval.text.toString().toIntOrNull() ?: 1
                val selectUnit =spinnerUnit.selectedItem.toString()

                repeatInterval=interval
                repeatUnit=selectUnit

                updateRepeatSpinnerWithNextDate()
            }
            .setNegativeButton("Cancel"){
                    dialog,_ ->
                dialog.cancel()
                binding.spinnerAgain.setSelection(0)
            }

        builder.create().show()
    }
    //Logic processing when clicking custom time
    private fun updateSpinnerWithCustomTime() {
        val formattedTime = timeFormat.format(selectedTime.time)
        val customTimeString = "$formattedTime"

        hourOptions.removeAll{ it.contains(":") && it != "Pick an Hour..."}
        hourOptions.add(hourOptions.size-1,customTimeString)
        hourAdapter.notifyDataSetChanged()
        binding.spinnerHour.setSelection(hourOptions.indexOf(customTimeString))
    }
    // Logic processing when clicking custom date
    private fun updateSpinnerWithCustomDate() {
        val formattedDate = dateFormat.format(selectedTime.time)
        val customDateString = "$formattedDate"

        if (dayOptions.size > 4) {
            dayOptions.removeAt(4)
        }
        dayOptions.add(customDateString)
        dayAdapter.notifyDataSetChanged()
        binding.spinnerDay.setSelection(4)
    }


    //processed for next iteration when spinner is selected
    private fun updateRepeatSpinnerWithNextDate() {
        if(repeatInterval > 0 && repeatUnit.isNotEmpty()){
            val nextDate = getNextRepeatDate(selectedTime,repeatInterval,repeatUnit)
            val formattedNextDate= dateFormat.format(nextDate.time)

            val repeatString = when(repeatUnit){
                "day" -> if(repeatInterval==1) "Daily" else "Every $repeatInterval days"
                "week" -> if(repeatInterval==1) "Weekly" else "Every $repeatInterval weeks"
                "month" -> if(repeatInterval==1) "Monthly" else "Every $repeatInterval months"
                "year" -> if(repeatInterval==1) "Yearly" else "Every $repeatInterval years"
                else -> "Custom"
            }

            val newOption = "$repeatString (Next: $formattedNextDate)"

            if(repeatOptions.size > 6){
                repeatOptions[6]=newOption
            }
            else{
                repeatOptions.add(newOption)
            }

            repeatAdapter.notifyDataSetChanged()
            binding.spinnerAgain.setSelection(repeatOptions.size-1)
        }
    }

    private fun getNextRepeatDate(currentDate: Calendar, interval: Int, unit: String): Calendar {
        val nextDate = currentDate.clone() as Calendar
        when(unit){
            "day" -> nextDate.add(Calendar.DAY_OF_YEAR,interval)
            "week" -> nextDate.add(Calendar.WEEK_OF_YEAR,interval)
            "month" -> {
                nextDate.add(Calendar.MONTH,interval)
                adjustForMonthEnd(nextDate,currentDate.get(Calendar.DAY_OF_MONTH))
            }
            "year" -> {
                nextDate.add(Calendar.YEAR,interval)
                if(currentDate.get(Calendar.MONTH)==Calendar.FEBRUARY && currentDate.get(Calendar.DAY_OF_MONTH)==29){
                    if(!isLeapYear(nextDate.get(Calendar.YEAR))){
                        nextDate.set(Calendar.DAY_OF_MONTH,28)
                    }
                }
            }
        }

        return nextDate
    }

    //Check how many days there are in a month in a year (30 or 31)
    private fun adjustForMonthEnd(date: Calendar, originalDay: Int) {
        val maxDay =date.getActualMaximum(Calendar.DAY_OF_MONTH)
        if(originalDay>maxDay){
            date.set(Calendar.DAY_OF_MONTH,maxDay)
        }
    }

    // check is leap year
    private fun isLeapYear(year: Int): Boolean {
        return year%4==0 && (year%100!=0 || year %400==0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}