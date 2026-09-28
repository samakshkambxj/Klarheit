package com.klarheit.audio.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.klarheit.audio.data.model.DsPreset
import com.klarheit.audio.data.model.EqPreset
import com.klarheit.audio.effect.BuiltinPresets

@Composable
fun resolvePresetName(preset: EqPreset): String {
    val resId = preset.nameKey?.let { BuiltinPresets.EQ_PRESET_NAME_RES[it] }
    return if (resId != null) stringResource(resId) else preset.name
}

@Composable
fun resolvePresetName(preset: DsPreset): String {
    val resId = preset.nameKey?.let { BuiltinPresets.DS_PRESET_NAME_RES[it] }
    return if (resId != null) stringResource(resId) else preset.name
}
