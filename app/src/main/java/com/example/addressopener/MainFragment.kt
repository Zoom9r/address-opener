import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.addressopener.MainAddressAdapter
import com.example.addressopener.House
import com.example.addressopener.R
import com.example.addressopener.Street
import com.example.addressopener.calculateDistance
import com.example.addressopener.generateKmlContent
import com.example.addressopener.readStreetsDataFromFile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream


class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var streetInput: AutoCompleteTextView
    private lateinit var houseSpinner: Spinner
    private lateinit var addButton: Button
    private lateinit var saveButton: Button

    private val selectedAddresses = mutableListOf<Map<String, String>>()
    private lateinit var streetsData: List<Street> // Завантажені дані про вулиці та будинки

    private lateinit var addressRecyclerView: RecyclerView
    private lateinit var addressAdapter: MainAddressAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        // Пошук елементів у макеті
        streetInput = view.findViewById(R.id.streetInput)
        houseSpinner = view.findViewById(R.id.houseSpinner)
        addButton = view.findViewById(R.id.addButton)
        saveButton = view.findViewById(R.id.saveButton)
        addressRecyclerView = view.findViewById(R.id.addressRecyclerView)

        // Завантаження даних із файлу JSON
        streetsData = readStreetsDataFromFile(
            requireContext(),
            "vinnytsia_streets_version_3.json"
        )

        // Ініціалізація Spinner за замовчуванням
        resetHouseSpinner()

        // Підказки для введення вулиць
        val streetNames = streetsData.map { it.name }
        val streetAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, streetNames)
        streetInput.setAdapter(streetAdapter)

        // Подія очищення поля вулиці
        streetInput.addTextChangedListener {
            if (streetInput.text.isNullOrEmpty()) {
                resetHouseSpinner()
            }
        }

        // Підключення події вибору вулиці
        streetInput.setOnItemClickListener { _, _, _, _ ->
            val selectedStreet = streetInput.text.toString()
            val streetData = streetsData.find { it.name == selectedStreet }

            if (streetData != null) {
                updateHouseSpinner(streetData.houses)
            } else {
                Toast.makeText(requireContext(), "Будинки для обраної вулиці не знайдено!", Toast.LENGTH_SHORT).show()
                updateHouseSpinner(emptyList())
            }
        }

        // Налаштування RecyclerView
        addressAdapter = MainAddressAdapter(selectedAddresses) { position ->
            selectedAddresses.removeAt(position)
            addressAdapter.notifyItemRemoved(position)
        }
        addressRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        addressRecyclerView.adapter = addressAdapter

        // Подія для кнопки "Додати адресу"
        addButton.setOnClickListener {
            val selectedStreet = streetInput.text.toString()
            val selectedHouse = houseSpinner.selectedItem?.toString() ?: ""
            val houseData = findHouseData(selectedStreet, selectedHouse) // Пошук даних будинку

            if (houseData != null) {
                val newCoordinates = houseData["coordinates"]

                // Перевірка на близькість до АО-адрес
                val nearbyAOAddresses = aoAddresses.filter { aoAddress ->
                    val aoCoordinates = aoAddress["coordinates"]
                    if (!newCoordinates.isNullOrEmpty() && !aoCoordinates.isNullOrEmpty()) {
                        calculateDistance(newCoordinates, aoCoordinates) < 500
                    } else false
                }

                // Формуємо дані для збереження
                val addressWithHint = houseData.toMutableMap()
                if (nearbyAOAddresses.isNotEmpty()) {
                    val aoHints = nearbyAOAddresses.joinToString("\n") {
                        "${it["street"]}, буд. ${it["house"]}, кв. ${it["apartment"] ?: "—"}"
                    }
                    addressWithHint["hint"] = aoHints
                }

                // Додаємо адресу до списку
                selectedAddresses.add(addressWithHint)
                addressAdapter.notifyItemInserted(selectedAddresses.size - 1)

                Toast.makeText(requireContext(), "Адреса додана!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Не вдалося знайти дані для цієї адреси.", Toast.LENGTH_SHORT).show()
            }
        }

        // Подія для кнопки "Додати в закладки"
        saveButton.setOnClickListener {
            if (selectedAddresses.isEmpty()) {
                Toast.makeText(requireContext(), "Додайте хоча б одну адресу!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val kmlFile = File(requireContext().filesDir, "bookmarks.kml")
            val kmlContent = generateKmlContent(selectedAddresses)

            try {
                FileOutputStream(kmlFile).use { outputStream ->
                    outputStream.write(kmlContent.toByteArray())
                }
                Toast.makeText(requireContext(), "KML-файл збережено!", Toast.LENGTH_SHORT).show()
                shareKmlFile(kmlFile)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Помилка запису в KML-файл: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        loadAOAddresses()

        return view
    }

    // Оновлення Spinner для будинків
    private fun updateHouseSpinner(houses: List<House>?) {
        if (houses.isNullOrEmpty()) {
            resetHouseSpinner() // Якщо немає будинків, скидаємо Spinner
        } else {
            houseSpinner.isEnabled = true // Розблокувати Spinner
            val houseList = houses.map { it.number }
            val houseAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, houseList)
            houseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            houseSpinner.adapter = houseAdapter
        }
    }

    // Скидання Spinner до стану "Виберіть будинок"
    private fun resetHouseSpinner() {
        houseSpinner.isEnabled = false // Вимкнути Spinner
        val defaultAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Виберіть будинок")
        )
        defaultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        houseSpinner.adapter = defaultAdapter
    }

    // Пошук даних будинку
    private fun findHouseData(street: String, house: String): Map<String, String>? {
        val streetData = streetsData.find { it.name == street }
        val houseData = streetData?.houses?.find { it.number == house }
        return houseData?.let {
            mapOf(
                "street" to street,
                "number" to house,
                "coordinates" to it.coordinates
            )
        }
    }

    private fun shareKmlFile(file: File) {
        val uri = FileProvider.getUriForFile(requireContext(), "com.example.addressopener.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.google-earth.kml+xml"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.mapswithme.maps.pro")
        }
        startActivity(intent)
    }

    private val aoAddresses = mutableListOf<Map<String, String>>() // Адреси з АО

    // Завантаження АО-адрес з файлу
    private fun loadAOAddresses() {
        val file = File(requireContext().filesDir, "saved_addresses.json")
        if (file.exists()) {
            val json = file.readText()
            val type = object : TypeToken<List<Map<String, String>>>() {}.type
            aoAddresses.clear()
            aoAddresses.addAll(Gson().fromJson(json, type))
        }
    }

}