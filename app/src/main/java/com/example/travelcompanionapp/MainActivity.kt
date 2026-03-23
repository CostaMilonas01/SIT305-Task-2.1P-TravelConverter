package com.example.travelcompanionapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerSource: Spinner
    private lateinit var spinnerDestination: Spinner
    private lateinit var etInput: EditText
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView

    private val categories = arrayOf("Currency", "Fuel / Distance", "Temperature")

    private val currencyUnits = arrayOf("USD", "AUD", "EUR", "JPY", "GBP")
    private val fuelUnits = arrayOf("mpg", "km/L", "Gallon (US)", "Liters", "Nautical Mile", "Kilometers")
    private val temperatureUnits = arrayOf("Celsius", "Fahrenheit", "Kelvin")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerSource = findViewById(R.id.spinnerSource)
        spinnerDestination = findViewById(R.id.spinnerDestination)
        etInput = findViewById(R.id.etInput)
        btnConvert = findViewById(R.id.btnConvert)
        tvResult = findViewById(R.id.tvResult)

        // Set category spinner
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        // Update source/destination spinners when category changes
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                updateUnitSpinners(categories[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnConvert.setOnClickListener {
            val inputText = etInput.text.toString()

            if (inputText.isEmpty()) {
                Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val inputValue = inputText.toDoubleOrNull()
            if (inputValue == null) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = spinnerCategory.selectedItem.toString()
            val source = spinnerSource.selectedItem.toString()
            val destination = spinnerDestination.selectedItem.toString()

            val result = convertValue(category, source, destination, inputValue)

            if (result == null) {
                tvResult.text = "Conversion not supported"
            } else {
                tvResult.text = "Result: %.2f".format(result)
            }
        }

        // Load initial spinners
        updateUnitSpinners(categories[0])
    }

    private fun updateUnitSpinners(category: String) {
        val units = when (category) {
            "Currency" -> currencyUnits
            "Fuel / Distance" -> fuelUnits
            "Temperature" -> temperatureUnits
            else -> arrayOf()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerSource.adapter = adapter
        spinnerDestination.adapter = adapter
    }

    private fun convertValue(category: String, source: String, destination: String, value: Double): Double? {
        if (source == destination) return value

        return when (category) {
            "Currency" -> convertCurrency(source, destination, value)
            "Fuel / Distance" -> convertFuelDistance(source, destination, value)
            "Temperature" -> convertTemperature(source, destination, value)
            else -> null
        }
    }

    private fun convertCurrency(source: String, destination: String, value: Double): Double? {
        // Convert everything to USD first
        val valueInUsd = when (source) {
            "USD" -> value
            "AUD" -> value / 1.55
            "EUR" -> value / 0.92
            "JPY" -> value / 148.50
            "GBP" -> value / 0.78
            else -> return null
        }

        // Convert USD to destination
        return when (destination) {
            "USD" -> valueInUsd
            "AUD" -> valueInUsd * 1.55
            "EUR" -> valueInUsd * 0.92
            "JPY" -> valueInUsd * 148.50
            "GBP" -> valueInUsd * 0.78
            else -> null
        }
    }

    private fun convertFuelDistance(source: String, destination: String, value: Double): Double? {
        return when {
            source == "mpg" && destination == "km/L" -> value * 0.425
            source == "km/L" && destination == "mpg" -> value / 0.425

            source == "Gallon (US)" && destination == "Liters" -> value * 3.785
            source == "Liters" && destination == "Gallon (US)" -> value / 3.785

            source == "Nautical Mile" && destination == "Kilometers" -> value * 1.852
            source == "Kilometers" && destination == "Nautical Mile" -> value / 1.852

            else -> null
        }
    }

    private fun convertTemperature(source: String, destination: String, value: Double): Double? {
        return when {
            source == "Celsius" && destination == "Fahrenheit" -> (value * 1.8) + 32
            source == "Fahrenheit" && destination == "Celsius" -> (value - 32) / 1.8
            source == "Celsius" && destination == "Kelvin" -> value + 273.15
            source == "Kelvin" && destination == "Celsius" -> value - 273.15
            else -> null
        }
    }
}