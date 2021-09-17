package com.example.test.ble
import android.bluetooth.BluetoothDevice

class CustomBleModel(var BleAddress:BluetoothDevice) {
    fun getAddress():BluetoothDevice{
        return this.BleAddress
    }
    fun setPosition(BLD:BluetoothDevice){
        this.BleAddress=BLD
    }
}
