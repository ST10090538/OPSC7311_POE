package a.co.varsitycollege.st10090538.opsc7311_poe

import com.google.gson.Gson
import com.google.gson.JsonArray
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class WebHelper {
    private val apiKey = "eh1ifdq07drd"
    fun fetchHotspotData(long: String, lat: String): Thread {
        return Thread {
            val distance = Preferences.maxDistance
            val url =
                URL("https://api.ebird.org/v2/ref/hotspot/geo?fmt=json&lat=${lat}&lng=${long}&dist=${distance}")
            val connection = url.openConnection() as HttpsURLConnection

            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val innputStreamReader = InputStreamReader(inputSystem, "UTF-8")

                val gson = Gson()
                val jsonArray = gson.fromJson(innputStreamReader, JsonArray::class.java)


                jsonArray.forEach { element ->
                    val log = element.asJsonObject["lng"].asDouble
                    val lat = element.asJsonObject["lat"].asDouble
                    val name = element.asJsonObject["locName"].asString
                    val hotspot = Hotspot(log, lat, name)
                    GlobalData.hotspotList.add(hotspot)
                }
            }
        }
    }

    fun fetchNearbyObservations(long: String, lat: String): Thread {
        return Thread {
            val distance = Preferences.maxDistance
            val url =
                URL("https://api.ebird.org/v2/data/obs/geo/recent?lat=${lat}&lng=${long}&dist=${distance}&sort=date&maxResults=10")
            val connection = url.openConnection() as HttpsURLConnection
            connection.setRequestProperty("X-eBirdApiToken", apiKey)

            if (connection.responseCode == 200) {
                val inputSystem = connection.inputStream
                val innputStreamReader = InputStreamReader(inputSystem, "UTF-8")

                val gson = Gson()
                val jsonArray = gson.fromJson(innputStreamReader, JsonArray::class.java)

                val comNames = mutableListOf<String>()
                jsonArray.forEach { element ->
                    val name = element.asJsonObject["comName"].asString
                    val log = element.asJsonObject["lng"].asString.toDouble()
                    val lat = element.asJsonObject["lat"].asString.toDouble()
                    val hotspot = Hotspot(log, lat, name)
                    GlobalData.hotspotList.add(hotspot)
                    comNames.add(name)
                }

            }
        }
    }
}