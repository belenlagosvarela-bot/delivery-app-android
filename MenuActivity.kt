package pe.alimentosdelivery

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.Locale

class MenuActivity : AppCompatActivity() {

    private lateinit var tvBienvenida: TextView
    private lateinit var tvCalculoDespacho: TextView
    private lateinit var tvTemperatura: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        tvBienvenida = findViewById(R.id.tvBienvenida)
        tvCalculoDespacho = findViewById(R.id.tvCalculoDespacho)
        tvTemperatura = findViewById(R.id.tvTemperatura)

        // Mostrar email del usuario
        val email = FirebaseAuth.getInstance().currentUser?.email ?: "Usuario"
        tvBienvenida.text = "Bienvenido: $email"

        // Calcular ejemplos de despacho
        calcularDespachoEjemplo()

        // Simular monitoreo de temperatura
        simularMonitoreoTemperatura()
    }

    private fun calcularDespachoEjemplo() {
        val montos = doubleArrayOf(55000.0, 35000.0, 15000.0) // >50k, 25-50k, <25k
        val distancia = 15.5 // km

        val resultado = StringBuilder("CÁLCULOS DE DESPACHO:\n\n")

        for (monto in montos) {
            val costo = calcularCostoDespacho(monto, distancia)
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

            resultado.append("Compra: ${format.format(monto)}")
                .append(" - Distancia: ${distancia}km")
                .append("\nCosto despacho: ${format.format(costo)}")
                .append("\n\n")
        }

        tvCalculoDespacho.text = resultado.toString()
    }

    private fun calcularCostoDespacho(montoCompra: Double, distanciaKm: Double): Double {
        return when {
            montoCompra > 50000 && distanciaKm <= 20 -> 0.0 // Gratis dentro de 20km
            montoCompra >= 25000 && montoCompra <= 49999 -> distanciaKm * 150 // $150 por km
            montoCompra < 25000 -> distanciaKm * 300 // $300 por km
            else -> (distanciaKm - 20) * 150 // >50k fuera de 20km
        }
    }

    private fun simularMonitoreoTemperatura() {
        val temperatura = -19.5 // Simulación
        val limite = -18.0

        var estadoTemperatura = "TEMPERATURA CAMIÓN: ${temperatura}°C\n"

        if (temperatura > limite) {
            estadoTemperatura += "⚠️ ALARMA: Temperatura crítica!"
            // Guardar alarma en Firebase
            FirebaseDatabase.getInstance().reference.child("alarmas")
                .push().setValue("Alarma: $temperatura°C - ${System.currentTimeMillis()}")
        } else {
            estadoTemperatura += "✅ Temperatura OK"
        }

        tvTemperatura.text = estadoTemperatura
    }
}