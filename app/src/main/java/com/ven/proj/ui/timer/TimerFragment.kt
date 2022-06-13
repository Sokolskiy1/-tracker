package com.ven.proj.ui.timer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ven.proj.R
import com.ven.proj.databinding.FragmentTimerBinding


//import kotlinx.android.synthetic.main.fragment_timer

class TimerFragment : Fragment() {
    private var _binding: FragmentTimerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(TimeViewModel::class.java)

        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textNotifications
       // notificationsViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
       // }
       // val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
       //val valmeter =R.id.c_meter
        //val meter = findViewById<Chronometer>(R.id.c_meter)
        val meter = binding.cMeter
        val btn = binding.btn
            /*  btn?.setOnClickListener(object : View.OnClickListener {

            var isWorking = false

            override fun onClick(v: View) {
                if (!isWorking) {
                    meter.start()
                    isWorking = true
                } else {
                    meter.stop()
                    isWorking = false
                }
                binding.btn.setText(
                    if (isWorking)
                        binding.cMeter.start().toString()
                    else
                        binding.cMeter.stop().toString()

                )


            }
        })*/
        val strElapsedMillis = "Прошло больше 5 секунд"
        var mediaPlayer = MediaPlayer.create(context, R.raw.da)
        val elapsedMillis: Long = (SystemClock.elapsedRealtime()
                - meter.getBase())
        if (elapsedMillis >5000) {
           // val strElapsedMillis = "Прошло больше 5 секунд"

                //System.out.println(strElapsedMillis)
            mediaPlayer.start()
        }
        System.out.println(strElapsedMillis)
        binding.btn.setOnClickListener {
            meter.setBase(SystemClock.elapsedRealtime() + 1000 * 1800 );//1000 * 1800
            Log.i("Chronometer", "Обратный отсчёт: " + meter.isCountDown());

            //meter.base = SystemClock.elapsedRealtime()
            meter.start()
        }
        binding.btn2.setOnClickListener {
            System.out.println(elapsedMillis)
            mediaPlayer.pause()
            meter.stop()
        }

        binding.btn3.setOnClickListener {
            meter.base = SystemClock.elapsedRealtime()
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}