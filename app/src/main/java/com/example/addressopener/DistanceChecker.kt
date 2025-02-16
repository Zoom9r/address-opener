package com.example.addressopener

import kotlin.math.*

fun calculateDistance(coord1: String, coord2: String): Double {
    val (lat1, lon1) = coord1.split(" ").map { it.toDouble() }
    val (lat2, lon2) = coord2.split(" ").map { it.toDouble() }

    val radius = 6371000.0 // Радіус Землі в метрах
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return radius * c
}
