package com.ven.proj.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.treaf.calendario.Calendario
import com.ven.proj.databinding.FragmentHomeBinding


class HomeFragment : Fragment(), Calendario.OnDateSelectedListener {
    override fun onDateSelected(day: Int, month: Int, year: Int) {
        val eventsArray = customCalendario.getEventsByDate(day, month, year).getEvent
        val timeArray   = customCalendario.getEventsByDate(day, month, year).getTime
        if (eventsArray.isEmpty()){
            recyclerView.visibility = View.GONE
            eventsInfo.visibility = View.VISIBLE
            eventsInfo.text = "No Events"
            eventsInfo.textSize = 20f
            eventsInfo.setTextColor(Color.BLACK)
        } else {
            eventsInfo.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            val eventAdapter = eventsAdapter(context!!.applicationContext , eventsArray , timeArray , object : ItemClick {
                override fun getItemClick(position: Int) {
                    customCalendario.removeEvent(eventsArray[position] , day, month, year)
                    recyclerView.adapter?.notifyDataSetChanged()
                    Toast.makeText(context!!.applicationContext , "Successfully" , Toast.LENGTH_SHORT).show()
                    //val intent = Intent(this@HomeFragment.getActivity(), HomeFragment::class.java)
                    //startActivity(intent)
                        //.replace(R.id.fragment_container, fragment);
                }

            })
            recyclerView.adapter = eventAdapter
            recyclerView.addItemDecoration(DividerItemDecoration(context!!.applicationContext , DividerItemDecoration.VERTICAL))


        }


    }
    private lateinit var customCalendario: Calendario
    lateinit var recyclerView: RecyclerView
    lateinit var eventsInfo: TextView
    lateinit var btnAdd: Button
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //setContentView(R.layout.fragment_home)
        customCalendario = binding.custom
        recyclerView = binding.eventsRecycler
        eventsInfo = binding.eventsInfo
        btnAdd = binding.btnAdd
        recyclerView.visibility = View.GONE
        customCalendario.setStartDayOfWeek(2)
        customCalendario.setSelectionType(1)
        customCalendario.setOnDateListener(this)
        btnAdd.setOnClickListener {
            //val ft = supportFragmentManager.beginTransaction()
            val ft  = (activity as FragmentActivity).supportFragmentManager
            val newFragment = addEventDialog()
            newFragment.show(ft, "dialog")
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun Calendario.setOnDateListener(listener: HomeFragment) {

}
