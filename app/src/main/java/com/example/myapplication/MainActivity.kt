package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Common.LOG_TAG
import com.example.myapplication.Common.SUBJECT_DB
import com.example.myapplication.recordDB.Record
import com.example.myapplication.recordDB.RecordDAO
import com.example.myapplication.recordDB.RecordDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var countTxtView : TextView
    lateinit var subjectSpinner : Spinner
    lateinit var subjectInputBox : EditText
    lateinit var spinnerAdapter: ArrayAdapter<String>

    lateinit var subjectInputBtn : Button
    lateinit var countUpBtn : Button
    lateinit var countDownBtn : Button

    lateinit var recordBtn : Button
    lateinit var resetSubjectBtn : Button


    lateinit var recordDB : RecordDatabase
    lateinit var recordDAO: RecordDAO
    lateinit var subjectDB : SharedPreferences

    lateinit var vibratorManager: VibratorManager
    private var count = 0
    lateinit var subject : String

    lateinit var today : String

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        recordDB = RecordDatabase.getInstance(applicationContext)
        recordDAO = recordDB.recordDao()

        subjectDB = this.getSharedPreferences(SUBJECT_DB, Context.MODE_PRIVATE)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        today = dateFormat.format(Date())

        viewAssign()
        setListeners()

        setSpinnerAdapter()



        Log.d("lifeCycle", "onCreate count : $count")
    }

    private fun setSpinnerAdapter() {

        var subjectArray : MutableList<String> = ArrayList()

        subjectDB.all.forEach{subjectMap->
            subjectArray.add(subjectMap.value.toString())
        }

        ArrayAdapter(this, android.R.layout.simple_spinner_item, subjectArray).also { adapter->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            subjectSpinner.adapter = adapter
            spinnerAdapter = adapter
        }


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
        subjectInputBox = findViewById(R.id.subjectInputBox)

        subjectInputBtn = findViewById(R.id.addSubjectBtn)
        countUpBtn = findViewById(R.id.countUpBtn)
        countDownBtn = findViewById(R.id.countDownBtn)

        recordBtn = findViewById(R.id.recordBtn)
        resetSubjectBtn = findViewById(R.id.resetSubjectDB)
    }

    private fun setListeners() {
        countUpBtn.setOnClickListener {
            setVibrate()
            changeCount(++count)
        }
        countDownBtn.setOnClickListener {
            setVibrate()
            changeCount(--count)
        }

        subjectInputBtn.setOnClickListener {
            setVibrate()
            val newSubject = subjectInputBox.text.toString()
            saveNewSubject(newSubject)
            loadSubject(newSubject)
            subjectInputBox.setText("")
        }

        countUpBtn.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event?.action){
                    MotionEvent.ACTION_DOWN
                    ->{
                        setVibrate()
                        changeCount(++count)
                    }
                }
                return true
            }
        })
        countDownBtn.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event?.action){
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->{
                        setVibrate()
                        changeCount(--count)
                    }
                }
                return true
            }
        })


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

        resetSubjectBtn.setOnClickListener {
            with(subjectDB.edit()){
                clear()
                apply()
            }
        }

    }


    private fun loadSubject(newSubject : String){
        spinnerAdapter.add(newSubject)
    }

    private fun saveNewSubject(newSubject: String) {
        with(subjectDB.edit()) {
            putString(newSubject, newSubject)
            apply()
        }
    }


    private fun loadCountBySubject() {
        try{
            CoroutineScope(Dispatchers.IO).launch {
                subject = subjectSpinner.selectedItem as String
                count = recordDAO.getCount(today, subject)
                countTxtView.text = count.toString()

                Log.d(LOG_TAG, "load subject : $subject , count : $count")
            }

        }catch (exception : NullPointerException){
            Log.d("exception", "subject Spinner가 비어있음.")
            exception.printStackTrace()
        }

    }


    private fun saveCountBySubject() {
        Log.d(LOG_TAG, "save subject : $subject , count : $count")
        subject?.let{
            CoroutineScope(Dispatchers.IO).launch {
                val newRecord = Record(today, subject, count)
                recordDAO.insert(newRecord)

            }
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