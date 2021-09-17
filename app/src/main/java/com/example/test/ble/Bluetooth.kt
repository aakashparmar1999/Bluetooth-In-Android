package com.example.test.ble

import android.R
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivityBluetoothBinding
import java.lang.reflect.Method


class Bluetooth : AppCompatActivity() {
    lateinit var viewBinding: ActivityBluetoothBinding
    val adapter = BluetoothAdapter.getDefaultAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityBluetoothBinding.inflate(LayoutInflater.from(this))
        setContentView(viewBinding.root)


        if (adapter == null) {
            Toast.makeText(this, "device not supported", Toast.LENGTH_SHORT).show()
        }
        viewBinding.btnOn.setOnClickListener {
            if (!adapter.isEnabled) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, 0)
            }
        }
        viewBinding.btnDiscoverable.setOnClickListener {
            if (!adapter.isDiscovering) {
                Toast.makeText(this, "making device discoverable", Toast.LENGTH_SHORT).show()
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                startActivityForResult(intent, 0)
            }
        }
        viewBinding.btnOff.setOnClickListener {
            if (adapter.isEnabled) {
                adapter.disable()
                Toast.makeText(this, "bluetooth turned off", Toast.LENGTH_SHORT).show()
            } else if (!adapter.isEnabled && !adapter.isDiscovering) {
                Toast.makeText(this, "bluetooth is already off", Toast.LENGTH_SHORT).show()
            }
        }
        viewBinding.btnPaired.setOnClickListener {
            if (adapter.isEnabled){
                val pairedDevices = adapter!!.bondedDevices
                var devices=pairedDevices as MutableSet<BluetoothDevice>
                val list = ArrayList<Any>()
                for (bt in (pairedDevices as MutableSet<BluetoothDevice>?)!!) {
                    list.add(bt)
                }
                Toast.makeText(this, "Showing Paired Devices", Toast.LENGTH_SHORT).show()
                val adapter1: ArrayAdapter<*> =
                    ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
                viewBinding.listviewPaired.adapter = adapter1
                var filter=IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
                registerReceiver(BluetoothReceiver1,filter)
               /* viewBinding.listviewPaired.setOnItemClickListener { parent, view, position, id ->
                    Toast.makeText(this, ""+(list[position] as BluetoothDevice).toString(), Toast.LENGTH_SHORT).show()
                    (list[position] as BluetoothDevice).createBond()
                }*/
            }
            else {
                Toast.makeText(this, "please turn on bluetooth", Toast.LENGTH_SHORT).show()
            }
        }
        viewBinding.btnList.setOnClickListener {
            if (adapter.isEnabled) {
                adapter.startDiscovery()
                Toast.makeText(this@Bluetooth, "showing available devices", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "please turn on bluetooth", Toast.LENGTH_SHORT).show()
            }
        }
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(BluetoothReceiver, filter)
    }

    private val BluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        var list= ArrayList<Any>()
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent
                    .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device!!.name!=null && device.name !in list){
                    list.add(device)
                }
                viewBinding.listview.adapter = ArrayAdapter<Any>(
                    context,
                    R.layout.simple_list_item_1, list
                )
                viewBinding.listview.setOnItemClickListener { parent, view, position, id ->
                    Toast.makeText(this@Bluetooth, ""+(list[position] as BluetoothDevice).name, Toast.LENGTH_SHORT).show()

                }
                viewBinding.listview.setOnItemLongClickListener { parent, view, position, id ->
                    (list[position] as BluetoothDevice).createBond()
                }
            }
        }
    }
    private val BluetoothReceiver1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var action=intent.action
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                var device=intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device?.bondState == BluetoothDevice.BOND_BONDED){

                }
                if (device?.bondState == BluetoothDevice.BOND_BONDING){

                }
                if (device?.bondState == BluetoothDevice.BOND_NONE){

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(BluetoothReceiver)
        unregisterReceiver(BluetoothReceiver1)
    }
}

/*
viewBinding.listviewPaired.setOnItemClickListener { _, _, position, _ ->
    list[position]
    Toast.makeText(
        this,
        "Pairing to bluetooth...Please wait until pairing success",
        Toast.LENGTH_SHORT
    ).show()
}*/
