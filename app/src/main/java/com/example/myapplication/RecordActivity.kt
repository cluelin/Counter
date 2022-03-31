package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.myapplication.Common.LOG_TAG
import com.example.myapplication.recordDB.Record
import com.example.myapplication.recordDB.RecordDAO
import com.example.myapplication.recordDB.RecordDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordActivity : AppCompatActivity() {

    lateinit var dateSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        val layout = findViewById<LinearLayout>(R.id.layout)
        dateSpinner = findViewById(R.id.dateSpinner)

        setSpinnerAdapter()



        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dateSpinner.setSelection(position)
                loadRecord(layout)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }

    private fun setSpinnerAdapter() {

        val recordDao = getRecordDao()
        CoroutineScope(Dispatchers.IO).launch {
            val dateArray = recordDao.getDate()
            ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, dateArray).also { adapter->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dateSpinner.adapter = adapter
            }
        }



    }

    private fun getRecordDao() : RecordDAO{
        val recordDB = RecordDatabase.getInstance(this)

        return recordDB.recordDao()
    }

    private fun loadRecord(viewGroup: ViewGroup){

        val recordDao = getRecordDao()

        val selectedDate = dateSpinner.selectedItem as String

        var getRecordsByDate : List<Record>? = null
        CoroutineScope(Dispatchers.IO).launch {
            getRecordsByDate = recordDao.getRecord(selectedDate)
            Log.d(LOG_TAG, "record : $getRecordsByDate")
        }

        viewGroup.removeAllViews()
        Thread.sleep(100)
        Log.d(LOG_TAG, "record : $getRecordsByDate")
        getRecordsByDate?.forEach{
            val dateTxt = TextView(applicationContext)
            val countTxt = TextView(applicationContext)
            val linearLayout = LinearLayout(applicationContext)

            linearLayout.orientation = LinearLayout.HORIZONTAL
            var param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            linearLayout.layoutParams = param

            dateTxt.setText(it.subject)
            countTxt.setText(it.count.toString())


            var param2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            param2.weight = 1F
            dateTxt.layoutParams = param2
            countTxt.layoutParams = param2

            linearLayout.addView(dateTxt)
            linearLayout.addView(countTxt)

            viewGroup.addView(linearLayout)
        }



    }
}