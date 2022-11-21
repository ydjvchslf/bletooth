package com.example.bledot.data

import com.xsens.dot.android.sdk.models.XsensDotDevice

data class BleDevice (
    val name: String,
    val macAddress: String,
    var connectState: Int,
    val tag: String,
    val batteryState: Int,
    val batteryPercent: Int
) {
    companion object {
        fun fromHashMapList(sensorMap: ArrayList<HashMap<String, Any>>) =
            sensorMap.map { sensorMap ->
                BleDevice(
                    name = sensorMap["KEY_NAME"] as String,
                    macAddress = sensorMap["KEY_DEVICE"].toString(),
                    connectState = sensorMap["KEY_CONNECTION_STATE"] as Int,
                    tag = sensorMap["KEY_TAG"] as String,
                    batteryState = sensorMap["KEY_BATTERY_STATE"] as Int,
                    batteryPercent = sensorMap["KEY_BATTERY_PERCENTAGE"] as Int,
                )
            }

        fun fromXsDeviceToBleDevice(xsDevice: XsensDotDevice): BleDevice {
            return BleDevice (
                        name = xsDevice.name,
                        macAddress = xsDevice.address,
                        connectState = xsDevice.connectionState,
                        tag = xsDevice.tag,
                        batteryState = xsDevice.batteryState,
                        batteryPercent = xsDevice.batteryPercentage
            )
        }
    }

    override fun toString(): String {
        return "$macAddress [$name], connectState: $connectState"
    }
}
