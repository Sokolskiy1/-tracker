package com.ven.proj.ui.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ven.proj.R
import com.ven.proj.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {
//NotificationsFragment

    private var _binding: FragmentNotificationsBinding? = null
    private val glass1: ImageButton by lazy { (_binding!!.glass1) }
    private val glass2: ImageButton by lazy { (_binding!!.glass2) }
    private val glass3: ImageButton by lazy { (_binding!!.glass3) }
    private val glass4: ImageButton by lazy { (_binding!!.glass4) }
    private val glass5: ImageButton by lazy { (_binding!!.glass5) }
    private val okBtn: Button by lazy { binding!!.okBtn}
    private val resetBtn: Button by lazy { binding!!.resetBtn }
    private val timeLinearLayout: LinearLayout by lazy { binding!!.timeLinearLayout }
    private val congratsLinearLayout: LinearLayout by lazy { binding!!.congratsLinearLayout }
    private val minsEditText: EditText by lazy { binding!!.minsEditText }
    private val alarmTextView: TextView by lazy {binding!!.alarmTextView }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val glassAmounts = mutableListOf(0, 0, 0, 0, 0) // 0 -> full, 4 -> empty
    private val glassImages = listOf(
        R.drawable.glass_100,
        R.drawable.glass_75,
        R.drawable.glass_50,
        R.drawable.glass_25,
        R.drawable.glass_0
    )
    //private val prefs: SharedPreferences by lazy { activity!!getSharedPreferences("Prefs", AppCompatActivity.MODE_PRIVATE) }
     //var prefs : SharedPreferences = activity!!.getPreferences(  Context.MODE_PRIVATE);

//    var prefs : SharedPreferences = activity!!.getSharedPreferences( "Prefs" ,MODE_PRIVATE);
    // Other object references
    //var alarmManager : AlarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
    //private val alarmManager: AlarmManager by lazy { getSystemService(Fragment.ALARM_SERVICE) as AlarmManager }
//    private val alarmIntent: Intent by lazy { Intent(applicationContext, AlarmReceiverWater::class.java) }
    //var pendingIntent : PendingIntent = activity!!.getSystemService(Context.ALARM_SERVICE) as PendingIntent
//    private val pendingIntent: PendingIntent by lazy { PendingIntent.getBroadcast(applicationContext, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE) }

    // Other variables / constants
    private var wait = 0
    private var running = false
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val thisContext = container!!.getContext();
        val prefs = getActivity()!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
        var alarmManager : AlarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
       // var pendingIntent : PendingIntent = activity!!.getSystemService(Context.ALARM_SERVICE) as PendingIntent
        val  alarmIntent: Intent= Intent(activity!!.applicationContext, AlarmReceiverWater::class.java)
        val v: View = inflater.inflate(R.layout.fragment_notifications, container, false)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(activity!!.applicationContext, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

// Create a notification channel (For Android O [28] and above only)
        createNotificationChannel()

        // Read the shared preferences
        readPrefs()
        glass1.setOnClickListener {
            glassClicked(0)
        }
        glass2.setOnClickListener {
            glassClicked(1)
        }
        glass3.setOnClickListener {
           glassClicked(2)
       }
       glass4.setOnClickListener {
           glassClicked(3)
        }
        glass5.setOnClickListener {
            glassClicked(4)
        }



        okBtn.setOnClickListener {
            startAlarm()
        }
        resetBtn.setOnClickListener {
            // 'Refill' all glasses
            writeAllGlassPrefs()
            glassAmounts.fill(0)

            updateUI()

            startAlarm()
        }
        minsEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.isNotEmpty()) {
                        val editor = prefs.edit()
                        editor.putInt("wait", it.toString().toInt())
                        editor.apply()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        //val textView: TextView = binding.textNotifications
        //notificationsViewModel.text.observe(viewLifecycleOwner) {
       //     textView.text = it
      //  }
      //        binding.fab.setOnClickListener()
       // {
            //action_navigation_notifications_to_water_new_Fragment
             //   view ->
            //view.findNavController().navigate(R.id.action_navigation_notifications_to_water_new_Fragment)
       //AlarmReceiver
    //}
        return root
    }
    private fun glassClicked(glassIndex: Int) {
        // Animate water level decreasing (and resetting after empty)
        glassAmounts[glassIndex] = if (glassAmounts[glassIndex] == 4) {
            0
        } else {
            glassAmounts[glassIndex] + 1
        }

        // reset the alarm if all glasses aren't empty
        if (allNotEmpty()) {
            startAlarm()
        } else if (allEmpty()) {
            cancelAlarm()
        }

        // Update the UI
        val glass = when (glassIndex) {
            0 -> glass1
            1 -> glass2
            2 -> glass3
            3 -> glass4
            4 -> glass5
            else -> null
        }
        glass?.setBackgroundResource(glassImages[glassAmounts[glassIndex]])

        setLLVisibilities()

        // Update the shared preferences
        writeGlassAmount(glassIndex)
    }
    private fun writeGlassAmount(glassIndex: Int) {
        val editor =  getActivity()!!.getSharedPreferences("pref", Context.MODE_PRIVATE).edit()
        editor.putInt("g$glassIndex", glassAmounts[glassIndex])
        editor.apply()
    }
    private fun createNotificationChannel() {
        lateinit var notificationManager : NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.drink_water)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("WATER_TRACKER", name, importance)

            //notificationChannel.enableLights(true)
           // notificationChannel.lightColor = Color.GREEN
            //notificationChannel.enableVibration(true)
            //val notificationManager = getSystemService(activity!!.NOTIFICATION_SERVICE) as NotificationManager
           // notificationManager.createNotificationChannel(channel)
            val notificationManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

           // notificationManager.createNotificationChannel(channel)
        }
    }
    private fun readPrefs() {
        // Read prefs
        val prefs =  getActivity()!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
        glassAmounts[0] = prefs.getInt("g0", 0)
        glassAmounts[1] = prefs.getInt("g1", 0)
        glassAmounts[2] = prefs.getInt("g2", 0)
        glassAmounts[3] = prefs.getInt("g3", 0)
        glassAmounts[4] = prefs.getInt("g4", 0)
        wait = prefs.getInt("wait", 0)
        running = prefs.getBoolean("running", false)

        // Update UI from read prefs
        updateUI()
    }
    private fun updateUI() {
        glass1.setBackgroundResource(glassImages[glassAmounts[0]])
        glass2.setBackgroundResource(glassImages[glassAmounts[1]])
        glass3.setBackgroundResource(glassImages[glassAmounts[2]])
        glass4.setBackgroundResource(glassImages[glassAmounts[3]])
        glass5.setBackgroundResource(glassImages[glassAmounts[4]])
        minsEditText.setText("$wait")
        if (running) {
            alarmTextView.text = getString(R.string.currently_running)
        } else {
            alarmTextView.text = getString(R.string.not_running)
        }
        setLLVisibilities()
   }
    private fun allNotEmpty(): Boolean {
        return (glassAmounts[0] != 4 ||
                glassAmounts[1] != 4 ||
                glassAmounts[2] != 4 ||
                glassAmounts[3] != 4 ||
                glassAmounts[4] != 4)
    }

    private fun allEmpty(): Boolean {
        return (glassAmounts[0] == 4 &&
                glassAmounts[1] == 4 &&
                glassAmounts[2] == 4 &&
                glassAmounts[3] == 4 &&
                glassAmounts[4] == 4)
    }
    private fun setLLVisibilities() {
        if (allEmpty()) {
            congratsLinearLayout.visibility = View.VISIBLE
            timeLinearLayout.visibility = View.INVISIBLE
        } else {
            congratsLinearLayout.visibility = View.INVISIBLE
            timeLinearLayout.visibility = View.VISIBLE
        }
    }
    private fun startAlarm() {
        try {
            // Stop the alarm if there was one
            cancelAlarm()

            // Calculate wait time in millis
            val wait = minsEditText.text.toString().toInt()
            val millis = System.currentTimeMillis() + (wait * 1000 * 60)

            // Start the alarm
            val  alarmIntent: Intent= Intent(activity!!.applicationContext, AlarmReceiverWater::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(activity!!.applicationContext, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
            var alarmManager : AlarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent)
           }

            // Update UI and shared preferences
            putRunning(true)
            alarmTextView.text = getString(R.string.currently_running)
        } catch (e: NumberFormatException) {
            //Toast.makeText(applicationContext, getString(R.string.no_empty_minutes), Toast.LENGTH_SHORT).show()
        }
    }
    private fun putRunning(running: Boolean) {
        val prefs =  getActivity()!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("running", running)
        editor.apply()
   }

    private fun cancelAlarm() {
        val  alarmIntent: Intent= Intent(activity!!.applicationContext, AlarmReceiverWater::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(activity!!.applicationContext, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        var alarmManager : AlarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        putRunning(false)
       alarmTextView.text = getString(R.string.not_running)
    }
    private fun writeAllGlassPrefs() {
        val prefs =  getActivity()!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("g0", 0)
        editor.putInt("g1", 0)
        editor.putInt("g2", 0)
        editor.putInt("g3", 0)
        editor.putInt("g4", 0)
        editor.apply()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}