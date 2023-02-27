package com.example.bledot.data

data class CSVData (
    val name: String,
    val isChecked: Boolean
) {
    override fun toString(): String {
        return "CSVData name: $name, isChecked: $isChecked"
    }
}
