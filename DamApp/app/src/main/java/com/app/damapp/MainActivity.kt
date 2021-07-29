package com.app.damapp


import BluetoothChannel
import BluetoothDeviceNotFound
import BluetoothUtils.embeddedDeviceDefaultUuid
import BluetoothUtils.getPairedDeviceByName
import C
import CommChannel
import ConnectToBluetoothServerTask
import ConnectionTask
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import java.util.*


private var btChannel: BluetoothChannel? = null
private var viewModel: ViewModel? = null

var servoValue: UInt = 0u

class MainActivity : ComponentActivity() {

    private val TAG: String = "MainActivity"
    private var updateDataThread: Thread? = null
    private var ui: DamUI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val btAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if(!btAdapter.isEnabled){
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), C.bluetooth.ENABLE_BT_REQUEST);
        }

        class DataUpdater: Runnable {
            override fun run() {

                while (true) {
                    viewModel!!.updateData()
                    ui!!.updateUI()
                    Thread.sleep(3000)
                }
            }
        }

        viewModel = ViewModel(this)
        updateDataThread = Thread(DataUpdater())

        updateDataThread!!.start()

        ui = DamUI(viewModel!!)

        setContent {
            ui!!.init()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_OK) {
            Log.d(C.APP_LOG_TAG, "Bluetooth enabled!")
        }
        if (requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_CANCELED) {
            Log.d(C.APP_LOG_TAG, "Bluetooth not enabled!")
        }
    }
}

@Throws(BluetoothDeviceNotFound::class)
fun connectToBTServer() {
    viewModel!!.bluetoothStatus = "Pending..."

    val serverDevice = getPairedDeviceByName(C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME)

    // !!! UTILIZZARE IL CORRETTO VALORE DI UUID
    val uuid: UUID = embeddedDeviceDefaultUuid
    //final UUID uuid = BluetoothUtils.generateUuidFromString(C.bluetooth.BT_SERVER_UUID);
    ConnectToBluetoothServerTask(serverDevice, uuid, object : ConnectionTask.EventListener {
        override fun onConnectionActive(channel: BluetoothChannel?) {
            btChannel = channel!!
            viewModel!!.bluetoothStatus = "Active"
            btChannel!!.registerListener(object : CommChannel.Listener {
                override fun onMessageReceived(receivedMessage: String?) {
                    Log.d("MainActivity", "Message received from BT")
                }

                override fun onMessageSent(sentMessage: String?) {
                    Log.d("MainActivity", "Message sent with BT")
                }
            })
        }

        override fun onConnectionCanceled() {
            Log.d("MainActivity", "Enable to connect, device not found")
        }
    }).execute()
}

/**
 * Increases dam opening of 30 degrees (the servo has a range of 0-180 degrees)
 */
fun openDam() {
    servoValue += 30u
    if (servoValue > 180u) servoValue = 180u
    try {
        btChannel!!.sendMessage(servoValue.toString())
    } catch (e: Exception) {
        Log.d("MainActivity", "Channel is null")
    }
}

/**
 * Decreases dam opening of 30 degrees (the servo has a range of 0-180 degrees)
 */
fun closeDam() {
    servoValue -= 30u
    if (servoValue > 180u) servoValue = 0u
    try {
        btChannel!!.sendMessage(servoValue.toString())
    } catch (e: Exception) {
        Log.d("MainActivity", "Channel is null")
    }
}
