package a.co.varsitycollege.st10090538.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginPage : AppCompatActivity() {

    private lateinit var editText2: EditText
    private lateinit var editTextTextPassword: EditText
    private lateinit var loginpage_login_button: Button
    private lateinit var registerpage_register_button: Button

    private var registeredUsername: String? = null
    private var registeredPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        editText2 = findViewById(R.id.login_username_input)
        editTextTextPassword = findViewById(R.id.login_password_input)
        loginpage_login_button = findViewById(R.id.Loginbutton)
        registerpage_register_button = findViewById(R.id.RegisterButton)

        registerpage_register_button.setOnClickListener {
            startActivity(Intent(this, RegisterPage::class.java))
        }

        val extras = intent.extras
        if (extras != null) {
            registeredUsername = extras.getString("username")
            registeredPassword = extras.getString("password")
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
            } else if (username != registeredUsername || password != registeredPassword) {
                Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                Toast.makeText(this, "Logged In Successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ExploreActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}