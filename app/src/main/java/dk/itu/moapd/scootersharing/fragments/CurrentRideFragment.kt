package dk.itu.moapd.scootersharing.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.BUCKET_URL
import dk.itu.moapd.scootersharing.DATABASE_URL
import dk.itu.moapd.scootersharing.GlideApp
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.broadcastreceivers.VibrationReceiver
import dk.itu.moapd.scootersharing.database.Ride
import dk.itu.moapd.scootersharing.database.Scooter
import dk.itu.moapd.scootersharing.databinding.FragmentCurrentRideBinding
import dk.itu.moapd.scootersharing.services.TimerService
import dk.itu.moapd.scootersharing.viewmodels.ScooterSharingVM
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CurrentRideFragment: Fragment() {

    private var _binding: FragmentCurrentRideBinding? = null

    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private val storage = Firebase.storage(BUCKET_URL)
    private val simpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm")

    private lateinit var database: DatabaseReference
    private lateinit var viewModel: ScooterSharingVM

    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var timer = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = Firebase.database(DATABASE_URL).reference
        database.keepSynced(true)
        auth = FirebaseAuth.getInstance()

        serviceIntent = Intent(context, TimerService::class.java)
        requireActivity().registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
        // Inflate the layout for this fragment
        _binding = FragmentCurrentRideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this)[ScooterSharingVM::class.java]
        } ?: throw Exception("Invalid Activity")

        with(binding) {
            startRideButton.setOnClickListener {
                if (viewModel.noCurrentRideSet()) {
                    toast("Use camera to scan QR code and then start ride")
                } else {
                    var currentRide = viewModel.currentRide.value
                    startRide(currentRide)
                    endRideButton.isEnabled = true
                    startRideButton.isEnabled = false
                }
            }

            endRideButton.setOnClickListener {
                if (viewModel.noCurrentRideSet()) {
                    toast("You have not started a ride")
                } else {
                    var currentRide = viewModel.currentRide.value
                    endRide(currentRide)
                    endRideButton.isEnabled = false
                    startRideButton.isEnabled = true
                }
            }
        }
    }

    private fun startRide(qrcode: String?) {
        database.child("scooters").child(qrcode!!).get().addOnSuccessListener {
            val scooter = it.getValue<Scooter>()

            val uid = database.child("rides")
                .push()
                .key

            val ride = Ride()
            ride.ID = uid!!
            ride.scooter = scooter?.model
            ride.user = auth.currentUser?.displayName
            ride.color = scooter?.color
            ride.battery = scooter?.battery

            val currentTime = System.currentTimeMillis()
            val date = Date(currentTime)
            val time = simpleDateFormat.format(date).toString()

            ride.timeStarted = time

            database.child("rides")
                .child(ride.ID!!)
                .setValue(ride)

            viewModel.startRideVM(ride.ID!!)

            val imageRef = storage.reference.child("scooters/${scooter?.color}Scooter.jpg")

            GlideApp.with(this)
                .load(imageRef)
                .into(binding.currentScooterImageview)


            binding.currentScooterName.text = ride.scooter
            binding.currentRideMinutes.text = ride.timeStarted
            vibrate()
            startTimer()
        }
    }

    @SuppressLint("ResourceType")
    private fun endRide(qrcode: String?) {
        database.child("rides").child(qrcode!!).get().addOnSuccessListener {
            val ride = it.getValue<Ride>()

            val currentTime = System.currentTimeMillis()
            val date = Date(currentTime)
            val time = simpleDateFormat.format(date).toString()

            ride?.timeEnded = time

            ride?.duration = timer.toString() + " min"
            ride?.price = (timer*2).toString() + " DKK"

            database.child("rides")
                .child(ride?.ID!!)
                .setValue(ride)

            viewModel.startRideVM("")

            binding.currentScooterName.setText(R.string.no_current_ride)
            binding.currentRideMinutes.setText(R.string.current_minutes)
            binding.currentScooterImageview.setImageResource(R.drawable.ic_baseline_electric_scooter_24)
            vibrate()
            stopTimer()
        }
    }

    private fun startTimer()
    {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, timer)
        requireActivity().startService(serviceIntent)
        timerStarted = true
    }

    private fun stopTimer()
    {
        requireActivity().stopService(serviceIntent)
        timerStarted = false
        timer = 0.0
        binding.currentRideTimer.text = getTimeStringFromDouble(timer)
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            timer = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.currentRideTimer.text = getTimeStringFromDouble(timer)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String
    {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hour: Int, min: Int, sec: Int): String = String.format("%02d:%02d:%02d", hour, min, sec)

    private fun vibrate() {
        // ALARM
        val timestamp = System.currentTimeMillis()

        // Create the explicit intent of the broadcast receiver.
        val intent = Intent(requireContext(), VibrationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Set a timer when the broadcast receiver will be executed.
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            timestamp, pendingIntent)
    }

    private fun toast(text: CharSequence,
                      duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(activity, text, duration).show()
    }
}