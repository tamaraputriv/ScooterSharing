package dk.itu.moapd.scootersharing

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

const val DATABASE_URL =
    "https://scooter-sharing-e5ea1-default-rtdb.europe-west1.firebasedatabase.app/"

const val BUCKET_URL = "gs://scooter-sharing-e5ea1.appspot.com"

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.database(DATABASE_URL).setPersistenceEnabled(true)
    }

}