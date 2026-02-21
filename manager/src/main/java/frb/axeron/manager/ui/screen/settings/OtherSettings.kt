package frb.axeron.manager.ui.screen.settings

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Web
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import com.ramcosta.composedestinations.generated.destinations.SettingsEditorScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import frb.axeron.api.core.AxeronSettings
import frb.axeron.manager.R
import frb.axeron.manager.ui.component.ClickableItem
import frb.axeron.manager.ui.component.SettingsCategory
import frb.axeron.manager.ui.component.SwitchItem
import frb.axeron.manager.ui.component.rememberLoadingDialog
import frb.axeron.manager.ui.util.checkNewVersion
import frb.axeron.manager.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun OtherSettings(
    searchText: String,
    axeronRunning: Boolean,
    navigator: DestinationsNavigator,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loadingDialog = rememberLoadingDialog()
    val prefs = AxeronSettings.getPreferences()

    val categoryTitle = stringResource(R.string.settings_category_other)
    val matchCategory = shouldShow(searchText, categoryTitle)

    val settingsEditorTitle = stringResource(R.string.settings_editor)
    val showSettingsEditor = axeronRunning && (matchCategory || shouldShow(searchText, settingsEditorTitle))

    val developerModeTitle = stringResource(R.string.enable_developer_mode)
    val developerModeSummary = stringResource(R.string.enable_developer_mode_msg)
    val showDeveloperMode = matchCategory || shouldShow(searchText, developerModeTitle, developerModeSummary)

    val webDebugTitle = stringResource(R.string.enable_debugging_webview)
    val webDebugSummary = stringResource(R.string.enable_debugging_webview_msg)
    val showWebDebug = settingsViewModel.isDeveloperModeEnabled && (matchCategory || shouldShow(searchText, webDebugTitle, webDebugSummary))

    val updateTitle = stringResource(R.string.settings_check_update)
    val showUpdate = matchCategory || shouldShow(searchText, updateTitle)

    val autoUpdateTitle = stringResource(R.string.settings_auto_update_check)
    val autoUpdateSummary = stringResource(R.string.settings_auto_update_check_summary)
    val showAutoUpdate = matchCategory || shouldShow(searchText, autoUpdateTitle, autoUpdateSummary)

    val showCategory = showSettingsEditor || showDeveloperMode || showWebDebug || showUpdate || showAutoUpdate

    var showUpdateDialog by remember { mutableStateOf(false) }
    var autoUpdateCheck by remember { mutableStateOf(prefs.getBoolean("auto_update_check", true)) }

    if (showCategory) {
        SettingsCategory(
            icon = Icons.Filled.MoreHoriz,
            title = categoryTitle,
            isSearching = searchText.isNotEmpty()
        ) {
            if (showSettingsEditor) {
                ClickableItem(
                    icon = Icons.Filled.Edit,
                    title = settingsEditorTitle,
                    onClick = {
                        navigator.navigate(SettingsEditorScreenDestination)
                    }
                )
            }

            if (showUpdate) {
                ClickableItem(
                    icon = Icons.Filled.Update,
                    title = updateTitle,
                    onClick = {
                        scope.launch {
                            loadingDialog.show()
                            val hasUpdate = checkNewVersion()
                            loadingDialog.hide()
                            if (hasUpdate) {
                                showUpdateDialog = true
                            } else {
                                Toast.makeText(context, R.string.update_latest, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }

            if (showAutoUpdate) {
                SwitchItem(
                    icon = Icons.Filled.Autorenew,
                    title = autoUpdateTitle,
                    summary = autoUpdateSummary,
                    checked = autoUpdateCheck,
                    onCheckedChange = {
                        autoUpdateCheck = it
                        prefs.edit { putBoolean("auto_update_check", it) }
                    }
                )
            }

            if (showDeveloperMode) {
                SwitchItem(
                    icon = Icons.Filled.DeveloperMode,
                    title = developerModeTitle,
                    summary = developerModeSummary,
                    checked = settingsViewModel.isDeveloperModeEnabled,
                    onCheckedChange = { settingsViewModel.setDeveloperOptions(it) }
                )
            }

            if (showWebDebug) {
                SwitchItem(
                    icon = Icons.Filled.Web,
                    title = webDebugTitle,
                    summary = webDebugSummary,
                    checked = settingsViewModel.isWebDebuggingEnabled,
                    onCheckedChange = { settingsViewModel.setWebDebugging(it) }
                )
            }
        }
    }

    if (showUpdateDialog) {
        frb.axeron.manager.ui.component.UpdateDialog(
            onDismiss = { showUpdateDialog = false },
            onUpdate = {
                showUpdateDialog = false
                frb.axeron.manager.ui.util.openUpdateUrl(context)
            }
        )
    }
}
