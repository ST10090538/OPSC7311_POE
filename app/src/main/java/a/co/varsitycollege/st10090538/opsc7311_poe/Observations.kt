package a.co.varsitycollege.st10090538.opsc7311_poe

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
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
        layout.orientation = LinearLayout.HORIZONTAL

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, dpToPx(10), 0, dpToPx(12))

        val backgroundColorResource = if (observationNumber % 2 == 0) {
            R.color.white
        } else {
            R.color.orange
        }
        layout.setBackgroundResource(backgroundColorResource)

        layout.layoutParams = layoutParams

        val numberTextView = TextView(this)
        numberTextView.text = observationNumber.toString()
        numberTextView.textSize = 24f
        numberTextView.setTextColor(Color.BLACK)
        numberTextView.setTypeface(null, Typeface.BOLD)

        val numberLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT

        )
        numberLayoutParams.gravity = Gravity.CENTER_VERTICAL
        numberLayoutParams.marginStart = dpToPx(20)
        numberTextView.layoutParams = numberLayoutParams

        val birdImageView = ImageView(this)

        // Loads image with Glide and applies rounded corners
        Glide.with(this)
            .load(observation.image)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(dpToPx(25)))
                .override(dpToPx(100), dpToPx(100)))
            .into(birdImageView)

        val imageLayoutParams = LinearLayout.LayoutParams(
            dpToPx(70),
            dpToPx(70)
        )
        imageLayoutParams.gravity = Gravity.CENTER_VERTICAL
        imageLayoutParams.setMargins(35, 15, dpToPx(20), 15)
        birdImageView.layoutParams = imageLayoutParams

        val observationDetailsLayout = LinearLayout(this)
        observationDetailsLayout.orientation = LinearLayout.VERTICAL
        // Display bird count
        val birdCountTextView = TextView(this)
        birdCountTextView.text = "Spotted: ${observation.count}"
        birdCountTextView.textSize = 18f
        birdCountTextView.setTextColor(Color.BLACK)

        //Display Bird Name
        val birdNameTextView = TextView(this)
        birdNameTextView.text = observation.name
        birdNameTextView.textSize = 22f
        birdNameTextView.setTextColor(Color.BLACK)
        birdNameTextView.setTypeface(null, Typeface.BOLD)

        val dateFormat = SimpleDateFormat("yyyy/MM/dd")
        val currentDate = dateFormat.format(Date())
        //Display Date when observation is created
        val birdNameDateTextView = TextView(this)
        birdNameDateTextView.text = "$currentDate"
        birdNameDateTextView.textSize = 18f
        birdNameDateTextView.setTextColor(Color.BLACK)

        observationDetailsLayout.addView(birdCountTextView)
        observationDetailsLayout.addView(birdNameTextView)
        observationDetailsLayout.addView(birdNameDateTextView)

        layout.addView(numberTextView)
        layout.addView(birdImageView)
        layout.addView(observationDetailsLayout)

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
