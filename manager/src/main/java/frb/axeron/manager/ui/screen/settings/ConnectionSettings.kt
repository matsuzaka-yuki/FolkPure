package frb.axeron.manager.ui.screen.settings

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import frb.axeron.adb.util.AdbEnvironment
import frb.axeron.manager.R
import frb.axeron.manager.ui.component.ClickableItem
import frb.axeron.manager.ui.component.SettingsCategory
import frb.axeron.manager.ui.component.SwitchItem

@Composable
fun ConnectionSettings(
    searchText: String,
    isTcpModeEnabled: Boolean,
    tcpPortInt: Int?,
    onTcpModeChange: (Boolean) -> Unit,
    onTcpPortChange: (Int?) -> Unit
) {
    val context = LocalContext.current
    
    val categoryTitle = stringResource(R.string.settings_category_connection)
    val matchCategory = shouldShow(searchText, categoryTitle)
    
    val tcpModeTitle = stringResource(R.string.tcp_mode)
    val tcpModeSummary = stringResource(R.string.tcp_mode_desc)
    val showTcpMode = matchCategory || shouldShow(searchText, tcpModeTitle, tcpModeSummary)
    
    val tcpPortTitle = stringResource(R.string.tcp_port)
    val showTcpPort = isTcpModeEnabled && (matchCategory || shouldShow(searchText, tcpPortTitle))
    
    val showCategory = showTcpMode || showTcpPort
    
    if (showCategory) {
        SettingsCategory(
            icon = Icons.Filled.Adb,
            title = categoryTitle,
            isSearching = searchText.isNotEmpty()
        ) {
            if (showTcpMode) {
                SwitchItem(
                    icon = Icons.Filled.Wifi,
                    title = tcpModeTitle,
                    summary = tcpModeSummary,
                    checked = isTcpModeEnabled,
                    onCheckedChange = onTcpModeChange
                )
            }
            
            if (showTcpPort) {
                TcpPortItem(
                    tcpPortInt = tcpPortInt,
                    onTcpPortChange = onTcpPortChange
                )
            }
        }
    }
}

@Composable
private fun TcpPortItem(
    tcpPortInt: Int?,
    onTcpPortChange: (Int?) -> Unit
) {
    var tcpPortText by remember {
        mutableStateOf(tcpPortInt?.toString() ?: "5555")
    }
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val portInt = tcpPortText.toIntOrNull()
    val isError = tcpPortText.isNotEmpty() &&
            (portInt == null || portInt !in 1024..65535)

    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state ->
                    isFocused = state.isFocused
                },
            value = tcpPortText,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } && newValue.length <= 5) {
                    tcpPortText = newValue
                }
            },
            label = {
                Text(stringResource(R.string.tcp_port))
            },
            supportingText = {
                AnimatedVisibility(
                    visible = isError,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.invalid_port),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            isError = isError,
            trailingIcon = {
                if (isFocused) {
                    IconButton(
                        enabled = !isError && portInt != null,
                        onClick = {
                            portInt?.let {
                                onTcpPortChange(it)
                                focusManager.clearFocus()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save TCP port"
                        )
                    }
                } else if (tcpPortInt != AdbEnvironment.getAdbTcpPort()) {
                    val reactiveToChange = stringResource(R.string.reactive_to_apply)
                    IconButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                reactiveToChange,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Re-Activate FolkPure"
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}
