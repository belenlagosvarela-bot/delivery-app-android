package pe.alimentosdelivery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. PRIMERO intentar login
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "✓ Login exitoso", Toast.LENGTH_SHORT).show()

                        // 2. LUEGO guardar en Firebase (versión simple)
                        guardarDatosEnFirebase()

                        // 3. FINALMENTE ir al menú
                        irAlMenu()
                    } else {
                        Toast.makeText(this, "✗ Error en login", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun guardarDatosEnFirebase() {
        try {
            val database = FirebaseDatabase.getInstance()
            val userId = auth.currentUser?.uid ?: "user-default"

            val datos = mapOf(
                "email" to auth.currentUser?.email,
                "ubicacion" to mapOf(
                    "latitud" to -33.4489,
                    "longitud" to -70.6693,
                    "timestamp" to System.currentTimeMillis(),
                    "tipo" to "GPS_SIMULADO"
                ),
                "fecha_login" to System.currentTimeMillis()
            )

            database.reference.child("usuarios").child(userId).setValue(datos)
                .addOnSuccessListener {
                    Toast.makeText(this, "✓ Datos guardados en Firebase", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "✗ Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "✗ Excepción: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun irAlMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
        finish()
    }
}