package a.co.varsitycollege.st10090538.opsc7311_poe

import a.co.varsitycollege.st10090538.opsc7311_poe.NewObservation.Companion.CAMERA_PERMISSION_REQUEST_CODE
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

class Preferences : AppCompatActivity() {
    companion object {
        var unitsOfMeasurement: String = "km"
        var maxDistance: Int = 100

        const val PICK_IMAGE_REQUEST = 1
        const val REQUEST_IMAGE_CAPTURE = 2
    }

    private lateinit var kmButton: Button
    private lateinit var mButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var minDistanceLabel: TextView
    private lateinit var maxDistanceLabel: TextView
    private var imgPicture: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        kmButton = findViewById(R.id.km_button)
        mButton = findViewById(R.id.m_button)
        seekBar = findViewById(R.id.seekBar)
        minDistanceLabel = findViewById(R.id.min_distance_label)
        maxDistanceLabel = findViewById(R.id.max_distance_label)
        val addPictureButton = findViewById<ImageView>(R.id.user_upload_image)
        val observeIcon = findViewById<ImageView>(R.id.imageView8)
        val exploreIcon = findViewById<ImageView>(R.id.imageView6)
        val backButton = findViewById<ImageView>(R.id.imageView)

        observeIcon.setOnClickListener {
            startActivity(Intent(this, Observations::class.java))
        }
        exploreIcon.setOnClickListener {
            startActivity(Intent(this, ExploreActivity::class.java))
        }
        backButton.setOnClickListener {
            // Navigate back to the Explore page
            startActivity(Intent(this, ExploreActivity::class.java))
        }

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
        addPictureButton.setOnClickListener {
            showPictureDialog()
        }

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

    //Allows the user to choose how to add a picture
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }
    private fun takePhotoFromCamera() {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed with camera intent
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with camera intent
                takePhotoFromCamera()
            } else {
                // Camera permission denied, show an error message or handle it accordingly
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val selectedImage = data?.data
                    imgPicture = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                    updateImageIcon()
                }

                REQUEST_IMAGE_CAPTURE -> {
                    imgPicture = data?.extras?.get("data") as Bitmap
                    if (imgPicture != null) {
                        // Convert bitmap to byte array
                        val stream = ByteArrayOutputStream()
                        imgPicture?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val imageData = stream.toByteArray()
                        updateImageIcon()
                    }
                }
            }
        }
    }
    private fun updateImageIcon() {
        val addPictureButton = findViewById<ImageView>(R.id.user_upload_image)
        addPictureButton.setImageBitmap(imgPicture)

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

