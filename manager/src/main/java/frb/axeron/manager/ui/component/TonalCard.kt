package frb.axeron.manager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun TonalCard(
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable () -> Unit
) {
    val finalContainerColor = containerColor ?: MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = finalContainerColor),
        shape = shape
    ) {
        content()
    }
}