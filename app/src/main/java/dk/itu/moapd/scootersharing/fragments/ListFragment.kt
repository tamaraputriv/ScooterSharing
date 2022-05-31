package dk.itu.moapd.scootersharing.fragments

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.DATABASE_URL
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.activities.CameraActivity
import dk.itu.moapd.scootersharing.activities.ScooterSharingActivity
import dk.itu.moapd.scootersharing.adapters.CustomAdapter
import dk.itu.moapd.scootersharing.database.Scooter
import dk.itu.moapd.scootersharing.databinding.FragmentListBinding
import dk.itu.moapd.scootersharing.interfaces.ItemClickListener
import java.io.IOException
import java.util.*

class ListFragment: Fragment(), ItemClickListener {

    private lateinit var binding: FragmentListBinding


    private lateinit var database: DatabaseReference

    /**
     * An extension of `AlertDialog.Builder` to create custom dialogs using a Material theme (e.g.,
     * Theme.MaterialComponents).
     */
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder

    /**
     * Inflates a custom Android layout used in the input dialog.
     */
    private lateinit var customAlertDialogView: View

    /**
     * A set of static attributes used in this activity class.
     */
    companion object {
        private lateinit var adapter: CustomAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database(DATABASE_URL).reference

        // Enable offline capabilities.
        database.keepSynced(true)

        // Create the search query.
        val query = database.child("scooters")
            .orderByChild("color")

        // A class provide by FirebaseUI to make a query in the database to fetch appropriate data.
        val options = FirebaseRecyclerOptions.Builder<Scooter>()
            .setQuery(query, Scooter::class.java)
            .setLifecycleOwner(this)
            .build()

        // Create the custom adapter to bind a list of dummy objects.
        adapter = CustomAdapter(this, options)
        binding = FragmentListBinding.inflate(layoutInflater)

        // Create a MaterialAlertDialogBuilder instance.
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this.requireContext())

        // Define the add button behavior.
        binding.floatingActionButton.setOnClickListener {

            // Inflate Custom alert dialog view
            customAlertDialogView = LayoutInflater.from(this.requireContext())
                .inflate(R.layout.dialog_add_scooter, binding.root, false)

            // Launching the custom alert dialog
            launchInsertAlertDialog()
        }

        // Define the recycler view layout manager.
        binding.recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        binding.recyclerView.adapter = adapter


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //_binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

        }
    }

    override fun onItemClickListener(scooter: Scooter, position: Int) {
        // Inflate Custom alert dialog view
        customAlertDialogView = LayoutInflater.from(this.requireContext())
            .inflate(R.layout.dialog_start_ride, binding.root, false)

        // Launching the custom alert dialog
        launchStartRideDialog(scooter, position)
    }

    /**
     * Building the insert alert dialog using the `MaterialAlertDialogBuilder` instance. This method
     * shows a dialog with a single edit text. The user can type a name and add it to the text file
     * dataset or cancel the operation.
     */
    private fun launchInsertAlertDialog() {
        val modelField = customAlertDialogView.findViewById<TextInputLayout>(R.id.name_text_field)
        val latField = customAlertDialogView.findViewById<TextInputLayout>(R.id.lat_text_field)
        val longField = customAlertDialogView.findViewById<TextInputLayout>(R.id.long_text_field)
        val colorField = customAlertDialogView.findViewById<TextInputLayout>(R.id.color_text_field)

        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle(getString(R.string.dialog_add_title))
            .setMessage(getString(R.string.dialog_add_message))
            .setPositiveButton(getString(R.string.add_button)) { dialog, _ ->
                val model = modelField.editText?.text.toString()
                val lat = latField.editText?.text.toString()
                val long = longField.editText?.text.toString()
                val color = colorField.editText?.text.toString()

                if (model.isNotEmpty()) {
                    val uid = database.child("scooters")
                        .push()
                        .key

                    val scooter = Scooter()
                    scooter.ID = uid!!
                    scooter.locked = false
                    scooter.battery = (30..100).random()
                    scooter.model = model
                    scooter.color = color
                    scooter.location = getAddress(lat.toDouble(), long.toDouble())
                    scooter.lat = lat
                    scooter.long = long


                    database.child("scooters")
                        .child(scooter.ID!!)
                        .setValue(scooter)
                }

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun launchStartRideDialog(scooter: Scooter, position: Int) {
        val textField = customAlertDialogView.findViewById<TextInputLayout>(R.id.name_text_field)
        textField.editText?.setText(scooter.model)

        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle(getString(R.string.dialog_start_ride))
            .setMessage(getString(R.string.dialog_scan_qr))
            .setPositiveButton(getString(R.string.dialog_start_ride_button)) { dialog, _ ->
                /*val model = textField.editText?.text.toString()
                if (model.isNotEmpty()) {
                    scooter.model = model

                    val adapter = binding.recyclerView.adapter as CustomAdapter
                    adapter.getRef(position).setValue(scooter)
                }*/

                val intent = Intent(this.requireActivity(), CameraActivity::class.java)
                startActivity(intent)



                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getAddress(latitude: Double, longitude: Double): String { val geocoder = Geocoder(this.requireContext(), Locale.getDefault())
        val stringBuilder = StringBuilder()
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                stringBuilder.apply{
                    append(address.getAddressLine(0)).append("\n")
                }
            } else return "Address not found!"
        } catch (ex: IOException) { ex.printStackTrace() }
        return stringBuilder.toString()
    }

}