package com.example.triproster.ui.theme.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.triproster.model.Trip
import com.example.triproster.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val user = FirebaseAuth.getInstance().currentUser
    val userName = user?.displayName ?: user?.email ?: "User"
    val userId = user?.uid ?: return

    val tripCount = remember { mutableIntStateOf(0) }
    val nextTrip = remember { mutableStateOf<Trip?>(null) }
    val trips = remember { mutableStateListOf<Trip>() }

    val showDialog = remember { mutableStateOf(false) }
    val summaryText = remember { mutableStateOf(TextFieldValue()) }

    val currentYear = LocalDate.now().year
    val currentDate = remember { mutableStateOf(LocalDate.now()) }
    val daysInMonth = currentDate.value.lengthOfMonth()
    val startDay = currentDate.value.withDayOfMonth(1).dayOfWeek.value % 7

    LaunchedEffect(Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("trips").child(userId)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trips.clear()
                for (snap in snapshot.children) {
                    val trip = snap.getValue(Trip::class.java)
                    if (trip != null) {
                        trips.add(trip.copy(id = snap.key ?: ""))
                    }
                }
                tripCount.intValue = trips.size
                nextTrip.value = trips.minByOrNull { it.date }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
            }
        })

        val summaryRef = FirebaseDatabase.getInstance().getReference("summaries").child(userId).child("$currentYear")
        summaryRef.get().addOnSuccessListener {
            val summary = it.getValue(String::class.java)
            if (summary != null) {
                summaryText.value = TextFieldValue(summary)
            }
        }
    }

    val tripsThisYear = trips.filter { it.date.startsWith("$currentYear") }
    val mostVisited = tripsThisYear.groupingBy { it.location }.eachCount().maxByOrNull { it.value }?.key ?: "N/A"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏝️ Trip Dashboard", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5)),
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Routes.SETTINGS)
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }

                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.List, contentDescription = null, tint = Color.White)
                    }
                }
            )

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Heading Section
            Text(
                text = "Welcome to Your Trip Dashboard!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Username Greeting
            Text("Hello, $userName!", style = MaterialTheme.typography.headlineSmall)

            // Trip and Next Trip Section
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Trips", style = MaterialTheme.typography.titleMedium)
                        Text("${tripCount.value}", style = MaterialTheme.typography.headlineLarge)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Next Trip", style = MaterialTheme.typography.titleMedium)
                        nextTrip.value?.let {
                            Text(it.title, style = MaterialTheme.typography.bodyLarge)
                            Text(it.date, style = MaterialTheme.typography.bodySmall)
                        } ?: Text("No upcoming trips", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Calendar and Month Navigation Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = {
                            currentDate.value = currentDate.value.minusMonths(1)
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
                        }
                        Text(
                            text = currentDate.value.month.name + " " + currentDate.value.year,
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(onClick = {
                            currentDate.value = currentDate.value.plusMonths(1)
                        }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(startDay) {
                            Box(modifier = Modifier.size(32.dp))
                        }
                        items(daysInMonth) { day ->
                            val actualDay = day + 1
                            val dateStr = currentDate.value.withDayOfMonth(actualDay).toString()
                            val isTripDay = trips.any { it.date == dateStr }
                            val isToday = currentDate.value.withDayOfMonth(actualDay) == LocalDate.now()
                            Surface(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(32.dp),
                                color = when {
                                    isTripDay -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.background
                                },
                                shape = MaterialTheme.shapes.small,
                                tonalElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("$actualDay", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }

            // Summary Button Section
            Button(
                onClick = { showDialog.value = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("View Year Summary (${tripsThisYear.size} Trips)", color = Color.White)
            }

            // Summary Dialog
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text("Trip Summary for $currentYear") },
                    text = {
                        Column {
                            Text("Number of Trips: ${tripsThisYear.size}")
                            Text("Most Visited: $mostVisited")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Write a short summary:")
                            TextField(
                                value = summaryText.value,
                                onValueChange = { summaryText.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val summaryRef = FirebaseDatabase.getInstance().getReference("summaries").child(userId).child("$currentYear")
                            summaryRef.setValue(summaryText.value.text)
                            showDialog.value = false
                        }) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog.value = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Routes.TRIP_LIST) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trips")
                }

                Button(
                    onClick = { navController.navigate(Routes.ADD_TRIP) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Trip")
                }
            }

            Button(
                onClick = { navController.navigate(Routes.SUMMARY_SCREEN) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.List, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("All Summaries")
            }
        }
    }
}
