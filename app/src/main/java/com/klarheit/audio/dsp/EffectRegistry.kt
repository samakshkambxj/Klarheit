package com.klarheit.audio.klarheit

import android.util.SparseArray
import androidx.core.util.size
import com.klarheit.audio.utils.FileLogger

class EffectRegistry {
    var aidlTypeUuid: Boolean = true

    private val lock = Any()
    private val sessions = SparseArray<KlarheitEffect>()
    private var globalEffect: KlarheitEffect? = null

    fun ensureGlobal(): KlarheitEffect? {
        synchronized(lock) {
            globalEffect?.let { return it }
            val typeUuid =
                if (aidlTypeUuid) KlarheitEffect.EFFECT_TYPE_UUID_AIDL else KlarheitEffect.EFFECT_TYPE_UUID
            val effect = KlarheitEffect(0, typeUuid)
            if (!effect.create()) {
                FileLogger.e("EffectReg", "Failed to create global effect")
                return null
            }
            globalEffect = effect
            FileLogger.i("EffectReg", "Global effect created (aidlType=$aidlTypeUuid)")
        }
        return globalEffect
    }

    fun isSessionOpen(sessionId: Int): Boolean = synchronized(lock) { sessions.get(sessionId) != null }

    fun openSession(
        sessionId: Int,
        packageName: String,
    ): Pair<KlarheitEffect, Boolean>? {
        synchronized(lock) {
            sessions.get(sessionId)?.let { return it to true }
            val typeUuid =
                if (aidlTypeUuid) KlarheitEffect.EFFECT_TYPE_UUID_AIDL else KlarheitEffect.EFFECT_TYPE_UUID
            val effect = KlarheitEffect(sessionId, typeUuid)
            if (!effect.create()) {
                FileLogger.e("EffectReg", "Failed to create effect for session $sessionId ($packageName)")
                return null
            }
            effect.enabled = true
            sessions.put(sessionId, effect)
            FileLogger.i("EffectReg", "Opened session $sessionId for $packageName")
            return effect to false
        }
    }

    fun closeSession(sessionId: Int) {
        synchronized(lock) {
            val effect = sessions.get(sessionId) ?: return
            effect.enabled = false
            effect.release()
            sessions.remove(sessionId)
            FileLogger.i("EffectReg", "Closed session $sessionId")
        }
    }

    fun globalEffect(): KlarheitEffect? = synchronized(lock) { globalEffect }

    fun liveEffects(): List<KlarheitEffect> =
        synchronized(lock) {
            buildList {
                globalEffect?.let { add(it) }
                for (i in 0 until sessions.size) {
                    add(sessions.valueAt(i))
                }
            }
        }

    fun activeEffect(): KlarheitEffect? =
        synchronized(lock) {
            globalEffect?.let { if (it.isCreated) return it }
            for (i in 0 until sessions.size) {
                val effect = sessions.valueAt(i)
                if (effect.isCreated) return effect
            }
            null
        }

    fun releaseAll() {
        synchronized(lock) {
            for (i in 0 until sessions.size) {
                val effect = sessions.valueAt(i)
                effect.enabled = false
                effect.release()
            }
            sessions.clear()
        }
    }

    fun releaseGlobal() {
        synchronized(lock) {
            globalEffect?.let {
                it.enabled = false
                it.release()
            }
            globalEffect = null
        }
    }
}
