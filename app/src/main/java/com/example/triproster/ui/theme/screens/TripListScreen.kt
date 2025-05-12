package com.example.triproster.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.triproster.data.TripCard
import com.example.triproster.model.Trip
import com.google.firebase.database.*

@Composable
fun TripListScreen(navController: NavHostController) {
    val tripList = remember { mutableStateListOf<Trip>() }
    val isLoading = remember { mutableStateOf(true) }

    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf("Ascending") }

    // Firebase listener
    LaunchedEffect(Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("trips")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tripList.clear()
                for (tripSnap in snapshot.children) {
                    val trip = tripSnap.getValue(Trip::class.java)
                    if (trip != null) {
                        tripList.add(trip.copy(id = tripSnap.key ?: ""))
                    }
                }
                isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading.value = false
            }
        }

        dbRef.addValueEventListener(listener)
        onDispose { dbRef.removeEventListener(listener) }
    }

    // Filtering and Sorting
    val filteredSortedTrips = tripList
        .filter { it.title.contains(searchQuery, ignoreCase = true) }
        .sortedBy { it.date }

    val displayTrips = if (sortOrder == "Descending") filteredSortedTrips.reversed() else filteredSortedTrips

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        TripListContent(
            trips = displayTrips,
            onAddTripClick = { navController.navigate("addTrip") },
            onEditTripClick = { tripId -> navController.navigate("editTrip/$tripId") },
            onDeleteTripClick = { trip ->
                val dbRef = FirebaseDatabase.getInstance().getReference("trips")
                dbRef.child(trip.id).removeValue()
            },
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            sortOrder = sortOrder,
            onSortOrderChange = { sortOrder = it }
        )
    }
}

fun onDispose(function: () -> Unit) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListContent(
    trips: List<Trip>,
    onAddTripClick: () -> Unit,
    onEditTripClick: (String) -> Unit,
    onDeleteTripClick: (Trip) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortOrder: String,
    onSortOrderChange: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Trip List") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTripClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Trip")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search Trips") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Sort dropdown
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text("Sort by Date: $sortOrder")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Ascending") }, onClick = {
                        onSortOrderChange("Ascending")
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Descending") }, onClick = {
                        onSortOrderChange("Descending")
                        expanded = false
                    })
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Trip list or empty state
            AnimatedVisibility(visible = trips.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No trips found. Try adding or adjusting your search.")
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(trips, key = { it.id }) { trip ->
                    TripCard(
                        trip = trip,
                        onEditClick = { onEditTripClick(trip.id) },
                        onDeleteClick = { onDeleteTripClick(trip) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripListScreenPreview() {
    val sampleTrips = listOf(
        Trip(
            id = "1",
            title = "Trip to Goa",
            description = "Beach vacation with friends.",
            date = "2025-06-20"
        ),
        Trip(
            id = "2",
            title = "Trip to Manali",
            description = "Hiking and hot springs.",
            date = "2025-07-15"
        )
    )
    TripListContent(
        trips = sampleTrips,
        onAddTripClick = {},
        onEditTripClick = {},
        onDeleteTripClick = {},
        searchQuery = "",
        onSearchQueryChange = {},
        sortOrder = "Ascending",
        onSortOrderChange = {}
    )
}
