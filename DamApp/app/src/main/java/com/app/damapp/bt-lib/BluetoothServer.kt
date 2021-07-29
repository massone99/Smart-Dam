
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import java.io.IOException
import java.util.*

class BluetoothServer(uuid: String?, name: String?, eventListener: Listener?) : Thread() {
    @Volatile
    private var stop = false
    private var btServerSocket: BluetoothServerSocket? = null
    private var listener: Listener? = null
    override fun run() {
        while (!stop) {
            try {
                val socket = btServerSocket!!.accept()
                if (listener != null) {
                    listener!!.onConnectionAccepted(RealBluetoothChannel(socket))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun terminate() {
        stop = true
    }

    interface Listener {
        fun onServerActive()
        fun onConnectionAccepted(btChannel: CommChannel?)
    }

    init {
        try {
            btServerSocket = BluetoothAdapter.getDefaultAdapter()
                .listenUsingRfcommWithServiceRecord(name, UUID.fromString(uuid))
            listener = eventListener
            if (listener != null) {
                listener!!.onServerActive()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}