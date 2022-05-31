package dk.itu.moapd.scootersharing.interfaces

import dk.itu.moapd.scootersharing.database.Scooter

interface ItemClickListener {

    fun onItemClickListener(scooter: Scooter, position: Int)
}