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
import dk.itu.moapd.scootersharing.database.Scooter

class CustomAdapter(options: FirebaseRecyclerOptions<Scooter>):
    FirebaseRecyclerAdapter<Scooter, CustomAdapter.ViewHolder>(options) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val modelView: TextView = view.findViewById(R.id.scooter_name)
        val locationView: TextView = view.findViewById(R.id.scooter_where)
        val colorView: TextView = view.findViewById(R.id.scooter_color)
        val imageView: ImageView = view.findViewById(R.id.scooter_imageview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_scooter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, scooter: Scooter) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //Log.i(TAG(), "Populate an item at position: $position")

        val storage = Firebase.storage(BUCKET_URL)
        val imageRef = storage.reference.child("scooters/${scooter.color}Scooter.jpg")

        GlideApp.with(holder.itemView.context)
            .load(imageRef)
            .into(holder.imageView)

        // Bind the view holder with the selected `String` data.
        holder.apply {
            modelView.text = scooter.model
            locationView.text = scooter.location
            colorView.text = scooter.color
        }
    }

}