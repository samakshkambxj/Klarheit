package com.klarheit.audio.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.klarheit.audio.R

@Composable
fun LabeledDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onDeleteOption: ((Int, String) -> Unit)? = null,
    isOptionDeletable: (Int, String) -> Boolean = { _, _ -> onDeleteOption != null },
) {
    var showPopup by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Pair<Int, String>?>(null) }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { showPopup = true }
                .padding(vertical = UiDimens.XSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = selectedValue,
                style = MaterialTheme.typography.bodyLarge,
                color =
                    if (enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    },
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            title = { Text(text = label) },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = UiDimens.DialogListMaxHeight),
                ) {
                    itemsIndexed(options) { index, option ->
                        val canDelete = onDeleteOption != null && isOptionDeletable(index, option)
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOptionSelected(index, option)
                                        showPopup = false
                                    }.padding(vertical = UiDimens.XSmall),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = option == selectedValue,
                                onClick = {
                                    onOptionSelected(index, option)
                                    showPopup = false
                                },
                            )
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                            )
                            if (canDelete) {
                                IconButton(onClick = { deleteTarget = index to option }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.action_delete),
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
        )
    }

    deleteTarget?.let { (index, name) ->
        ConfirmDialog(
            title = stringResource(R.string.delete_file_title),
            body = stringResource(R.string.delete_file_message, name),
            confirmLabel = stringResource(R.string.action_delete),
            destructive = true,
            onConfirm = {
                onDeleteOption?.invoke(index, name)
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null },
        )
    }
}
