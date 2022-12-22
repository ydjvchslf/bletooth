package com.example.bledot.data

data class XYZData(
        var xValue: Double,
        var yValue: Double,
        var zValue: Double,
){
    override fun toString(): String {
        return "[XYZData] xValue: $xValue, yValue: $yValue, zValue: $zValue"
    }
}
