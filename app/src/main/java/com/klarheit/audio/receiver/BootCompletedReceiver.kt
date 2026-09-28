package com.klarheit.audio.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.klarheit.audio.data.repository.KlarheitRepository
import com.klarheit.audio.service.KlarheitService
import com.klarheit.audio.utils.FileLogger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: KlarheitRepository

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val autoStart =
            runBlocking {
                repository.getBooleanPreference("auto_start", true).first()
            }
        if (!autoStart) return

        try {
            KlarheitService.startService(context)
        } catch (e: Exception) {
            FileLogger.e(
                "BootReceiver",
                "Cannot start FGS from boot, will start on next app open",
                e,
            )
        }
    }
}
