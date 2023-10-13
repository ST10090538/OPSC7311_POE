package a.co.varsitycollege.st10090538.opsc7311_poe

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Preferences : AppCompatActivity() {
    companion object {
        var unitsOfMeasurement: String = "km"
        var maxDistance: Int = 100
    }

    private lateinit var kmButton: Button
    private lateinit var mButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var minDistanceLabel: TextView
    private lateinit var maxDistanceLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        kmButton = findViewById(R.id.km_button)
        mButton = findViewById(R.id.m_button)
        seekBar = findViewById(R.id.seekBar)
        minDistanceLabel = findViewById(R.id.min_distance_label)
        maxDistanceLabel = findViewById(R.id.max_distance_label)

        updateButtonColors()
        updateDistanceLabels()

        seekBar.max = 100  // Set max to 100 (kilometers) initially

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                maxDistance = progress
                updateDistanceLabels()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Not needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not needed
            }
        })

        kmButton.setOnClickListener {
            unitsOfMeasurement = "km"
            updateButtonColors()
            updateDistanceLabels()
        }

        mButton.setOnClickListener {
            unitsOfMeasurement = "m"
            updateButtonColors()
            updateDistanceLabels()
        }

        val updateButton = findViewById<Button>(R.id.update_button)
        updateButton.setOnClickListener {
            updateMaxDistance()
            saveSettingsToSharedPreferences()
            Toast.makeText(this, "Settings updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateButtonColors() {
        if (unitsOfMeasurement == "km") {
            kmButton.setBackgroundResource(R.drawable.selected_button_background)
            mButton.setBackgroundResource(R.drawable.unselected_button_background)
        } else {
            kmButton.setBackgroundResource(R.drawable.unselected_button_background)
            mButton.setBackgroundResource(R.drawable.selected_button_background)
        }
    }

    private fun updateDistanceLabels() {
        if (unitsOfMeasurement == "km") {
            minDistanceLabel.text = "0 km"
            maxDistanceLabel.text = "$maxDistance km"
        } else {
            minDistanceLabel.text = "0 m"
            maxDistanceLabel.text = "${maxDistance * 1000} m"
        }
    }

    private fun updateMaxDistance() {
        if (unitsOfMeasurement == "m") {
            maxDistance *= 1000
        }
    }

    private fun saveSettingsToSharedPreferences() {
        val preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("unitsOfMeasurement", unitsOfMeasurement)
        editor.putInt("maxDistance", maxDistance)
        editor.apply()
    }
}
