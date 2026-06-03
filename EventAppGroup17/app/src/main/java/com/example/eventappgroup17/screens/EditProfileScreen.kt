package com.example.eventappgroup17.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    currentName: String,
    onSave: suspend (name: String?, password: String?, currentPassword: String?) -> String?,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf(currentName) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var currentPasswordError by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Personal Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = "" },
                label = { Text("Full Name") },
                isError = nameError.isNotEmpty(),
                supportingText = { if (nameError.isNotEmpty()) Text(nameError) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            Text("Change Password", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "Leave blank to keep current password",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it; currentPasswordError = "" },
                label = { Text("Current Password") },
                isError = currentPasswordError.isNotEmpty(),
                supportingText = { if (currentPasswordError.isNotEmpty()) Text(currentPasswordError) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it; newPasswordError = "" },
                label = { Text("New Password") },
                isError = newPasswordError.isNotEmpty(),
                supportingText = { if (newPasswordError.isNotEmpty()) Text(newPasswordError) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmPasswordError = "" },
                label = { Text("Confirm New Password") },
                isError = confirmPasswordError.isNotEmpty(),
                supportingText = { if (confirmPasswordError.isNotEmpty()) Text(confirmPasswordError) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    nameError = if (name.isBlank()) "Name is required" else ""

                    if (newPassword.isNotBlank()) {
                        currentPasswordError = if (currentPassword.isBlank()) "Current password is required" else ""
                        newPasswordError = if (newPassword.length < 6) "At least 6 characters" else ""
                        confirmPasswordError = if (confirmPassword != newPassword) "Passwords do not match" else ""
                    } else {
                        currentPasswordError = ""
                        newPasswordError = ""
                        confirmPasswordError = ""
                    }

                    if (nameError.isEmpty() && currentPasswordError.isEmpty() &&
                        newPasswordError.isEmpty() && confirmPasswordError.isEmpty()) {
                        scope.launch {
                            isLoading = true
                            val newName = if (name != currentName) name else null
                            val passwordToSend = if (newPassword.isNotBlank()) newPassword else null
                            val currentPasswordToSend = if (newPassword.isNotBlank()) currentPassword else null
                            val error = onSave(newName, passwordToSend, currentPasswordToSend)
                            isLoading = false
                            if (error != null) {
                                if (error.contains("current password", ignoreCase = true)) {
                                    currentPasswordError = "Incorrect current password"
                                } else {
                                    snackbarHostState.showSnackbar(error)
                                }
                            } else {
                                snackbarHostState.showSnackbar("Profile updated successfully!")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Changes", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}