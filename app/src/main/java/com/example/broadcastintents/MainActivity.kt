package com.example.broadcastintents

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Switch
import android.widget.Toast
import com.example.broadcastintents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding?=null
    private var  bluetoothAdapter: BluetoothAdapter?=null
    private var broadcastReceiver:BroadcastReceiver?=null
    private var connectivityManager: ConnectivityManager?=null


      //airplane mode change
    private val AirplaneModeReceiver= object:BroadcastReceiver(){
        override fun onReceive(context: Context?,intent: Intent) {
            if (intent?.action==Intent.ACTION_AIRPLANE_MODE_CHANGED){
                // update ui based on mode change
                updateUI()
            }
        }
    }
    // data change status mode
    private val DataModeReceiver= object :BroadcastReceiver(){
        override fun onReceive(context: Context?,intent: Intent?) {

            // Check the network state and take appropriate action
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                // Network is available
                Toast.makeText(context, "Network available", Toast.LENGTH_SHORT).show()

            } else {
                // Network is unavailable
                Toast.makeText(context, "Network unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //power mode
    private val PowerModeReceiver=object :BroadcastReceiver(){
        override fun onReceive(context: Context?,intent: Intent?) {

            if(intent?.action==Intent.ACTION_BATTERY_CHANGED){
                // update ui based on change

            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        

        val intentFilter=IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        registerReceiver(AirplaneModeReceiver,intentFilter)

        //update ui based on current airplane mode

        updateUI()
               //data connectivity

          //bluetooth connectivity
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        // Set the initial Bluetooth status
        //setBluetoothStatusText(bluetoothAdapter.isEnabled)

        // Register a BroadcastReceiver to receive updates when the Bluetooth state changes
        val bluetoothStateIntentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStateReceiver, bluetoothStateIntentFilter)

          //register a broadcast receiver to receive updates when the power state change


    }

    override fun onDestroy() {
        super.onDestroy()
        //unregister airplane mode receiver
        unregisterReceiver(AirplaneModeReceiver)
        unregisterReceiver(DataModeReceiver)
        unregisterReceiver(bluetoothStateReceiver)
    }

    // status for airplane mode
    private fun updateUI(){
        val isAirplaneModeOn=isAirplaneModeOn()
        binding?.airmode?.text=if (isAirplaneModeOn) "Airplane Mode ON" else {
            "Airplane Mode OFF"
        }
        binding?.switch1?.isChecked=isAirplaneModeOn
    }
     //airplane mode
    private fun isAirplaneModeOn():Boolean{
        return Settings.Global.getInt(contentResolver,Settings.Global.AIRPLANE_MODE_ON,0)!=0
    }
     //update data status
    private fun updateDataStatus() {
        val mobileDataEnabled = connectivityManager?.isDefaultNetworkActive
        if (mobileDataEnabled == true) {
            binding?.datamode?.text = "Mobile data is ON"
        } else if(mobileDataEnabled==false) {
            binding?.datamode?.text = "Mobile data is OFF"
        }
    }
      //bluetooth mode change
    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_ON -> setBluetoothStatusText(true)
                    BluetoothAdapter.STATE_OFF -> setBluetoothStatusText(false)
                }
            }
        }
    }
    //bluetooth mode
    private fun setBluetoothStatusText(isEnabled: Boolean) {
        val statusText = if (isEnabled) "Bluetooth is ON" else "Bluetooth is OFF"
        binding?.bluetoothmode?.text = statusText
        binding?.switch3?.isChecked=isEnabled
    }


    //power ui
    private fun isPowerModeOn():Boolean{
        return true
    }
    private fun updatePowerUI(){
        val isPowerModeOn=isPowerModeOn()
        binding?.powermode?.text=if (isPowerModeOn)"Power is Pluged in" else{
            "Power is Plugeg off"
        }
        binding?.switch4?.isChecked=isPowerModeOn
    }

}