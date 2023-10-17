package a.co.varsitycollege.st10090538.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class RegisterPage : AppCompatActivity() {


    private lateinit var editText2: EditText
    private lateinit var editTextTextPassword: EditText
    private lateinit var editTextTextPassword2: EditText
    private lateinit var registerpage_register_button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        editText2 = findViewById(R.id.UsernameRegister)
        editTextTextPassword = findViewById(R.id.editTextTextPassword)
        editTextTextPassword2 = findViewById(R.id.editTextTextPassword2)
        registerpage_register_button = findViewById(R.id.registerpage_register_button)

        registerpage_register_button.setOnClickListener {
            val username = editText2.text.toString().trim()
            val password = editTextTextPassword.text.toString().trim()
            val confpass = editTextTextPassword2.text.toString().trim()

            //validation for empty username field
            if (username.isEmpty()){
                editText2.error = "Username is Required!"
                return@setOnClickListener

            }
            //validation for empty password field
            if (password.isEmpty()) {
                editTextTextPassword.error = "Password is Required!"
                return@setOnClickListener
            }
            //validation for an eight-character password
            var passwordPattern = Regex("^.{8,}$")  // Regex pattern for 8 or more characters
            if (!password.matches(passwordPattern)) {
                editTextTextPassword.error = "Password must be 8 characters or longer!"
                return@setOnClickListener
            }
            //validation for empty confirm password field
            if (confpass.isEmpty()) {
                editTextTextPassword2.error = "Confirm Password is Required!"
                return@setOnClickListener
            }
            // validation for passwords to match
            if (password != confpass) {
                editTextTextPassword2.error = "Password does not match!"
            }
            // validation if passwords match
            if (password == confpass) {

                Toast.makeText(this, "Registered Successfully!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginPage::class.java)
                intent.putExtra("username", username)
                intent.putExtra("password", password)
                startActivity(intent)
                finish()
            } else {
                editTextTextPassword2.error = "Password does not match!"
            }

        }
    }
}