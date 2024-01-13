package com.sesac.stopwatchwithcoroutine.common

 fun Int.getMinutes() = (this / 100) / 60
 fun Int.getSeconds() = (this / 100) % 60
 fun Int.getMilliseconds() = this % 100

const val DELAY_TIME = 10L
const val TIME_FORMAT = "%02d"