package dk.itu.moapd.scootersharing.database

import android.widget.ImageView
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/**
 * A model class with all parameters to represent a dummy object in the database.
 */
@IgnoreExtraProperties
open class Ride (
    var ID: String? = null,
    var battery: Int? = null,
    var scooter: String? = null,
    var user: String? = null,
    var color: String? = null,
    var image: ImageView? = null,
    var timeStarted: String? = null,
    var timeEnded: String? = null,
    var duration: String? = null,
    var price: String? = null
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
            "battery" to battery.toString(),
            "scooter" to  scooter,
            "user" to user,
            "color" to color,
            "image" to image,
            "timeStarted" to timeStarted,
            "timeEnded" to timeEnded,
            "duration" to duration,
            "price" to price
        )
    }

}
