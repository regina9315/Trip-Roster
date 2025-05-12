package com.example.triproster.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.triproster.model.Trip
import com.google.firebase.database.FirebaseDatabase

@Composable
fun AddTripScreen(navController: NavHostController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) } // Track form submission status

    AddTripContent(
        title = title,
        description = description,
        date = date,
        onTitleChange = { title = it },
        onDescriptionChange = { description = it },
        onDateChange = { date = it },
        onAddClick = {
            // Disable the button to prevent multiple submissions
            isSubmitting = true

            val tripId = FirebaseDatabase.getInstance().getReference("trips").push().key
            if (tripId != null) {
                val newTrip = Trip(id = tripId, title = title, description = description, date = date)
                FirebaseDatabase.getInstance().getReference("trips")
                    .child(tripId)
                    .setValue(newTrip)
                    .addOnCompleteListener { task ->
                        isSubmitting = false  // Re-enable button after operation completes
                        if (task.isSuccessful) {
                            Toast.makeText(navController.context, "Trip added successfully!", Toast.LENGTH_SHORT).show()
                            // Navigate to the trip list after successful addition
                            navController.navigate("tripList") {
                                popUpTo("addTrip") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(navController.context, "Failed to add trip: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(navController.context, "Failed to generate trip ID.", Toast.LENGTH_LONG).show()
                isSubmitting = false
            }
        }
    )
}

@Composable
fun AddTripContent(
    title: String,
    description: String,
    date: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Add New Trip", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = date,
            onValueChange = onDateChange,
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty() // Enable only if all fields are filled
        ) {
            Text("Add Trip")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddTripContentPreview() {
    AddTripContent(
        title = "",
        description = "",
        date = "",
        onTitleChange = {},
        onDescriptionChange = {},
        onDateChange = {},
        onAddClick = {}
    )
}
