package a.co.varsitycollege.st10090538.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class Preferences : AppCompatActivity() {
    companion object {
        var unitsOfMeasurments: String = "metric"
        var maxDistance: String = "10"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val observeIcon = findViewById<ImageView>(R.id.imageView8)
        val exploreIcon = findViewById<ImageView>(R.id.imageView6)


        observeIcon.setOnClickListener {
            startActivity(Intent(this, Observations::class.java))
        }
        exploreIcon.setOnClickListener {
            startActivity(Intent(this, ExploreActivity::class.java))
        }
    }

}


