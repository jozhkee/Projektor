package com.example.eventappgroup17.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventappgroup17.model.Category
import com.example.eventappgroup17.model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEventFormScreen(
    existingEvent: Event?,
    categories: List<Category>,
    onSave: (Event) -> Unit,
    onBack: () -> Unit
) {
    val isEditing = existingEvent != null

    var title by remember { mutableStateOf(existingEvent?.title ?: "") }
    var description by remember { mutableStateOf(existingEvent?.description ?: "") }
    var date by remember { mutableStateOf(existingEvent?.date ?: "") }
    var time by remember { mutableStateOf(existingEvent?.time ?: "") }
    var location by remember { mutableStateOf(existingEvent?.location ?: "") }
    var organizer by remember { mutableStateOf(existingEvent?.organizer ?: "") }
    var maxParticipants by remember { mutableStateOf(existingEvent?.maxParticipants?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(existingEvent?.category ?: categories.first()) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf("") }
    var locationError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Event" else "Create Event") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text("Event Details", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = "" },
                label = { Text("Title *") },
                isError = titleError.isNotEmpty(),
                supportingText = { if (titleError.isNotEmpty()) Text(titleError) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it; dateError = "" },
                    label = { Text("Date *") },
                    placeholder = { Text("YYYY-MM-DD") },
                    isError = dateError.isNotEmpty(),
                    supportingText = { if (dateError.isNotEmpty()) Text(dateError) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time") },
                    placeholder = { Text("HH:MM") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it; locationError = "" },
                label = { Text("Location *") },
                isError = locationError.isNotEmpty(),
                supportingText = { if (locationError.isNotEmpty()) Text(locationError) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = organizer,
                onValueChange = { organizer = it },
                label = { Text("Organizer") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = maxParticipants,
                onValueChange = { maxParticipants = it },
                label = { Text("Max Participants (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel") }

                Button(
                    onClick = {
                        titleError = if (title.isBlank()) "Title is required" else ""
                        dateError = if (date.isBlank()) "Date is required" else ""
                        locationError = if (location.isBlank()) "Location is required" else ""
                        if (listOf(titleError, dateError, locationError).all { it.isEmpty() }) {
                            onSave(
                                Event(
                                    id = existingEvent?.id ?: 0,
                                    title = title.trim(),
                                    description = description.trim(),
                                    date = date.trim(),
                                    time = time.trim(),
                                    location = location.trim(),
                                    organizer = organizer.trim(),
                                    category = selectedCategory,
                                    participantCount = existingEvent?.participantCount ?: 0,
                                    maxParticipants = maxParticipants.trim().toIntOrNull()
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text(if (isEditing) "Save Changes" else "Create Event") }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
