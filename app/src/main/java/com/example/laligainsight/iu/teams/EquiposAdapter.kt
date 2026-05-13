package com.example.laligainsight.iu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.laligainsight.R
import com.example.laligainsight.databinding.ItemEquipoBinding
import com.example.laligainsight.modelo.Team
import com.squareup.picasso.Picasso

// Clase para el adaptador del RecyclerView de equipos
class EquiposAdapter(private val equipos: List<Team>):
    RecyclerView.Adapter<EquiposAdapter.EquipoViewHolder>() {

    // Clase para representar cada equipo de la lista (ViewHolder)
    class EquipoViewHolder(val binding: ItemEquipoBinding): RecyclerView.ViewHolder(binding.root)

    // Método ejecutable cuando el Recycler necesita crear un nuevo item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipoViewHolder {

        // Inflamos el layout item_equipo.xml
        val binding = ItemEquipoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // Devolvemos el ViewHolder con el binding inflado
        return EquipoViewHolder(binding)
    }

    // Asignamos los datos a cada item del RecyclerView
    override fun onBindViewHolder(holder: EquipoViewHolder, position: Int) {

        // Obtenemos el equipo correspondiente a la posición actual
        val equipo = equipos[position]

        // Asignamos los datos del equipo al ViewHolder
        holder.binding.txtNombreEquipo.text = equipo.name

        // Si es el Atlético de Madrid, usamos el escudo guardado en drawable
        if (equipo.name == "Club Atlético de Madrid") {
            holder.binding.imgEscudo.setImageResource(R.drawable.escudoatleti)
        } else {
            // Para el resto de equipos cargamos el escudo desde la URL con Picasso
            Picasso.get()
                .load(equipo.crest)
                .into(holder.binding.imgEscudo)
        }
    }

    // Devolvemos el número de equipos en la lista
    override fun getItemCount(): Int {
        return equipos.size
    }
}