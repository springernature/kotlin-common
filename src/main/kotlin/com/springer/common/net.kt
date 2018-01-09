package com.springer.common

import java.io.IOException
import java.net.ServerSocket

fun findFreePort(): Int {
    return try {
        ServerSocket(0).use { socket -> return socket.localPort }
    } catch (e: IOException) {
        -1
    }
}