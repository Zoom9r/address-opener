package com.example.addressopener

import android.content.Context
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

data class Street(
    val name: String,
    val houses: List<House>
)

data class House(
    val number: String,
    val coordinates: String
)

fun readStreetsDataFromFile(context: Context, fileName: String): List<Street> {
    val streetsList = mutableListOf<Street>()
    try {
        val inputStream = context.assets.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = bufferedReader.use { it.readText() }
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val streetObject = jsonArray.getJSONObject(i)
            val streetName = streetObject.getString("street")
            val housesArray = streetObject.getJSONArray("houses")
            val housesList = mutableListOf<House>()

            for (j in 0 until housesArray.length()) {
                val houseObject = housesArray.getJSONObject(j)
                val houseNumber = houseObject.getString("number")
                val coordinates = houseObject.getString("coordinates")
                housesList.add(House(houseNumber, coordinates))
            }

            streetsList.add(Street(streetName, housesList))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return streetsList
}
