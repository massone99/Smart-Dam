import android.os.AsyncTask

abstract class ConnectionTask : AsyncTask<Void?, Void?, Int?>() {
    var connectedChannel: BluetoothChannel? = null
    var eventListener: EventListener? = null
    override fun onPostExecute(result: Int?) {
        when (result) {
            CONNECTION_DONE -> if (eventListener != null) {
                eventListener!!.onConnectionActive(connectedChannel)
            }
            CONNECTION_CANCELED -> if (eventListener != null) {
                eventListener!!.onConnectionCanceled()
            }
        }
    }

    interface EventListener {
        fun onConnectionActive(channel: BluetoothChannel?)
        fun onConnectionCanceled()
    }

    companion object {
        const val CONNECTION_DONE = 1
        const val CONNECTION_CANCELED = 2
    }
}