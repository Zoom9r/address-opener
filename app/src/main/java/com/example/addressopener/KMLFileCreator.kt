package com.example.addressopener

fun generateKmlContent(addresses: List<Map<String, String>>): String {
    return buildString {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n")
        append("<Document>\n")
        addresses.forEach { address ->
            val coordinates = address["coordinates"] ?: ""
            val (latitude, longitude) = coordinates.split(" ")
            append("<Placemark>\n")
            append("<name>${address["street"]}, ${address["number"]}</name>\n")
            append("<Point>\n")
            append("<coordinates>$longitude,$latitude</coordinates>\n") // Довгота, широта
            append("</Point>\n")
            append("</Placemark>\n")
        }
        append("</Document>\n")
        append("</kml>")
    }
}

