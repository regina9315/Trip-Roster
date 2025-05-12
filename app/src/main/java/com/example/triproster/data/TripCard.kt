package com.example.triproster.data

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triproster.model.Trip

@Composable
fun TripCard(
    trip: Trip,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = trip.title, style = MaterialTheme.typography.titleMedium)
            Text(text = trip.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Date: ${trip.date}", style = MaterialTheme.typography.labelSmall)

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Trip")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }

                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Trip")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Trip") },
            text = { Text("Are you sure you want to delete this trip?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick()
                    showDeleteDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}
