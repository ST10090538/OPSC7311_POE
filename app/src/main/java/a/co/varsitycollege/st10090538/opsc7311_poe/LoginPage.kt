package a.co.varsitycollege.st10090538.opsc7311_poe

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class LoginPage : AppCompatActivity() {

    private lateinit var editText2: EditText
    private lateinit var editTextTextPassword: EditText
    private lateinit var loginpage_login_button: Button
    private lateinit var registerpage_register_button: Button
    private lateinit var firebaseAuth: FirebaseAuth

    private val storageRef = FirebaseStorage.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        firebaseAuth = FirebaseAuth.getInstance()
        editText2 = findViewById(R.id.login_username_input)
        editTextTextPassword = findViewById(R.id.login_password_input)
        loginpage_login_button = findViewById(R.id.Loginbutton)
        registerpage_register_button = findViewById(R.id.RegisterButton)

        registerpage_register_button.setOnClickListener {
            startActivity(Intent(this, RegisterPage::class.java))
        }


        loginpage_login_button.setOnClickListener {
            val username = editText2.text.toString().trim()
            val password = editTextTextPassword.text.toString().trim()

            if (TextUtils.isEmpty(username)) {
                editText2.error = "Username is Required!"
                return@setOnClickListener
            } else if (TextUtils.isEmpty(password)) {
                editTextTextPassword.error = "Password is Required!"
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // User login successful
                        Toast.makeText(this, "Logged In Successfully!", Toast.LENGTH_SHORT).show()
                        GlobalData.userID = firebaseAuth.currentUser?.uid.toString()

                        val database = Firebase.database("https://featherfinder-68e61-default-rtdb.europe-west1.firebasedatabase.app/")
                        val observationReference = database.getReference(GlobalData.userID).child("observations")
                        var wait = false

                        val observationListener = object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(observationSnapshot in snapshot.children){
                                    val observationMap = observationSnapshot.value as? Map<*, *>
                                    observationMap?.let {
                                        val name = it["name"] as String
                                        val count = it["count"].toString().toInt()
                                        val desc = it["desc"] as String
                                        val lat = it["lat"] as Double
                                        val lng = it["lng"] as Double
                                        val imgUrl = it["imgurl"]?.toString()

                                        if(imgUrl!=null){
                                            var img: Bitmap? = null
                                            val observationImageRef = storageRef.child(imgUrl)
                                            val MAX_SIZE_BYTES: Long = 1024 * 1024
                                            observationImageRef
                                            observationImageRef.getBytes(MAX_SIZE_BYTES).addOnSuccessListener { imageData ->
                                                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                                                // Use the bitmap as needed
                                                img = bitmap
                                                val observation = Observation(lng, lat, name, desc, count, img, null)
                                                GlobalData.observations.add(observation)
                                                GlobalData.updateMap = true
                                            }.addOnFailureListener {
                                            }
                                        }
                                        else{
                                            val observation = Observation(lng, lat, name, desc, count, null, null)
                                            GlobalData.observations.add(observation)
                                        }

                                    }
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        }
                        observationReference.addValueEventListener(observationListener)
                        startActivity(Intent(this, ExploreActivity::class.java))
                        finish()

                    } else {
                        // User login failed
                        Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }
}




