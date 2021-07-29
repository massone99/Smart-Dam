interface ExtendedRunnable : Runnable {
    fun write(bytes: ByteArray?)
    fun cancel()
}