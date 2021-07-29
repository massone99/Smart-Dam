object C {
    const val LIB_TAG = "BluetoothLib"
    const val APP_LOG_TAG = "BT CLN"

    object channel {
        const val MESSAGE_RECEIVED = 0
        const val MESSAGE_SENT = 1
    }

    object message {
        const val MESSAGE_TERMINATOR = '\n'
    }

    object emulator {
        const val HOST_IP = "10.0.2.2"
        const val HOST_PORT = 8080
    }


    object bluetooth {
        const val ENABLE_BT_REQUEST = 1
        const val BT_DEVICE_ACTING_AS_SERVER_NAME =
            "HC-05" //MODIFICARE QUESTA COSTANTE CON IL NOME DEL DEVICE CHE FUNGE DA SERVER
        const val BT_SERVER_UUID = "7ba55836-01eb-11e9-8eb2-f2801f1b9fd1"
    }
}