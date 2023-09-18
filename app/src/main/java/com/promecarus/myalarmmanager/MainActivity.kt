package com.promecarus.myalarmmanager

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.promecarus.myalarmmanager.databinding.ActivityMainBinding
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener,
    TimePickerFragment.DialogTimeListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var alarmReceiver: AlarmReceiver

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
            .show()
        else Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33) requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)

        binding.btnOnceDate.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
        }

        binding.btnOnceTime.setOnClickListener {
            val timePickerFragmentOne = TimePickerFragment()
            timePickerFragmentOne.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
        }

        binding.btnSetOnceAlarm.setOnClickListener {
            val onceDate = binding.tvOnceDate.text.toString()
            val onceTime = binding.tvOnceTime.text.toString()
            val onceMessage = binding.edtOnceMessage.text.toString()
            alarmReceiver.setOneTimeAlarm(
                this, AlarmReceiver.TYPE_ONE_TIME, onceDate, onceTime, onceMessage
            )
        }

        binding.btnRepeatingTime.setOnClickListener {
            val timePickerFragmentRepeat = TimePickerFragment()
            timePickerFragmentRepeat.show(supportFragmentManager, TIME_PICKER_REPEAT_TAG)
        }

        binding.btnSetRepeatingAlarm.setOnClickListener {
            val repeatTime = binding.tvRepeatingTime.text.toString()
            val repeatMessage = binding.edtRepeatingMessage.text.toString()
            alarmReceiver.setRepeatingAlarm(
                this, AlarmReceiver.TYPE_REPEATING, repeatTime, repeatMessage
            )
        }

        alarmReceiver = AlarmReceiver()
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.tvOnceDate.text = dateFormat.format(calendar.time)
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        when (tag) {
            TIME_PICKER_ONCE_TAG -> binding.tvOnceTime.text = dateFormat.format(calendar.time)
            TIME_PICKER_REPEAT_TAG -> binding.tvRepeatingTime.text =
                dateFormat.format(calendar.time)

            else -> {
            }
        }
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
        private const val TIME_PICKER_REPEAT_TAG = "TimePickerRepeat"
    }
}
