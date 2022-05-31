package dk.itu.moapd.scootersharing.adapters

import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.BUCKET_URL
import dk.itu.moapd.scootersharing.GlideApp
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.database.Scooter
import dk.itu.moapd.scootersharing.interfaces.ItemClickListener

class CustomAdapter(private val itemClickListener: ItemClickListener, options: FirebaseRecyclerOptions<Scooter>):
    FirebaseRecyclerAdapter<Scooter, CustomAdapter.ViewHolder>(options) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val modelView: TextView = view.findViewById(R.id.scooter_name)
        val locationView: TextView = view.findViewById(R.id.scooter_where)
        val colorView: TextView = view.findViewById(R.id.scooter_color)
        val imageView: ImageView = view.findViewById(R.id.scooter_imageview)
        val reserveButtonView: Button = view.findViewById(R.id.reserve_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_ride, parent, false)
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

            reserveButtonView.setOnClickListener{
                itemClickListener.onItemClickListener(scooter, position)
                true
            }
        }
    }

}