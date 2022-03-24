package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibratorManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var countTxtView : TextView
    lateinit var dateTxtView : TextView
    lateinit var sharedPref : SharedPreferences
    lateinit var vibratorManager: VibratorManager
    private var count = 0
    lateinit var date : String

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        countTxtView = findViewById(R.id.countTxt)
        dateTxtView = findViewById(R.id.dateTxt)

        val countUpBtn = findViewById<Button>(R.id.countUpBtn)
        val countDownBtn = findViewById<Button>(R.id.countDownBtn)
        val resetBtn = findViewById<Button>(R.id.resetBtn)
        val storeBtn = findViewById<Button>(R.id.storeBtn)

        sharedPref = this.getSharedPreferences(COUNT_DB, Context.MODE_PRIVATE)


        Log.d("lifeCycle", "onCreate count : $count")

        countTxtView.setText(count.toString())

        countUpBtn.setOnClickListener {
            changeCount(++count)
        }

        countDownBtn.setOnClickListener {
            changeCount(--count)
        }

        resetBtn.setOnClickListener {
            changeCount(0)
        }

        storeBtn.setOnClickListener {
            with(sharedPref.edit()){
                putInt(date, count)
                apply()
            }
        }
    }


    override fun onResume() {
        super.onResume()

        count = sharedPref.getInt(KEY_COUNT, 0)
        countTxtView.setText(count.toString())


        date = SimpleDateFormat("yyyy-MM-dd").format(Date())

        dateTxtView.setText(date)

        Log.d("lifeCycle", "onResume count : $count")
    }

    override fun onPause() {
        super.onPause()
        Log.d("lifeCycle", "onPause count : $count")
        with(sharedPref.edit()){
            putInt(KEY_COUNT, count)
            apply()
        }
    }

    override fun onStop() {
        super.onStop()

        Log.d("lifeCycle", "onStop count : $count")
    }

    override fun onStart() {
        super.onStart()
        Log.d("lifeCycle", "onstart count : $count")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("lifeCycle", "onRestart count : $count")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("lifeCycle", "onDestroy count : $count")
    }

    private fun changeCount(count: Int) {
        this.count = count
        countTxtView.text = count.toString()
    }

    companion object{
        private val COUNT_DB = "countDB"
        private val KEY_COUNT = "count"
    }
}