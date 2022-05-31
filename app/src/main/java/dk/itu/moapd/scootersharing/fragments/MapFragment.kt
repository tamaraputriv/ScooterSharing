package dk.itu.moapd.scootersharing.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import dk.itu.moapd.scootersharing.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.MapsInitializer.Renderer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.DATABASE_URL
import dk.itu.moapd.scootersharing.database.Scooter
import dk.itu.moapd.scootersharing.viewmodels.ScooterSharingVM

class MapFragment: Fragment(), OnMapsSdkInitializedCallback {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private lateinit var database: DatabaseReference

    companion object {
        private val TAG = MapFragment::class.qualifiedName
        private const val DEFAULT_ZOOM = 15
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        val scooterQuery = database.child("scooters").orderByChild("location")

        scooterQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val scooter = postSnapshot.getValue<Scooter>()
                    googleMap.addMarker(MarkerOptions()
                        .position
                            (LatLng(scooter!!.lat!!.toDouble(),scooter!!.long!!.toDouble()))
                        .title(scooter.model))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })
        // Add a marker in ITU and move the camera
        val locationRes = fusedLocationProviderClient.lastLocation
        val itu = LatLng(55.6596, 12.5910)

        locationRes.addOnCompleteListener(this.requireActivity()) { task ->
            if (task.isSuccessful) {
                // Set the map's camera position to the current location of the device.
                lastKnownLocation = task.result
                if (lastKnownLocation != null) {
                    googleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude
                            ), DEFAULT_ZOOM.toFloat()
                        )
                    )
                }
            } else {
                Log.d(TAG, "Current location is null. Using defaults.")
                Log.e(TAG, "Exception: %s", task.exception)
                googleMap?.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(itu, DEFAULT_ZOOM.toFloat()))
                googleMap?.uiSettings?.isMyLocationButtonEnabled = false
            }
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        if (!checkPermission()) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        database = Firebase.database(DATABASE_URL).reference
        // Enable offline capabilities.
        database.keepSynced(true)
        MapsInitializer.initialize(requireContext(), Renderer.LATEST, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    /**
     * This method checks if the user allows the application uses all location-aware resources to
     * monitor the user's location.
     *
     * @return A boolean value with the user permission agreement.
     */
    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED

    /**
     * This method determines which version of the renderer was returned.
     *
     * @param renderer The renderer used in the Google Maps
     */
    override fun onMapsSdkInitialized(renderer: Renderer) {
        when (renderer) {
            Renderer.LATEST ->
                Log.d(TAG, "The latest version of the renderer is used.")
            Renderer.LEGACY ->
                Log.d(TAG, "The legacy version of the renderer is used.")
        }
    }
}