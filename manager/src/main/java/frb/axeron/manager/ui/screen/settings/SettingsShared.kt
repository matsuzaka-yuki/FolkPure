package frb.axeron.manager.ui.screen.settings

fun shouldShow(searchText: String, vararg texts: String?): Boolean {
    if (searchText.isEmpty()) return true
    return texts.any { it?.contains(searchText, ignoreCase = true) == true }
}
