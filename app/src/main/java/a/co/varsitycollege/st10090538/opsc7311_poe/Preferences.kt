package a.co.varsitycollege.st10090538.opsc7311_poe

import a.co.varsitycollege.st10090538.opsc7311_poe.NewObservation.Companion.CAMERA_PERMISSION_REQUEST_CODE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class Preferences : AppCompatActivity() {
    companion object {
        var unitsOfMeasurement: String = "km"
        var maxDistance: String = "10"

        const val PICK_IMAGE_REQUEST = 1
        const val REQUEST_IMAGE_CAPTURE = 2
    }

    private lateinit var kmButton: Button
    private lateinit var mButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var minDistanceLabel: TextView
    private lateinit var maxDistanceLabel: TextView
    private lateinit var selectedValue: TextView
    private var imgPicture: Bitmap? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        kmButton = findViewById(R.id.km_button)
        mButton = findViewById(R.id.m_button)
        seekBar = findViewById(R.id.seekBar)
        minDistanceLabel = findViewById(R.id.min_distance_label)
        maxDistanceLabel = findViewById(R.id.max_distance_label)
        selectedValue = findViewById(R.id.selectedDistanceLabel)
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
            startActivity(Intent(this, ExploreActivity::class.java))
        }

        updateButtonColors()
        updateDistanceLabels()

        seekBar.max = 50
        seekBar.progress = maxDistance.toInt()
        if(unitsOfMeasurement == "km"){
            selectedValue.text = "${maxDistance}${unitsOfMeasurement}"
        }
        else{
            selectedValue.text = "${(maxDistance.toDouble() / 1.609).roundToInt()}${unitsOfMeasurement}"
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                GlobalData.hotspotList.clear()
                maxDistance = progress.toString()
                if(unitsOfMeasurement == "km"){
                    selectedValue.text = "${progress.toString()}${unitsOfMeasurement}"
                }
                else{
                    selectedValue.text = "${(progress / 1.609).roundToInt()}${unitsOfMeasurement}"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
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
            unitsOfMeasurement = "mi"
            updateButtonColors()
            updateDistanceLabels()
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
        val orangeColor = ContextCompat.getColor(this, R.color.orange)
        val greyColor = ContextCompat.getColor(this, R.color.light_grey)

        if (unitsOfMeasurement == "km") {
            kmButton.setBackgroundColor(orangeColor)
            mButton.setBackgroundColor(greyColor)
        } else {
            kmButton.setBackgroundColor(greyColor)
            mButton.setBackgroundColor(orangeColor)
        }
    }


    private fun updateDistanceLabels() {
        if (unitsOfMeasurement == "km") {
            minDistanceLabel.text = "0 km"
            maxDistanceLabel.text = "50 km"
            selectedValue.text = "${maxDistance.toString()}km"
        } else {
            minDistanceLabel.text = "0 mi"
            maxDistanceLabel.text = (50 / 1.609).roundToInt().toString() + " mi"
            selectedValue.text = "${(maxDistance.toDouble() / 1.609).roundToInt()}mi"
        }
    }

}

