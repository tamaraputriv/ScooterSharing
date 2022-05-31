package dk.itu.moapd.scootersharing.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.BUCKET_URL
import dk.itu.moapd.scootersharing.DATABASE_URL
import dk.itu.moapd.scootersharing.GlideApp
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.database.Ride
import dk.itu.moapd.scootersharing.database.Scooter
import dk.itu.moapd.scootersharing.databinding.FragmentCurrentRideBinding
import dk.itu.moapd.scootersharing.viewmodels.ScooterSharingVM
import java.text.SimpleDateFormat
import java.util.*

class CurrentRideFragment: Fragment() {

    private var _binding: FragmentCurrentRideBinding? = null

    private val binding get() = _binding!!

    private val storage = Firebase.storage(BUCKET_URL)
    private val simpleDateFormat = SimpleDateFormat("EEE, MMM d, hh:mm")

    private lateinit var database: DatabaseReference

    private lateinit var viewModel: ScooterSharingVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        database = Firebase.database(DATABASE_URL).reference
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
            database.child("rides")
                .child(ride?.ID!!)
                .setValue(ride)

            viewModel.startRideVM("")

            binding.currentScooterName.setText(R.string.no_current_ride)
            binding.currentRideMinutes.setText(R.string.current_minutes)
            binding.currentScooterImageview.setImageResource(R.drawable.ic_baseline_electric_scooter_24)
        }
    }
    private fun toast(text: CharSequence,
                      duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(activity, text, duration).show()
    }
}