package a.co.varsitycollege.st10090538.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class Observations: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.observations)

            val exploreIcon = findViewById<ImageView>(R.id.imageView10)
            val settingsIcon = findViewById<ImageView>(R.id.imageView9)


           exploreIcon.setOnClickListener {
                startActivity(Intent(this, ExploreActivity::class.java))
            }
            settingsIcon.setOnClickListener {
                startActivity(Intent(this, Preferences::class.java))
            }
        }
}