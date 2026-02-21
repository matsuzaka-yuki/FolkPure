package frb.axeron.manager.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import frb.axeron.manager.R
import frb.axeron.manager.ui.component.SettingsCategory
import frb.axeron.manager.ui.component.SwitchItem

@Composable
fun ActivationSettings(
    searchText: String,
    axeronRunning: Boolean,
    isActivateOnBootEnabled: Boolean,
    isIgniteWhenRelogEnabled: Boolean,
    onActivateOnBootChange: (Boolean) -> Unit,
    onIgniteWhenRelogChange: (Boolean) -> Unit,
    isShizukuActive: Boolean,
    onShizukuInterceptChange: (Boolean) -> Unit
) {
    val categoryTitle = stringResource(R.string.settings_category_activation)
    val matchCategory = shouldShow(searchText, categoryTitle)
    
    val shizukuTitle = stringResource(R.string.axeron_permission)
    val shizukuSummary = stringResource(R.string.axeron_permission_desc)
    val showShizuku = axeronRunning && (matchCategory || shouldShow(searchText, shizukuTitle, shizukuSummary))
    
    val activateOnBootTitle = stringResource(R.string.active_on_boot)
    val activateOnBootSummary = stringResource(R.string.active_on_boot_desc)
    val showActivateOnBoot = matchCategory || shouldShow(searchText, activateOnBootTitle, activateOnBootSummary)
    
    val igniteWhenRelogTitle = stringResource(R.string.ignite_when_relog)
    val igniteWhenRelogSummary = stringResource(R.string.ignite_when_relog_desc)
    val showIgniteWhenRelog = matchCategory || shouldShow(searchText, igniteWhenRelogTitle, igniteWhenRelogSummary)
    
    val showCategory = showShizuku || showActivateOnBoot || showIgniteWhenRelog
    
    if (showCategory) {
        SettingsCategory(
            icon = Icons.Filled.RestartAlt,
            title = categoryTitle,
            isSearching = searchText.isNotEmpty()
        ) {
            if (showShizuku) {
                SwitchItem(
                    icon = Icons.Filled.AdminPanelSettings,
                    title = shizukuTitle,
                    summary = shizukuSummary,
                    checked = isShizukuActive,
                    onCheckedChange = onShizukuInterceptChange
                )
            }
            
            if (showActivateOnBoot) {
                SwitchItem(
                    icon = Icons.Filled.RestartAlt,
                    title = activateOnBootTitle,
                    summary = activateOnBootSummary,
                    checked = isActivateOnBootEnabled,
                    onCheckedChange = onActivateOnBootChange
                )
            }
            
            if (showIgniteWhenRelog) {
                SwitchItem(
                    icon = Icons.Filled.Refresh,
                    title = igniteWhenRelogTitle,
                    summary = igniteWhenRelogSummary,
                    checked = isIgniteWhenRelogEnabled,
                    onCheckedChange = onIgniteWhenRelogChange
                )
            }
        }
    }
}
