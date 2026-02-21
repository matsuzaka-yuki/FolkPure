package frb.axeron.manager.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import frb.axeron.manager.R
import frb.axeron.manager.ui.component.SettingsCategory
import frb.axeron.manager.ui.component.SwitchItem

@Composable
fun AppearanceSettings(
    searchText: String,
    autoThemeEnabled: Boolean,
    onAutoThemeChange: (Boolean) -> Unit,
    darkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    dynamicColorEnabled: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    currentLanguageDisplay: String?,
    onLanguageClick: () -> Unit,
    onPaletteClick: () -> Unit
) {
    val categoryTitle = stringResource(R.string.settings_category_appearance)
    val matchCategory = shouldShow(searchText, categoryTitle)

    val languageTitle = stringResource(R.string.settings_language)
    val showLanguage = matchCategory || shouldShow(searchText, languageTitle, currentLanguageDisplay ?: "")

    val autoThemeTitle = stringResource(R.string.auto_theme)
    val autoThemeSummary = stringResource(R.string.auto_theme_desc)
    val showAutoTheme = matchCategory || shouldShow(searchText, autoThemeTitle, autoThemeSummary)

    val darkThemeTitle = stringResource(R.string.dark_theme)
    val showDarkMode = !autoThemeEnabled && (matchCategory || shouldShow(searchText, darkThemeTitle))

    val dynamicColorTitle = stringResource(R.string.dynamic_color)
    val dynamicColorSummary = stringResource(R.string.dynamic_color_desc)
    val showDynamicColor = matchCategory || shouldShow(searchText, dynamicColorTitle, dynamicColorSummary)

    val colorPaletteTitle = stringResource(R.string.color_palette)
    val colorPaletteSummary = stringResource(R.string.customize_color_palette)
    val showColorPalette = !dynamicColorEnabled && (matchCategory || shouldShow(searchText, colorPaletteTitle, colorPaletteSummary))

    val showCategory = showLanguage || showAutoTheme || showDarkMode || showDynamicColor || showColorPalette
    
    if (showCategory) {
        SettingsCategory(
            icon = Icons.Filled.Palette,
            title = categoryTitle,
            isSearching = searchText.isNotEmpty()
        ) {
            if (showLanguage) {
                androidx.compose.material3.ListItem(
                    headlineContent = { Text(languageTitle) },
                    supportingContent = {
                        Text(
                            text = currentLanguageDisplay ?: stringResource(R.string.system_default),
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.outline
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.Translate,
                            contentDescription = null,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = androidx.compose.material3.ListItemDefaults.colors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    modifier = Modifier
                        .clickable(enabled = true, onClick = onLanguageClick)
                        .padding(horizontal = 8.dp)
                )
            }
            
            if (showAutoTheme) {
                SwitchItem(
                    icon = Icons.Filled.DarkMode,
                    title = autoThemeTitle,
                    summary = autoThemeSummary,
                    checked = autoThemeEnabled,
                    onCheckedChange = onAutoThemeChange
                )
            }
            
            if (showDarkMode) {
                SwitchItem(
                    icon = Icons.Filled.DarkMode,
                    title = darkThemeTitle,
                    summary = null,
                    checked = darkModeEnabled,
                    onCheckedChange = onDarkModeChange
                )
            }
            
            if (showDynamicColor) {
                SwitchItem(
                    icon = Icons.Filled.Palette,
                    title = dynamicColorTitle,
                    summary = dynamicColorSummary,
                    checked = dynamicColorEnabled,
                    onCheckedChange = onDynamicColorChange
                )
            }
            
            if (showColorPalette) {
                androidx.compose.material3.ListItem(
                    headlineContent = { Text(colorPaletteTitle) },
                    supportingContent = {
                        Text(
                            text = colorPaletteSummary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.outline
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.FormatColorFill,
                            contentDescription = null,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = androidx.compose.material3.ListItemDefaults.colors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    modifier = Modifier
                        .clickable(enabled = true, onClick = onPaletteClick)
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}
