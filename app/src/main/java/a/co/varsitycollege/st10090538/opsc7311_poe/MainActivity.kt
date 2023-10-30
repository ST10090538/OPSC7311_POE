package a.co.varsitycollege.st10090538.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        welcomePage()
    }

    private fun welcomePage() {
        setContentView(R.layout.welcome)

        val click = findViewById<View>(R.id.WelcomePage)
        click.setOnClickListener {
            startActivity(Intent(this, LoginPage::class.java))
        }
    }
}