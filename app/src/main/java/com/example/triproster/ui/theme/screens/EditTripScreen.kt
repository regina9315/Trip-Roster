package com.example.triproster.ui.theme.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.triproster.model.Trip
import com.google.firebase.database.*

@Composable
fun EditTripScreen(
    navController: NavHostController,
    tripId: String
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch existing trip data
    LaunchedEffect(tripId) {
        val dbRef = FirebaseDatabase.getInstance().getReference("trips").child(tripId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val trip = snapshot.getValue(Trip::class.java)
                if (trip != null) {
                    title = trip.title
                    description = trip.description
                    date = trip.date
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Edit Trip", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val updatedTrip = Trip(id = tripId, title = title, description = description, date = date)
                    FirebaseDatabase.getInstance().getReference("trips")
                        .child(tripId)
                        .setValue(updatedTrip)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                navController.popBackStack()
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Trip")
            }
        }
    }
}
