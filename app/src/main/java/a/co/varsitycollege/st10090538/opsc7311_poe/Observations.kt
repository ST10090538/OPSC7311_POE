package a.co.varsitycollege.st10090538.opsc7311_poe

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.ViewUtils.dpToPx
import java.text.SimpleDateFormat
import java.util.Date

class Observations: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.observations)

        val exploreIcon = findViewById<ImageView>(R.id.imageView10)
        val settingsIcon = findViewById<ImageView>(R.id.imageView9)

        val observationListLayout = findViewById<LinearLayout>(R.id.observationListLayout)

        val observations: List<Observation> = getObservations()

        // Initialize a counter for observation numbering
        var observationNumber = 1

        // Iterate through the list of observations and create UI elements for each
        for (observation in observations) {
            val observationLayout = createObservationLayout(observation, observationNumber)
            observationListLayout.addView(observationLayout)

            observationNumber++
        }

        exploreIcon.setOnClickListener {
            startActivity(Intent(this, ExploreActivity::class.java))
        }
        settingsIcon.setOnClickListener {
            startActivity(Intent(this, Preferences::class.java))
        }
    }

    private fun createObservationLayout(observation: Observation, observationNumber: Int): LinearLayout {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, dpToPx(10), 0, dpToPx(12))
        layout.layoutParams = layoutParams

        val numberTextView = TextView(this)
        numberTextView.text = observationNumber.toString()
        numberTextView.textSize = 24f
        numberTextView.setTextColor(Color.BLACK)


        val numberLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        numberLayoutParams.gravity = Gravity.CENTER_HORIZONTAL
        numberTextView.layoutParams = numberLayoutParams

        val dateFormat = SimpleDateFormat("yyyy/MM/dd")
        val currentDate = dateFormat.format(Date())

        val birdNameTextView = TextView(this)
        birdNameTextView.text = observation.name
        birdNameTextView.textSize = 22f
        birdNameTextView.setTextColor(Color.BLACK)

        val nameLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        nameLayoutParams.gravity = Gravity.CENTER_HORIZONTAL
        birdNameTextView.layoutParams = nameLayoutParams

        val birdImageView = ImageView(this)
        birdImageView.setImageBitmap(observation.image)

        val imageLayoutParams = LinearLayout.LayoutParams(
            dpToPx(70),
            dpToPx(70)
        )
        imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL
        imageLayoutParams.setMargins(0, dpToPx(10), 0, 0)
        birdImageView.layoutParams = imageLayoutParams

        val birdNameDateTextView = TextView(this)
        val birdNameDateText = "$currentDate"
        birdNameDateTextView.text = birdNameDateText
        birdNameDateTextView.textSize = 18f
        birdNameDateTextView.setTextColor(Color.BLACK)

        val dateLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dateLayoutParams.gravity = Gravity.CENTER_HORIZONTAL
        birdNameDateTextView.layoutParams = dateLayoutParams

        layout.addView(numberTextView)
        layout.addView(birdImageView)
        layout.addView(birdNameTextView)
        layout.addView(birdNameDateTextView)

        return layout
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun getObservations(): List<Observation> {
        return GlobalData.observations
    }
}