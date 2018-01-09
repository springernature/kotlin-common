package com.springer.common

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.stackTraceToString() =
    StringWriter()
        .also { printStackTrace(PrintWriter(it)) }
        .toString()
