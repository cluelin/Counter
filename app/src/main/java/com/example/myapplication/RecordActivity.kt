package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.myapplication.recordDB.Record
import com.example.myapplication.recordDB.RecordDAO
import com.example.myapplication.recordDB.RecordDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordActivity : AppCompatActivity() {

    lateinit var dateSpinner: Spinner
    lateinit var viewGroup : ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        viewGroup = findViewById<LinearLayout>(R.id.layout)
        dateSpinner = findViewById(R.id.dateSpinner)

        setSpinnerAdapter()

        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dateSpinner.setSelection(position)
                loadRecord()
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

    private fun loadRecord(){

        val recordDao = getRecordDao()

        val selectedDate = dateSpinner.selectedItem as String

        var getRecordsByDate : LiveData<List<Record>> = recordDao.getRecord(selectedDate)

        val recordObserver = Observer<List<Record>>{recordList ->
            updateRecord(recordList)
        }
        getRecordsByDate?.observe(this, recordObserver)
    }

    private fun updateRecord(recordList: List<Record>) {
        viewGroup.removeAllViews()
        recordList?.forEach {
            val dateTxt = TextView(applicationContext)
            val countTxt = TextView(applicationContext)
            val linearLayout = LinearLayout(applicationContext)

            setRecordView(linearLayout, dateTxt, countTxt)

            dateTxt.setText(it.subject)
            countTxt.setText(it.count.toString())

            registerView(linearLayout, dateTxt, countTxt)
        }
    }

    private fun registerView(
        linearLayout: LinearLayout,
        dateTxt: TextView,
        countTxt: TextView
    ) {
        linearLayout.addView(dateTxt)
        linearLayout.addView(countTxt)

        viewGroup.addView(linearLayout)
    }

    private fun setRecordView(
        linearLayout: LinearLayout,
        dateTxt: TextView,
        countTxt: TextView
    ) {
        linearLayout.orientation = LinearLayout.HORIZONTAL
        var param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayout.layoutParams = param


        var param2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        param2.weight = 1F
        dateTxt.layoutParams = param2
        countTxt.layoutParams = param2
    }
}