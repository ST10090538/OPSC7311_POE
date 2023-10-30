package a.co.varsitycollege.st10090538.opsc7311_poe

import android.graphics.Bitmap
import android.location.Location

object GlobalData {
    val hotspotList = mutableListOf<Hotspot>()
    val observations = mutableListOf<Observation>()
    var lastKnownLocation: Location? = null
    var userID: String = "testUser"
    var updateMap: Boolean = false
    var profilePic: Bitmap? = null
}