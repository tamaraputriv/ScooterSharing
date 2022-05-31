package dk.itu.moapd.scootersharing.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.DATABASE_URL
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.adapters.CustomAdapter
import dk.itu.moapd.scootersharing.adapters.CustomRideAdapter
import dk.itu.moapd.scootersharing.database.Ride
import dk.itu.moapd.scootersharing.database.Scooter
import dk.itu.moapd.scootersharing.databinding.FragmentListBinding
import dk.itu.moapd.scootersharing.databinding.FragmentRideListBinding

class RideListFragment: Fragment() {
    private lateinit var binding: FragmentRideListBinding

    private lateinit var database: DatabaseReference

    companion object {
        private lateinit var adapter: CustomRideAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Firebase.database(DATABASE_URL).reference

        // Enable offline capabilities.
        database.keepSynced(true)

        // Create the search query.
        val query = database.child("rides")
            .orderByChild("timeStarted")

        // A class provide by FirebaseUI to make a query in the database to fetch appropriate data.
        val options = FirebaseRecyclerOptions.Builder<Ride>()
            .setQuery(query, Ride::class.java)
            .setLifecycleOwner(this)
            .build()

        // Create the custom adapter to bind a list of dummy objects.
        adapter = CustomRideAdapter(options)
        binding = FragmentRideListBinding.inflate(layoutInflater)


        // Define the recycler view layout manager.
        binding.ridesRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        binding.ridesRecyclerView.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //_binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

        }
    }


}