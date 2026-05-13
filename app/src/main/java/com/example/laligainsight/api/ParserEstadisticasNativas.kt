package com.example.laligainsight.api

import com.example.laligainsight.modelo.InformacionExtraJugador

// Creación del objeto para parsear el HTML de nativeStats con inf completa del jugador
object ParserEstadisticasNativas {

    // Funcion que recibe el html como texto y devuelve un objeto InformacionExtraJugador
    fun parsePlayerHtml(html: String): InformacionExtraJugador {
        return InformacionExtraJugador(
            fullName = extractValue(html, "Full name"),
            position = extractValue(html, "Position"),
            birthday = extractValue(html, "Birthday"),
            nationality = extractValue(html, "Nationality"),
            currentTeam = extractValue(html, "Current team"),
            contract = extractValue(html, "Contract"),
            marketValue = extractValue(html, "Est. market value"),
            preferredFoot = extractValue(html, "Preferred foot"),
            matchesInDatabase = extractValue(html, "Matches in database"),

            // Lista de equipos/relaciones (parte de abajo)
            relatedItems = extractRelatedItems(html)
        )
    }


    // Función que busca un valor concreto del HTML
    private fun extractValue(html: String, label: String): String {



        // Buscamos el texto de la etiqueta en HTML
        val startLabel = ">$label</dt>"
        val startIndex = html.indexOf(startLabel)

        // Si no encuentra...
        if (startIndex == -1) {
            return "No disponible"
        }

        // --- <dt> Titulo (etiqueta)
        // --- <dd> Valor (dato)

        // Buscamos el <dd>
        val ddStart = html.indexOf("<dd", startIndex)

        // Buscamos el inicio de texto (después de ">")
        val valueStart = html.indexOf(">", ddStart) + 1

        // Buscamos el final de valor (antes de "</dd>")
        val valueEnd = html.indexOf("</dd>", valueStart)

        // Si falla, valor por defecto
        if (ddStart == -1 || valueEnd == -1) return "No disponible"

        return html.substring(valueStart, valueEnd)
            .replace(Regex("<.*?>"), "") // elimina etiquetas HTML
            .replace("\n", "")
            .replace("\r", "")
            .trim()
            .ifEmpty { "No disponible" }
    }


    // Función para extraer los elementos de abajo (equipos anteriores, selección...)
    private fun extractRelatedItems(html: String): List<String> {

        // Regex que busca nombres dentro de spans (donde salen los equipos)
        val matches = Regex("""alt="[^"]*">\s*<span[^>]*>\s*([^<]+)\s*</span>""")
            .findAll(html)
            .map { it.groupValues[1].trim() } // obtenemos el texto dentro de los span
            .filter { it.isNotBlank() }
            .distinct() // eliminamos duplicados
            .toList()

        // Limitamos a 10 elementos como maximo
        return matches.take(10)
    }
}

