package com.springer.common

fun onShutdown(cleanup: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(Thread {
        cleanup()
    })
}

fun (() -> Unit).runOnShutdown() {
    Runtime.getRuntime().addShutdownHook(Thread {
        this()
    })
}
