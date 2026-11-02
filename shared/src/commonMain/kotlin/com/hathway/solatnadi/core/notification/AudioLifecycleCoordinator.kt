package com.hathway.solatnadi.core.notification

import com.hathway.solatnadi.presentation.components.ActiveAlarmData

/**
 * Platform-independent production coordinator encapsulating audio playback lifecycle,
 * compound-atomic session state transitions using lock-free compareAndSet (CAS),
 * and active alarm dismissal rules.
 *
 * Compound Atomicity & Overflow Safety Strategy:
 * - Packs both session ID (bits 0-62) and playing state (bit 62) into a single 64-bit AtomicLong word.
 * - State updates (newSession, startPlayback, completion, failure, stopPlayback, snooze, dismissal) are performed atomically via lock-free compareAndSet loops.
 * - Explicit safe exhaustion strategy: when session ID reaches 62-bit max (`0x3FFFFFFFFFFFFFFF`), it throws an `IllegalStateException` rather than wrapping or reusing active session identifiers.
 * - Zero locks are held during long-running native operations or external callbacks.
 */
class AudioLifecycleCoordinator {
    private val stateAtomic = PlatformAtomicLong(0L) // bit 62: isPlaying flag, bits 0-61: sessionId

    val isPlaying: Boolean
        get() = (stateAtomic.get() and (1L shl 62)) != 0L

    val sessionId: Long
        get() = stateAtomic.get() and 0x3FFFFFFFFFFFFFFF

    fun newSession(): Long {
        while (true) {
            val cur = stateAtomic.get()
            val sid = cur and 0x3FFFFFFFFFFFFFFF
            if (sid >= 0x3FFFFFFFFFFFFFFF) {
                throw IllegalStateException("Audio lifecycle session ID space exhausted (4.6 quintillion sessions reached)")
            }
            val nextSid = sid + 1L
            val nextState = (1L shl 62) or nextSid
            if (stateAtomic.compareAndSet(cur, nextState)) {
                return nextSid
            }
        }
    }

    fun startPlayback(alarmData: ActiveAlarmData): Long {
        val id = newSession()
        AlarmStateManager.triggerAlarm(alarmData)
        return id
    }

    fun handleNaturalCompletion(session: Long) {
        while (true) {
            val cur = stateAtomic.get()
            val sid = cur and 0x3FFFFFFFFFFFFFFF
            if (sid != session) return // Stale session, do not modify state
            val nextState = 0L or sid // playing = 0, sessionId = sid
            if (stateAtomic.compareAndSet(cur, nextState)) {
                return
            }
        }
    }

    fun handlePlaybackFailure(session: Long) {
        while (true) {
            val cur = stateAtomic.get()
            val sid = cur and 0x3FFFFFFFFFFFFFFF
            if (sid != session) return
            val nextState = 0L or sid
            if (stateAtomic.compareAndSet(cur, nextState)) {
                return
            }
        }
    }

    fun stopPlayback(): Long {
        while (true) {
            val cur = stateAtomic.get()
            val sid = cur and 0x3FFFFFFFFFFFFFFF
            if (sid >= 0x3FFFFFFFFFFFFFFF) {
                throw IllegalStateException("Audio lifecycle session ID space exhausted")
            }
            val nextSid = sid + 1L
            val nextState = 0L or nextSid // playing = 0, invalidates session immediately
            if (stateAtomic.compareAndSet(cur, nextState)) {
                return nextSid
            }
        }
    }

    fun explicitDismissal() {
        while (true) {
            val cur = stateAtomic.get()
            val sid = cur and 0x3FFFFFFFFFFFFFFF
            if (sid >= 0x3FFFFFFFFFFFFFFF) {
                throw IllegalStateException("Audio lifecycle session ID space exhausted")
            }
            val nextSid = sid + 1L // Atomically invalidate session
            val nextState = 0L or nextSid
            if (stateAtomic.compareAndSet(cur, nextState)) {
                break
            }
        }
        AlarmStateManager.clearActiveAlarm()
    }

    fun snooze(onScheduleSnooze: () -> Unit) {
        while (true) {
            val cur = stateAtomic.get()
            val sid = cur and 0x3FFFFFFFFFFFFFFF
            if (sid >= 0x3FFFFFFFFFFFFFFF) {
                throw IllegalStateException("Audio lifecycle session ID space exhausted")
            }
            val nextSid = sid + 1L // Atomically invalidate session
            val nextState = 0L or nextSid
            if (stateAtomic.compareAndSet(cur, nextState)) {
                break
            }
        }
        onScheduleSnooze()
        AlarmStateManager.clearActiveAlarm()
    }

    fun isStaleSession(session: Long): Boolean {
        return (stateAtomic.get() and 0x3FFFFFFFFFFFFFFF) != session
    }

    fun invalidateSession(): Long {
        while (true) {
            val cur = stateAtomic.get()
            val sid = cur and 0x3FFFFFFFFFFFFFFF
            if (sid >= 0x3FFFFFFFFFFFFFFF) {
                throw IllegalStateException("Audio lifecycle session ID space exhausted")
            }
            val nextSid = sid + 1L
            val nextState = 0L or nextSid
            if (stateAtomic.compareAndSet(cur, nextState)) {
                return nextSid
            }
        }
    }
}
