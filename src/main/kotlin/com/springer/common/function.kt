package com.springer.common

fun <T> identity(): (T) -> T = { t: T -> t }
