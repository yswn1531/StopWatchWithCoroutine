package com.sesac.stopwatchwithcoroutine.common

 fun Int.getMinutes() = (this / 100) / 60
 fun Int.getSeconds() = (this / 100) % 60
 fun Int.getMilliseconds() = this % 100


