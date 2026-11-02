package com.hathway.solatnadi.core.notification

import kotlin.concurrent.AtomicLong

actual class PlatformAtomicLong actual constructor(initialValue: Long) {
    private val delegate = AtomicLong(initialValue)

    actual fun incrementAndGet(): Long = delegate.addAndGet(1L)
    actual fun get(): Long = delegate.value
    actual fun set(value: Long) { delegate.value = value }
    actual fun compareAndSet(expect: Long, update: Long): Boolean = delegate.compareAndSet(expect, update)
}
