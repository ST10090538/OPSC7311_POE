package a.co.varsitycollege.st10090538.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class NewObservation: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_observation)

        val observeIcon = findViewById<ImageView>(R.id.imageView4)
        val settingsIcon = findViewById<ImageView>(R.id.imageView2)
        val submitButton = findViewById<Button>(R.id.newObservation_submit)

        observeIcon.setOnClickListener {
            startActivity(Intent(this, Observations::class.java))
        }
        settingsIcon.setOnClickListener {
            startActivity(Intent(this, Preferences::class.java))
        }
    }
}