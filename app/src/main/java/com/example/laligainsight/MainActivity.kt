package com.example.laligainsight

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laligainsight.api.RetrofitCliente
import com.example.laligainsight.databinding.ActivityMainBinding
import com.example.laligainsight.iu.EquiposAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Binding para acceder a los elementos de la interfaz de usuario
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicializamos el binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuramos el RecyclerView
        binding.recyclerEquipos.layoutManager = LinearLayoutManager(this)


        // Llamamos a la API utilizando corrutinas
        lifecycleScope.launch {
            try {

                // peticiaon a la api para obtner los equipos de la liga española
                val response = RetrofitCliente.api.getTeams()

                if (response.isSuccessful) {

                    val teams = response.body()?.teams

                    // Comprobamos que la lista de equipos no sea nula
                    if (teams != null) {

                        // Adapter donde pasamos la lista de equipos
                        val adapter = EquiposAdapter(teams)

                        // Conectamos el adapter al recyclerView
                        binding.recyclerEquipos.adapter = adapter
                    }

                } else {
                    // Si la api da error mostramos mensaje
                    println("Error en la respuesta de la API: ${response.code()}")
                }

            } catch (e: Exception) {
                // Aqui cogemos los posibles errores de conexión existentes
                e.printStackTrace()
            }
        }
    }
}
