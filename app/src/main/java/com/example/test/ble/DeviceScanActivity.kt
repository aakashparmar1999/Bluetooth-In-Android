package com.example.test.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.databinding.ActivityDeviceScanBinding

class DeviceScanActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityDeviceScanBinding
    var adapter = BluetoothAdapter.getDefaultAdapter()
    var list = ArrayList<CustomBleModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityDeviceScanBinding.inflate(LayoutInflater.from(this))
        setContentView(viewBinding.root)
        viewBinding.btnScan.setOnClickListener {
            scanLeDevice()
            val arrayadapter = CustomBLEAdapter(this, list)
            Log.d("TAG", "onCreate: " + list.size)
            viewBinding.list.adapter = arrayadapter
            viewBinding.list.layoutManager = LinearLayoutManager(this)
            arrayadapter.notifyDataSetChanged()
            if (!adapter.isEnabled) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, 0)
                arrayadapter.notifyDataSetChanged()
            }
        }
        /*  val bluetoothManager = this.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
          bluetoothManager.rxScan()
              .subscribe { scanResult ->
                  Log.d("TAG", "onCreate: "+scanResult)
                  viewBinding.btnScan.setOnClickListener {
                      list.add(CustomBleModel(scanResult.device))
                      val arrayadapter = CustomBLEAdapter(this, list)
                      viewBinding.list.adapter = arrayadapter
                      viewBinding.list.layoutManager=LinearLayoutManager(this)
                  }
              }*/
    }

    private val bluetoothLeScanner = adapter.bluetoothLeScanner
    private var scanning = false
    private val handler = Handler()

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (result.device != null) {
                list.add(CustomBleModel(result.device))
            }
        }
    }

    private fun scanLeDevice() {
        if (!scanning) {
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, 25000)
            scanning = true
            bluetoothLeScanner.startScan(leScanCallback)
            Toast.makeText(this, "scanning", Toast.LENGTH_SHORT).show()
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }
}
