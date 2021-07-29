import android.util.Log
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.Socket
import java.util.*

class EmulatedBluetoothChannel internal constructor(socket: Socket) : BluetoothChannel() {
    override val remoteDeviceName: String
        get() = "Arduino through PC"

    /**
     *
     */
    private inner class TcpWorker internal constructor(private val socket: Socket) :
        ExtendedRunnable {
        private val inputStream: InputStream?
        private val outputStream: OutputStream?
        override fun run() {
            while (true) {
                try {
                    val input = DataInputStream(inputStream)
                    var readbuffer = StringBuffer()
                    var inputByte: Byte
                    while (input.readByte().also { inputByte = it }.toInt() != 0) {
                        val chr = inputByte.toChar()
                        if (chr != C.message.MESSAGE_TERMINATOR) {
                            readbuffer.append(chr)
                        } else {
                            val inputString = readbuffer.toString()
                            val receivedMessage = btChannelHandler.obtainMessage(
                                C.channel.MESSAGE_RECEIVED,
                                inputString.toByteArray().size,
                                -1,
                                inputString.toByteArray()
                            )
                            receivedMessage.sendToTarget()
                            readbuffer = StringBuffer()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun write(bytes: ByteArray?) {
            try {
                val bytesToBeSent = Arrays.copyOf(bytes, bytes!!.size + 1)
                bytesToBeSent[bytesToBeSent.size - 1] = C.message.MESSAGE_TERMINATOR.toByte()
                outputStream!!.write(bytesToBeSent)
                val writtenMsg =
                    btChannelHandler.obtainMessage(C.channel.MESSAGE_SENT, -1, -1, bytes)
                writtenMsg.sendToTarget()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun cancel() {
            try {
                socket.close()
            } catch (e: IOException) {
                Log.e(C.LIB_TAG, "Could not close the connect socket", e)
            }
        }

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null
            try {
                tmpIn = socket.getInputStream()
            } catch (e: IOException) {
                Log.e(C.LIB_TAG, "Error occurred when creating input stream", e)
            }
            try {
                tmpOut = socket.getOutputStream()
            } catch (e: IOException) {
                Log.e(C.LIB_TAG, "Error occurred when creating output stream", e)
            }
            inputStream = tmpIn
            outputStream = tmpOut
        }
    }

    init {
        worker = TcpWorker(socket)
        Thread(worker).start()
    }
}