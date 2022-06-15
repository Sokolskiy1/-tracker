package com.ven.proj.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.treaf.calendario.sqliteDB
import com.ven.proj.R

class addEventDialog(): DialogFragment() {


    @SuppressLint("NewApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_layout , container , false)
        val eventTitle = view.findViewById<EditText>(R.id.eventTitle)
        val eventTime = view.findViewById<TimePicker>(R.id.eventTime)
        val eventDate = view.findViewById<DatePicker>(R.id.eventDate)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val eventTiming = StringBuilder().append(eventTime.hour).append(":")
            .append(eventTime.minute).toString()
        btnSave.setOnClickListener {
            val dbConnection = sqliteDB(context)
            dbConnection.insertIntoDB(eventTitle.text.toString(),eventDate.dayOfMonth,eventDate.month + 1 ,eventDate.year,eventTiming)
            Toast.makeText(context , "Done" , Toast.LENGTH_SHORT).show()
            this.dismiss()
            //activity?.finish()
            val intent = Intent(context, HomeFragment::class.java)
            //startActivity(intent)

        }



        return view

    }


}