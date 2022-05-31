package dk.itu.moapd.scootersharing.database

import android.location.Location
import android.media.Image
import android.net.Uri
import android.widget.ImageView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.net.URI
import java.net.URL

/**
 * A model class with all parameters to represent a dummy object in the database.
 */
@IgnoreExtraProperties
open class Scooter (
    var ID: String? = null,
    var locked: Boolean? = null,
    var battery: Int? = null,
    var model: String? = null,
    var color: String? = null,
    var image: ImageView? = null,
    var location: String? = null,
    var lat: String? = null,
    var long: String? = null
) {

    /**
     * Convert a instance of `Dummy` class into a `Map` to update the database on the RealTime
     * Firebase.
     *
     * @return A map with the table column name as the `key` and the class attribute as the `value`.
     */
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "ID" to ID,
            "locked" to locked,
            "battery" to battery.toString(),
            "model" to  model,
            "color" to color,
            "image" to image,
            "location" to location,
            "lat" to lat,
            "long" to long
        )
    }

}
