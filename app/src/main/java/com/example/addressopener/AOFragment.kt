package com.example.addressopener

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher

class AOFragment : Fragment(R.layout.fragment_ao) {

    private lateinit var streetInputAO: AutoCompleteTextView
    private lateinit var houseSpinnerAO: Spinner
    private lateinit var apartmentInputAO: EditText
    private lateinit var addAddressButtonAO: Button
    private lateinit var savedAddressesRecyclerViewAO: RecyclerView
    private lateinit var addressAdapterAO: AOAddressAdapter

    private val savedAddresses = mutableListOf<Map<String, String>>() // Список збережених адрес
    private lateinit var streetsData: List<Street> // Дані про вулиці та будинки

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ao, container, false)

        // Ініціалізація елементів макету
        streetInputAO = view.findViewById(R.id.streetInputAO)
        houseSpinnerAO = view.findViewById(R.id.houseSpinnerAO)
        apartmentInputAO = view.findViewById(R.id.apartmentInputAO)
        addAddressButtonAO = view.findViewById(R.id.addAddressButtonAO)
        savedAddressesRecyclerViewAO = view.findViewById(R.id.savedAddressesRecyclerViewAO)

        // Встановлюємо початковий стан Spinner
        resetHouseSpinner()

        // Завантаження даних із файлу JSON
        streetsData = readStreetsDataFromFile(requireContext(), "vinnytsia_streets_final.json")

        setupStreetInput()
        setupRecyclerView()
        setupAddButton()

        loadAddressesFromFile() // Завантаження збережених адрес із файлу

        return view
    }

    private fun setupStreetInput() {
        val streetNames = streetsData.map { it.name } // Список назв вулиць
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, streetNames)
        streetInputAO.setAdapter(adapter)

        // Подія вибору вулиці
        streetInputAO.setOnItemClickListener { _, _, _, _ ->
            val selectedStreet = streetInputAO.text.toString()
            val streetData = streetsData.find { it.name == selectedStreet }

            if (streetData != null) {
                houseSpinnerAO.isEnabled = true
                updateHouseSpinner(streetData.houses)
            } else {
                resetHouseSpinner()
            }
        }

        // Очищення списку будинків, якщо поле очищене
        streetInputAO.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    resetHouseSpinner()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun resetHouseSpinner() {
        val defaultAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Виберіть будинок") // Текст для відображення
        )
        defaultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        houseSpinnerAO.adapter = defaultAdapter
        houseSpinnerAO.isEnabled = false // Поле недоступне, доки не вибрано вулицю
    }

    private fun updateHouseSpinner(houses: List<House>) {
        val houseNumbers = houses.map { it.number }
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            houseNumbers
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        houseSpinnerAO.adapter = spinnerAdapter
    }

    private fun setupRecyclerView() {
        savedAddressesRecyclerViewAO.layoutManager = LinearLayoutManager(requireContext())
        addressAdapterAO = AOAddressAdapter(savedAddresses) { position ->
            savedAddresses.removeAt(position)
            addressAdapterAO.notifyItemRemoved(position)
            saveAddressesToFile()
        }
        savedAddressesRecyclerViewAO.adapter = addressAdapterAO
    }

    private fun setupAddButton() {
        addAddressButtonAO.setOnClickListener {
            val selectedStreet = streetInputAO.text.toString()
            val selectedHouse = houseSpinnerAO.selectedItem?.toString() ?: ""
            val apartment = apartmentInputAO.text.toString()

            // Пошук координат введеної адреси
            val coordinates = findCoordinates(selectedStreet, selectedHouse)

            if (selectedStreet.isNotEmpty() && selectedHouse.isNotEmpty() && coordinates != null) {
                val addressData = mapOf(
                    "street" to selectedStreet,
                    "house" to selectedHouse,
                    "apartment" to apartment,
                    "coordinates" to coordinates
                )

                savedAddresses.add(addressData)
                addressAdapterAO.notifyItemInserted(savedAddresses.size - 1)
                saveAddressesToFile()
                Toast.makeText(requireContext(), "Адреса додана!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Заповніть всі поля або дані не знайдені!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findCoordinates(street: String, house: String): String? {
        val streetData = streetsData.find { it.name == street }
        val houseData = streetData?.houses?.find { it.number == house }
        return houseData?.coordinates
    }

    private fun saveAddressesToFile() {
        val file = File(requireContext().filesDir, "saved_addresses.json")
        val json = Gson().toJson(savedAddresses)
        file.writeText(json)
    }

    private fun loadAddressesFromFile() {
        val file = File(requireContext().filesDir, "saved_addresses.json")
        if (file.exists()) {
            val json = file.readText()
            val type = object : TypeToken<List<Map<String, String>>>() {}.type
            savedAddresses.clear()
            savedAddresses.addAll(Gson().fromJson(json, type))
            addressAdapterAO.notifyDataSetChanged()
        }
    }
}
