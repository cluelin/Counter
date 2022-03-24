package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.Common.COUNT_DB

class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        val layout = findViewById<LinearLayout>(R.id.layout)

        loadRecord(layout)

    }


    fun loadRecord(viewGroup: ViewGroup){
        val coudtDB = getSharedPreferences(COUNT_DB, MODE_PRIVATE)

        coudtDB.all.forEach{
            val dateTxt = TextView(this)
            val countTxt = TextView(this)
            val linearLayout = LinearLayout(this)

            linearLayout.orientation = LinearLayout.HORIZONTAL
            var param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            linearLayout.layoutParams = param

            dateTxt.setText(it.key)
            countTxt.setText(it.value.toString())


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