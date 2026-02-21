package frb.axeron.manager.ui.screen.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderDelete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.generated.destinations.FlashScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import frb.axeron.api.Axeron
import frb.axeron.manager.R
import frb.axeron.manager.ui.component.ClickableItem
import frb.axeron.manager.ui.component.ConfirmResult
import frb.axeron.manager.ui.component.SettingsCategory
import frb.axeron.manager.ui.component.rememberConfirmDialog
import frb.axeron.manager.ui.screen.FlashIt
import frb.axeron.shared.AxeronApiConstant
import frb.axeron.shared.PathHelper
import kotlinx.coroutines.launch

@Composable
fun PathSettings(
    searchText: String,
    navigator: DestinationsNavigator
) {
    val categoryTitle = stringResource(R.string.settings_category_path)
    val matchCategory = shouldShow(searchText, categoryTitle)
    
    val resetPathTitle = stringResource(R.string.reset_path)
    val resetPathSummary = stringResource(R.string.reset_path_desc)
    val showResetPath = matchCategory || shouldShow(searchText, resetPathTitle, resetPathSummary)
    
    val showCategory = showResetPath
    
    if (showCategory) {
        val confirmDialog = rememberConfirmDialog()
        val scope = rememberCoroutineScope()
        
        val title = stringResource(R.string.ask_reset_path)
        val content = stringResource(R.string.ask_reset_path_desc)
        val confirm = stringResource(R.string.reset)
        val dismiss = stringResource(R.string.cancel)
        
        SettingsCategory(
            icon = Icons.Filled.FolderDelete,
            title = categoryTitle,
            isSearching = searchText.isNotEmpty()
        ) {
            if (showResetPath) {
                ClickableItem(
                    icon = Icons.Filled.Restore,
                    title = resetPathTitle,
                    summary = resetPathSummary,
                    onClick = {
                        scope.launch {
                            val confirmResult = confirmDialog.awaitConfirm(
                                title,
                                content = content.format(PathHelper.getWorkingPath(
                                    Axeron.getAxeronInfo().isRoot(),
                                    AxeronApiConstant.folder.PARENT
                                ).absolutePath),
                                confirm = confirm,
                                dismiss = dismiss
                            )
                            if (confirmResult == ConfirmResult.Confirmed) {
                                navigator.navigate(FlashScreenDestination(FlashIt.FlashUninstall))
                            }
                        }
                    }
                )
            }
        }
    }
}
