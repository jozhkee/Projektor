package com.example.eventappgroup17.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLogin: suspend (email: String, password: String, rememberMe: Boolean) -> String?,
    onNavigateToRegister: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "EventAppGroup17",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Event & Meetup Platform",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Sign In",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; emailError = "" },
            label = { Text("Email") },
            isError = emailError.isNotEmpty(),
            supportingText = { if (emailError.isNotEmpty()) Text(emailError) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; passwordError = "" },
            label = { Text("Password") },
            isError = passwordError.isNotEmpty(),
            supportingText = { if (passwordError.isNotEmpty()) Text(passwordError) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                enabled = !isLoading,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Remember me", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                loginError = ""
                emailError = when {
                    email.isBlank() -> "Email is required"
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email"
                    else -> ""
                }
                passwordError = if (password.isBlank()) "Password is required" else ""
                if (emailError.isEmpty() && passwordError.isEmpty()) {
                    scope.launch {
                        isLoading = true
                        val error = onLogin(email, password, rememberMe)
                        isLoading = false
                        if (error != null) loginError = error
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
                Text("Sign In", fontSize = 16.sp)
            }
        }

        if (loginError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = loginError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister, enabled = !isLoading) {
            Text("Don't have an account? Register")
        }
    }
}