package com.mhike.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector? = Icons.Default.Warning,
    iconTint: Color? = null,
    isDestructive: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    val dialogShape = RoundedCornerShape(20.dp)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = if (icon != null) {
            {
                // Slightly smaller icon with themed tint (error for destructive, primary otherwise)
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = iconTint ?: if (isDestructive) cs.error else cs.primary
                )
            }
        } else null,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = cs.onSurface
            )
        },
        text = {
            // Centered message with comfortable line length and subtle color
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = message,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            // Use primary for normal actions, error container for destructive
            val btnColors = if (isDestructive) {
                ButtonDefaults.buttonColors(
                    containerColor = cs.error,
                    contentColor = cs.onError
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = cs.primary,
                    contentColor = cs.onPrimary
                )
            }

            Button(
                onClick = onConfirm,
                colors = btnColors,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                if (isDestructive) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = confirmText,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        },
        dismissButton = {
            // Outlined to reduce visual weight; picks up outline color from theme
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = cs.onSurface
                ),
                border = ButtonDefaults.outlinedButtonBorder(true)
            ) {
                Text(
                    text = dismissText,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
                )
            }
        },
        shape = dialogShape,
        containerColor = cs.surface,
        tonalElevation = 6.dp
    )
}

