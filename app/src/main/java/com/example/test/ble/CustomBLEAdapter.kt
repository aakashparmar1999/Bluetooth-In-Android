package com.example.test.ble
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R

class CustomBLEAdapter(var context: Context,var list: ArrayList<CustomBleModel>) :
    RecyclerView.Adapter<CustomBLEAdapter.BleViewHolder>() {
    class BleViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var name: TextView =itemView.findViewById(R.id.tv_ble_name)
        var address: TextView =itemView.findViewById(R.id.tv_ble_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.custom_ble_layout,parent,false)
        return BleViewHolder(view)
    }

    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {
        holder.address.text = list[position].BleAddress.toString()
        if (list[position].BleAddress.name==null){
            holder.name.text="_"
        }else{
            holder.name.text = list[position].BleAddress.name.toString()
        }
        holder.itemView.setOnClickListener {
            val intent= Intent(context, DeviceControlActivity::class.java)
            intent.putExtra("address",list[position].BleAddress.toString())
            Log.d("TAG", "onBindViewHolder: "+list[position].BleAddress)
            Log.d("TAG", "onBindViewHolder: "+list[position].BleAddress.name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
