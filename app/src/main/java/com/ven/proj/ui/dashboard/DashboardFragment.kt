package com.ven.proj.ui.dashboard


import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ven.proj.R
import com.ven.proj.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    @Override
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {


        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //final MediaPlayer mediaPlayer = MediaPlayer.create(binding.this, R.raw.mymusic);
        //MediaPlayer mediaPlayer = MediaPlayer.create(binding.this, R.raw.mymusic);
        //
        var mediaPlayer = MediaPlayer.create(context, R.raw.da)
        val play = binding.play
        val pause = binding.pause
        play.setOnClickListener { mediaPlayer.start() }
        pause.setOnClickListener { mediaPlayer.pause() }

        //val textView: TextView = binding.textDashboard
        //dashboardViewModel.text.observe(viewLifecycleOwner) {
         //   textView.text = it
        //}

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}