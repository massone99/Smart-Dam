
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import java.util.*

object BluetoothUtils {
    private const val DEFAULT_UUID_FOR_EMBEDDED_DEVICES = "00001101-0000-1000-8000-00805F9B34FB"
    fun generateRandomUuid(): UUID {
        return UUID.randomUUID()
    }

    fun generateUuidFromString(uuid: String?): UUID {
        return UUID.fromString(uuid)
    }

    val embeddedDeviceDefaultUuid: UUID
        get() = UUID.fromString(DEFAULT_UUID_FOR_EMBEDDED_DEVICES)
    val pairedDevices: Set<BluetoothDevice>
        get() = BluetoothAdapter.getDefaultAdapter().bondedDevices

    @Throws(BluetoothDeviceNotFound::class)
    fun getPairedDeviceByName(deviceName: String): BluetoothDevice {
        val pairedList = BluetoothAdapter.getDefaultAdapter().bondedDevices
        if (pairedList.size > 0) {
            for (device in pairedList) {
                if (device.name == deviceName) {
                    return device
                }
            }
        }
        throw BluetoothDeviceNotFound()
    }

    @Throws(BluetoothDeviceNotFound::class)
    fun getPairedDeviceByAddress(deviceMacAddress: String): BluetoothDevice {
        val pairedList = BluetoothAdapter.getDefaultAdapter().bondedDevices
        if (pairedList.size > 0) {
            for (device in pairedList) {
                if (device.address == deviceMacAddress) {
                    return device
                }
            }
        }
        throw BluetoothDeviceNotFound()
    }
}