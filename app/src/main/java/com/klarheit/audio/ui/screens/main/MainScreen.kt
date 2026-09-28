package com.klarheit.audio.ui.screens.main

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpeakerGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klarheit.audio.R
import com.klarheit.audio.effect.EffectState
import com.klarheit.audio.ui.components.UiDimens
import com.klarheit.audio.ui.screens.debug.DebugLogDialog
import com.klarheit.audio.ui.screens.device.DeviceDialog
import com.klarheit.audio.ui.screens.preset.PresetDialog
import com.klarheit.audio.ui.screens.settings.ExcludedAppsDialog
import com.klarheit.audio.ui.screens.settings.SettingsDialog
import com.klarheit.audio.ui.screens.settings.UpdateDialog
import com.klarheit.audio.ui.screens.status.DriverStatusDialog
import com.klarheit.audio.ui.theme.master_on_container_dark
import com.klarheit.audio.ui.theme.master_on_container_light
import com.klarheit.audio.ui.theme.master_on_onContainer_dark
import com.klarheit.audio.ui.theme.master_on_onContainer_light
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.saveSettingsOnBackground()
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val presets by viewModel.presetList.collectAsStateWithLifecycle()
    val deviceSettings by viewModel.deviceSettingsList.collectAsStateWithLifecycle()
    val driverStatus by viewModel.driverStatus.collectAsStateWithLifecycle()
    val autoStart by viewModel.autoStartEnabled.collectAsStateWithLifecycle()
    val globalMode by viewModel.globalModeEnabled.collectAsStateWithLifecycle()
    val aidlMode by viewModel.aidlModeEnabled.collectAsStateWithLifecycle()
    val debugMode by viewModel.debugModeEnabled.collectAsStateWithLifecycle()
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()
    val excludedApps by viewModel.excludedApps.collectAsStateWithLifecycle()

    var showPresetDialog by remember { mutableStateOf(false) }
    var showDriverStatusDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showDebugLog by remember { mutableStateOf(false) }
    var showDeviceDialog by remember { mutableStateOf(false) }
    var showExcludedAppsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val appVersionName =
        remember {
            try {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
            } catch (_: Exception) {
                ""
            }
        }

    val clearAllProgressStr = stringResource(R.string.preset_clear_all_progress)
    val clearedStr = stringResource(R.string.preset_cleared)

    if (showExcludedAppsDialog) {
        ExcludedAppsDialog(
            excludedApps = excludedApps,
            onToggle = viewModel::setAppExcluded,
            loadInstalledApps = viewModel::loadInstalledApps,
            onDismiss = { showExcludedAppsDialog = false },
        )
    }

    if (showPresetDialog) {
        PresetDialog(
            presets = presets,
            onSave = viewModel::savePreset,
            onLoad = { id ->
                viewModel.loadPreset(id)
                showPresetDialog = false
            },
            onDelete = viewModel::deletePreset,
            onRename = viewModel::renamePreset,
            onUpdate = viewModel::updatePreset,
            onClearAll = {
                viewModel.clearAllPresets(
                    notificationTitle = clearAllProgressStr,
                    successStr = clearedStr,
                ) { count ->
                    Toast.makeText(context, "$clearedStr: $count", Toast.LENGTH_SHORT).show()
                }
            },
            onDismiss = { showPresetDialog = false },
        )
    }

    if (showDriverStatusDialog) {
        LaunchedEffect(Unit) {
            while (true) {
                viewModel.queryDriverStatus()
                delay(500.milliseconds)
            }
        }
        DriverStatusDialog(
            driverStatus = driverStatus,
            onDismiss = { showDriverStatusDialog = false },
        )
    }

    if (showDebugLog) {
        DebugLogDialog(
            onDisableDebug = {
                viewModel.disableDebugMode()
                showDebugLog = false
            },
            onDismiss = { showDebugLog = false },
        )
    }

    if (showDeviceDialog) {
        DeviceDialog(
            devices = deviceSettings,
            activeDeviceId = state.activeDeviceId,
            onRename = viewModel::renameDevice,
            onLoad = viewModel::loadDevicePreset,
            onUpdate = viewModel::saveDevicePreset,
            onDelete = viewModel::deleteDeviceSettings,
            onDismiss = { showDeviceDialog = false },
        )
    }

    val importSuccessStr = stringResource(R.string.import_success)
    val importFailedStr = stringResource(R.string.import_failed)
    val importPresetStr = stringResource(R.string.settings_import_preset)
    val importPresetLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenMultipleDocuments(),
        ) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.importPresetFiles(uris, notificationTitle = importPresetStr, successStr = importSuccessStr) { success ->
                    val msg = if (success) importSuccessStr else importFailedStr
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

    val importKernelStr = stringResource(R.string.settings_import_kernel)
    val importKernelLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenMultipleDocuments(),
        ) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.importKernels(uris, notificationTitle = importKernelStr, successStr = importSuccessStr) { success ->
                    val msg = if (success) importSuccessStr else importFailedStr
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

    val importVdcStr = stringResource(R.string.settings_import_vdc)
    val importVdcLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenMultipleDocuments(),
        ) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.importVdcs(uris, notificationTitle = importVdcStr, successStr = importSuccessStr) { success ->
                    val msg = if (success) importSuccessStr else importFailedStr
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

    if (showSettingsDialog) {
        LaunchedEffect(Unit) { viewModel.queryDriverStatus() }
        SettingsDialog(
            autoStartEnabled = autoStart,
            globalModeEnabled = globalMode,
            aidlModeActive = aidlMode,
            debugModeEnabled = debugMode,
            onGlobalModeChanged = viewModel::toggleGlobalMode,
            onOpenExcludedApps = { showExcludedAppsDialog = true },
            driverStatus = driverStatus,
            appVersionName = appVersionName,
            onAutoStartChanged = viewModel::toggleAutoStart,
            onImportPreset = { importPresetLauncher.launch(arrayOf("application/json")) },
            onImportKernel = {
                importKernelLauncher.launch(
                    arrayOf(
                        "audio/*",
                        "application/octet-stream",
                        "*/*",
                    ),
                )
            },
            onDebugUnlocked = viewModel::enableDebugMode,
            onImportVdc = { importVdcLauncher.launch(arrayOf("*/*")) },
            onCheckUpdate = { viewModel.checkForUpdate() },
            onDismiss = { showSettingsDialog = false },
        )
    }

    val updateCheckingStr = stringResource(R.string.update_checking)
    val updateNoApkStr = stringResource(R.string.update_no_apk_asset)
    val updateCheckFailedFmt = stringResource(R.string.update_check_failed)
    LaunchedEffect(updateState.checking) {
        if (updateState.checking) {
            Toast.makeText(context, updateCheckingStr, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(updateState.error) {
        val err = updateState.error
        if (err != null) {
            Toast.makeText(context, updateCheckFailedFmt.format(err), Toast.LENGTH_LONG).show()
            viewModel.dismissUpdate()
        }
    }
    updateState.release?.let { release ->
        UpdateDialog(
            release = release,
            currentVersion = appVersionName,
            upToDate = updateState.upToDate,
            downloading = updateState.downloading,
            downloadProgress = updateState.downloadProgress,
            onDownloadInstall = {
                viewModel.downloadAndInstall(release) {
                    Toast.makeText(context, updateNoApkStr, Toast.LENGTH_SHORT).show()
                }
            },
            onViewOnGithub = {
                context.startActivity(Intent(Intent.ACTION_VIEW, release.htmlUrl.toUri()))
            },
            onDismiss = { viewModel.dismissUpdate() },
        )
    }

    Scaffold { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = UiDimens.Standard,
                            end = UiDimens.XSmall,
                            top = UiDimens.Medium,
                            bottom = UiDimens.Small,
                        ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f),
                )
                if (debugMode) {
                    IconButton(onClick = { showDebugLog = true }) {
                        Icon(
                            Icons.Default.BugReport,
                            contentDescription = stringResource(R.string.debug_log_title),
                        )
                    }
                }
                IconButton(onClick = { showPresetDialog = true }) {
                    Icon(
                        Icons.Default.LibraryMusic,
                        contentDescription = stringResource(R.string.menu_presets),
                    )
                }
                IconButton(onClick = { showDeviceDialog = true }) {
                    Icon(
                        Icons.Filled.SpeakerGroup,
                        contentDescription = stringResource(R.string.menu_devices),
                    )
                }
                IconButton(onClick = { showDriverStatusDialog = true }) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = stringResource(R.string.menu_driver_status),
                    )
                }
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = stringResource(R.string.menu_settings),
                    )
                }
            }

            val masterOn = state.masterEnable
            val darkTheme = isSystemInDarkTheme()
            val pillContainer =
                when {
                    !masterOn -> MaterialTheme.colorScheme.secondaryContainer
                    darkTheme -> master_on_container_dark
                    else -> master_on_container_light
                }
            val pillContent =
                when {
                    !masterOn -> MaterialTheme.colorScheme.onSecondaryContainer
                    darkTheme -> master_on_onContainer_dark
                    else -> master_on_onContainer_light
                }
            Card(
                onClick = { viewModel.setMasterEnabled(!masterOn) },
                shape = CircleShape,
                colors =
                    CardDefaults.cardColors(
                        containerColor = pillContainer,
                        contentColor = pillContent,
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = UiDimens.Medium)
                        .height(76.dp),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = UiDimens.Large),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text =
                            stringResource(
                                if (masterOn) R.string.master_enabled else R.string.master_disabled,
                            ),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = masterOn,
                        onCheckedChange = { viewModel.setMasterEnabled(it) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(UiDimens.Medium))
            EffectList(
                state = state,
                viewModel = viewModel,
            )
            Spacer(modifier = Modifier.height(UiDimens.FabListPadding))
        }
    }
}

@Composable
private fun EffectList(
    state: EffectState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val targetAlpha = if (state.masterEnable) 1f else 0.38f
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 200),
        label = "effectListAlpha",
    )
    Column(
        modifier = modifier.fillMaxWidth().graphicsLayer { this.alpha = alpha },
    ) {
        MasterLimiterRows(state, viewModel)
        PlaybackGainSection(state, viewModel)
        LUFSTargetingSection(state, viewModel)
        MultibandCompressorSection(state, viewModel)
        FetCompressorSection(state, viewModel)
        DdcSection(state, viewModel)
        SpectrumExtensionSection(state, viewModel)
        EqualizerSection(state, viewModel)
        DynamicEqSection(state, viewModel)
        ConvolverSection(state, viewModel)
        FieldSurroundSection(state, viewModel)
        DiffSurroundSection(state, viewModel)
        StereoImagerSection(state, viewModel)
        HeadphoneSurroundSection(state, viewModel)
        ReverberationSection(state, viewModel)
        DynamicSystemSection(state, viewModel)
        TubeSimulatorSection(state, viewModel)
        PsychoacousticBassSection(state, viewModel)
        KlarheitBassSection(state, viewModel)
        KlarheitBassMonoSection(state, viewModel)
        KlarheitClaritySection(state, viewModel)
        AuditoryProtectionSection(state, viewModel)
        AnalogXSection(state, viewModel)
        SpeakerOptSection(state, viewModel)
    }
}
