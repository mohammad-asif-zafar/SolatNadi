package com.hathway.solatnadi.core.notification

expect class PlatformAtomicLong(initialValue: Long = 0L) {
    fun incrementAndGet(): Long
    fun get(): Long
    fun set(value: Long)
    fun compareAndSet(expect: Long, update: Long): Boolean
}
