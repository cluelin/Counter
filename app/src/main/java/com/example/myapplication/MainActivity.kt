package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.myapplication.Common.COUNT_DB
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var countTxtView : TextView
    lateinit var subjectSpinner : Spinner
    lateinit var countDB :SharedPreferences
    lateinit var vibratorManager: VibratorManager
    private var count = 0
    lateinit var subject : String

    lateinit var countUpBtn : Button
    lateinit var countDownBtn : Button
    lateinit var resetBtn : Button
    lateinit var storeBtn : Button
    lateinit var recordBtn : Button

    lateinit var today : String

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        countDB = this.getSharedPreferences(COUNT_DB, Context.MODE_PRIVATE)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        today = dateFormat.format(Date())

        viewAssign()
        setOnClickListener()

        Log.d("lifeCycle", "onCreate count : $count")
    }

    override fun onResume() {
        super.onResume()

        loadCountBySubject()

        Log.d("lifeCycle", "onResume count : $count")
    }


    override fun onPause() {
        super.onPause()
        Log.d("lifeCycle", "onPause count : $count")

        saveCountBySubject()
    }



    private fun viewAssign() {

        countTxtView = findViewById(R.id.countTxt)
        subjectSpinner = findViewById(R.id.subject_spinner)

        ArrayAdapter.createFromResource(
            this, R.array.subject_array, android.R.layout.simple_spinner_item).also {adapter->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            subjectSpinner.adapter = adapter
        }

        countUpBtn = findViewById<Button>(R.id.countUpBtn)
        countDownBtn = findViewById<Button>(R.id.countDownBtn)
        resetBtn = findViewById<Button>(R.id.resetBtn)
        storeBtn = findViewById<Button>(R.id.storeBtn)
        recordBtn = findViewById<Button>(R.id.recordBtn)
    }

    private fun setOnClickListener() {
        countUpBtn.setOnClickListener {
            setVibrate()
            changeCount(++count)
        }

        countDownBtn.setOnClickListener {
            setVibrate()
            changeCount(--count)
        }

        resetBtn.setOnClickListener {
            setVibrate()
            changeCount(0)
        }

        storeBtn.setOnClickListener {
            setVibrate()
            with(countDB.edit()) {
                putInt(subject, count)
                apply()
            }
        }

        recordBtn.setOnClickListener {
            val recordIntent = Intent(this, RecordActivity::class.java)
            startActivity(recordIntent)
        }

        subjectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                saveCountBySubject()
                subjectSpinner.setSelection(position)
                loadCountBySubject()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }


    private fun loadCountBySubject() {
        subject = subjectSpinner.selectedItem as String
        count = countDB.getInt("$today $subject", 0)
        countTxtView.setText(count.toString())
    }


    private fun saveCountBySubject() {

        with(countDB.edit()) {
            putInt("$today $subject", count)
            apply()
        }
    }

    fun setVibrate(){
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, 50))
        }
    }


    private fun changeCount(count: Int) {
        this.count = count
        countTxtView.text = count.toString()
    }


}