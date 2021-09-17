package com.example.test.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattService
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R
import com.example.test.ble.BluetoothLeService


class DeviceControlActivity : AppCompatActivity() {
    var adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var connected: Boolean = false
    private var bluetoothService: BluetoothLeService? = null
    lateinit var deviceAddress: String
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName,
            service: IBinder
        ) {
            bluetoothService = (service as BluetoothLeService.LocalBinder).getService()
            Log.d("TAG", "onServiceConnected: $bluetoothService")
            bluetoothService?.let { bluetooth ->
                if (!bluetooth.initialize()) {
                    Log.e("TAG", "Unable to initialize Bluetooth")
                    finish()
                }
                bluetooth.connect(deviceAddress)
                Log.d("TAG", "onServiceConnected: service connected")
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothService = null
            Log.d("TAG", "onServiceDisconnected: service disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_control)

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        deviceAddress = intent.getStringExtra("address").toString()
        /*val device=intent.getStringExtra("address") as BluetoothDevice
        device.connectRxGatt()
            .flatMapMaybe { gatt -> gatt.whenConnectionIsReady().map { gatt } }
            .subscribe { rxBluetoothGatt ->

            }*/
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
        Log.d("TAG", "onCreate: address of device " + intent.getStringExtra("address"))
    }

    private val gattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    connected = true
                    BluetoothLeService.STATE_CONNECTED
                    Log.d("TAG", "onReceive: state connected")
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    connected = false
                    BluetoothLeService.STATE_DISCONNECTED
                    Log.d("TAG", "onReceive: state disconnected")
                }
                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    displayGattServices(bluetoothService?.getSupportedGattServices())
                }
            }
        }
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(gattUpdateReceiver)
    }

    private fun displayGattServices(gattServices: MutableList<BluetoothGattService>?) {
        /* if (gattServices == null) return
         var uuid: String?
         val unknownServiceString: String = "unknown_service"
         val unknownCharaString: String = "unknown_characteristic"
         val gattServiceData: MutableList<HashMap<String, String>> = mutableListOf()
         val gattCharacteristicData: MutableList<ArrayList<HashMap<String, String>>> =
             mutableListOf()
         var mGattCharacteristics = mutableListOf<BluetoothGattCharacteristic>()

         gattServices.forEach { gattService ->
             val currentServiceData = HashMap<String, String>()
             uuid = gattService.uuid.toString()
             currentServiceData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownServiceString)
             currentServiceData[LIST_UUID] = uuid
             gattServiceData += currentServiceData

             val gattCharacteristicGroupData: ArrayList<HashMap<String, String>> = arrayListOf()
             val gattCharacteristics = gattService.characteristics
             val charas: MutableList<BluetoothGattCharacteristic> = mutableListOf()

             // Loops through available Characteristics.
             gattCharacteristics.forEach { gattCharacteristic ->
                 charas += gattCharacteristic
                 val currentCharaData: HashMap<String, String> = hashMapOf()
                 uuid = gattCharacteristic.uuid.toString()
                 currentCharaData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownCharaString)
                 currentCharaData[LIST_UUID] = uuid
                 gattCharacteristicGroupData += currentCharaData
             }
             mGattCharacteristics += charas
             gattCharacteristicData += gattCharacteristicGroupData
         }*/
    }
}