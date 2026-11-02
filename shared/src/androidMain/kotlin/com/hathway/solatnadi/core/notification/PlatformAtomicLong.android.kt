package com.hathway.solatnadi.core.notification

import java.util.concurrent.atomic.AtomicLong

actual class PlatformAtomicLong actual constructor(initialValue: Long) {
    private val delegate = AtomicLong(initialValue)

    actual fun incrementAndGet(): Long = delegate.incrementAndGet()
    actual fun get(): Long = delegate.get()
    actual fun set(value: Long) = delegate.set(value)
    actual fun compareAndSet(expect: Long, update: Long): Boolean = delegate.compareAndSet(expect, update)
}
