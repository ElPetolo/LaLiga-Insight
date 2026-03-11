package com.example.laligainsight

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.laligainsight.api.RetrofitCliente
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Prueba simple para probalar la llamada a la API en consola (LogCat)
        lifecycleScope.launch {
            try {
                val response = RetrofitCliente.api.getTeams()

                if (response.isSuccessful) {
                    Log.d("API_TEST", response.body().toString())
                } else {
                    Log.d("API_TEST", "Error: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.d("API_TEST", "Exception: ${e.message}")
            }
        }

        }
    }
