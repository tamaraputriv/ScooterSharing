package dk.itu.moapd.scootersharing.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.BUCKET_URL
import dk.itu.moapd.scootersharing.GlideApp
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.database.Ride
import dk.itu.moapd.scootersharing.database.Scooter
import dk.itu.moapd.scootersharing.interfaces.ItemClickListener
import org.w3c.dom.Text

class CustomRideAdapter(options: FirebaseRecyclerOptions<Ride>):
    FirebaseRecyclerAdapter<Ride, CustomRideAdapter.ViewHolder>(options) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val modelView: TextView = view.findViewById(R.id.ride_list_scooter_model)
        val rideStartView: TextView = view.findViewById(R.id.ride_started)
        val rideEndView: TextView = view.findViewById(R.id.ride_ended)
        val durationView: TextView = view.findViewById(R.id.final_duration)
        val priceView: TextView = view.findViewById(R.id.final_price)
        val userView: TextView = view.findViewById(R.id.ride_list_username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_ride, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, ride: Ride) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //Log.i(TAG(), "Populate an item at position: $position")

        // Bind the view holder with the selected `String` data.
        holder.apply {
            modelView.text = ride.scooter
            rideStartView.text = ride.timeStarted
            rideEndView.text = ride.timeEnded
            durationView.text = ride.duration
            priceView.text = ride.price
            userView.text = ride.user
        }
    }
}