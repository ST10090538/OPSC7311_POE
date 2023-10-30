package a.co.varsitycollege.st10090538.opsc7311_poe

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class NewObservation : AppCompatActivity() {
    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val REQUEST_IMAGE_CAPTURE = 2
        const val CAMERA_PERMISSION_REQUEST_CODE = 3
    }

    private var imgPicture: Bitmap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_observation)

        val observeIcon = findViewById<ImageView>(R.id.imageView4)
        val settingsIcon = findViewById<ImageView>(R.id.imageView2)
        val exploreIcon = findViewById<ImageView>(R.id.imageView3)
        val submitButton = findViewById<Button>(R.id.newObservation_submit)

        observeIcon.setOnClickListener {
            startActivity(Intent(this, Observations::class.java))
        }
        settingsIcon.setOnClickListener {
            startActivity(Intent(this, Preferences::class.java))
        }
        exploreIcon.setOnClickListener {
            startActivity(Intent(this, ExploreActivity::class.java))
        }

        val addPictureButton = findViewById<ImageView>(R.id.newobservation_upload_image)
        val backButton = findViewById<ImageView>(R.id.imageView)

        backButton.setOnClickListener {
            // Navigate back to the Explore page
            startActivity(Intent(this, ExploreActivity::class.java))
        }

        submitButton.setOnClickListener {
            val locationEditText = findViewById<EditText>(R.id.txtLocation)
            val birdNameEditText = findViewById<EditText>(R.id.txtBirdName)
            val birdCountEditText = findViewById<EditText>(R.id.txtBirdCount)
            val descriptionEditText = findViewById<EditText>(R.id.txtDescription)

            val location = locationEditText.text.toString()
            val birdName = birdNameEditText.text.toString()
            val birdCountText = birdCountEditText.text.toString()
            val description = descriptionEditText.text.toString()

            //Implemented exception handling for the following fields:
            if (location.isBlank()) {
                locationEditText.error = "Location is required!"
                return@setOnClickListener
            }

            if (birdName.isBlank()) {
                birdNameEditText.error = "Bird name is required!"
                return@setOnClickListener
            }

            if (birdCountText.isBlank()) {
                birdCountEditText.error = "Bird count is required!"
                return@setOnClickListener
            }
            val birdCount = birdCountText.toIntOrNull() ?: 0

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                getDeviceLocation(birdName, birdCount, description)
            }

            addPictureButton.setOnClickListener {
            showPictureDialog()
        }
    }

    // Allows the user to choose how to add a picture
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Upload Image:")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { _: DialogInterface?, which: Int ->
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            // Permission already granted, proceed with camera intent
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with camera intent
                takePhotoFromCamera()
            } else {
                // Camera permission denied, show an error message
                Toast.makeText(this, "E R R O R", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @Deprecated("Deprecated in Java")
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
                    updateImageIcon()
                }
            }
        }
    }

    private fun updateImageIcon() {
        val addPictureButton = findViewById<ImageView>(R.id.newobservation_upload_image)
        addPictureButton.background = null
        addPictureButton.setImageBitmap(imgPicture)
    }
    private fun getDeviceLocation(birdName: String, count: Int?, desc: String?) {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    GlobalData.lastKnownLocation = task.result
                    val observation = Observation(GlobalData.lastKnownLocation!!.longitude, GlobalData.lastKnownLocation!!.latitude,
                        birdName, desc, count, imgPicture, null)
                    // Save the observation to a list in global data class
                    GlobalData.observations.add(observation)
                    val database = Firebase.database("https://featherfinder-68e61-default-rtdb.europe-west1.firebasedatabase.app/")
                    val userRef = database.getReference(GlobalData.userID)
                    val observationRef = userRef.child("observations").push()
                    if(observation.image == null){
                        observationRef.setValue(observation)
                    }
                    else{
                        val stream = ByteArrayOutputStream()
                        observation.image.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val imageData = stream.toByteArray()

                        val storageRef = FirebaseStorage.getInstance().reference
                        val observationImageRef = storageRef.child("${GlobalData.userID}/observationImages/${observationRef.key}")
                        observationImageRef.putBytes(imageData)

                        val tempObservation = Observation(GlobalData.lastKnownLocation!!.longitude, GlobalData.lastKnownLocation!!.latitude,
                            birdName, desc, count, null, "${GlobalData.userID}/observationImages/${observationRef.key}")

                        observationRef.setValue(tempObservation)
                    }
                    Toast.makeText(this, "Observation Created Successfully", Toast.LENGTH_SHORT).show()
                    if(!Achievements.firstObservation){
                        Achievements.firstObservation = true

                        val achievementsRef = database.getReference(GlobalData.userID)
                        achievementsRef.child("Achievements").child("firstObservation").setValue(true)
                    }
                    if(GlobalData.observations.count() >= 10){
                        if(!Achievements.milestone10){
                            Achievements.milestone10 = true
                            val achievementsRef = database.getReference(GlobalData.userID)
                            achievementsRef.child("Achievements").child("milestone10").setValue(true)
                        }
                        if(!Achievements.milestone20){
                            Achievements.milestone20 = true
                            val achievementsRef = database.getReference(GlobalData.userID)
                            achievementsRef.child("Achievements").child("milestone20").setValue(true)
                        }
                        if(!Achievements.milestone30){
                            Achievements.milestone30 = true
                            val achievementsRef = database.getReference(GlobalData.userID)
                            achievementsRef.child("Achievements").child("milestone30").setValue(true)
                        }
                    }
                    startActivity(Intent(this, ExploreActivity::class.java))
                } else {

                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}
