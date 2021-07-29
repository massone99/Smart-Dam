import java.io.IOException
import java.net.InetAddress
import java.net.Socket

class ConnectToEmulatedBluetoothServerTask(eventListener: EventListener?) : ConnectionTask() {
    private var socket: Socket? = null

    protected override fun doInBackground(vararg params: Void?): Int? {
        socket = try {
            Socket(InetAddress.getByName(C.emulator.HOST_IP), C.emulator.HOST_PORT)
        } catch (e: IOException) {
            e.printStackTrace()
            try {
                socket!!.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            return CONNECTION_CANCELED
        }
        connectedChannel = EmulatedBluetoothChannel(socket!!)
        return CONNECTION_DONE
    }

    init {
        this.eventListener = eventListener
    }
}