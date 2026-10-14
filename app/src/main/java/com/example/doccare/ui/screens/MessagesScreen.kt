package com.example.doccare.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doccare.R
import com.example.doccare.model.ChatMessage
import com.example.doccare.model.Doctor
import com.example.doccare.ui.DocCareUiState
import com.example.doccare.ui.theme.MedicalDeepPine
import com.example.doccare.ui.theme.MedicalEucalyptus
import com.example.doccare.ui.theme.MedicalForestTeal
import com.example.doccare.ui.theme.MedicalTealLight
import com.example.doccare.ui.theme.MutedSlateText
import com.example.doccare.ui.theme.SubtleGrayText
import com.example.doccare.ui.theme.WarmCardBorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    uiState: DocCareUiState,
    onSendMessage: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDoctorForChat by remember {
        mutableStateOf(uiState.allDoctors.firstOrNull())
    }
    var inputText by remember { mutableStateOf("") }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.FRANCE) }

    val activeDoctor = selectedDoctorForChat ?: uiState.allDoctors.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (activeDoctor != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = activeDoctor.localDrawableRes),
                                contentDescription = activeDoctor.name,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = activeDoctor.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "En ligne • ${activeDoctor.specialty}",
                                    fontSize = 11.sp,
                                    color = MedicalEucalyptus
                                )
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(id = R.string.messages),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (activeDoctor != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Écrire au ${activeDoctor.name}...", fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            shape = RoundedCornerShape(22.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                focusedBorderColor = MedicalForestTeal
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    onSendMessage(activeDoctor.id, inputText)
                                    inputText = ""
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(MedicalForestTeal)
                                .testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Envoyer",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 20.dp)
            ) {
                // Sélection du praticien
                item {
                    Text(
                        text = "Praticiens avec qui échanger",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MutedSlateText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        uiState.allDoctors.forEach { doc ->
                            val isSelected = activeDoctor?.id == doc.id
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = if (isSelected) MedicalForestTeal else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) MedicalDeepPine else WarmCardBorder
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { selectedDoctorForChat = doc }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = doc.localDrawableRes),
                                        contentDescription = doc.name,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = doc.name.split(" ").takeLast(1).firstOrNull() ?: doc.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Messages de la conversation
                if (activeDoctor != null) {
                    val messages = uiState.chatMessages[activeDoctor.id] ?: emptyList()

                    items(messages, key = { it.id }) { msg ->
                        ChatBubble(
                            message = msg,
                            doctor = activeDoctor,
                            formattedTime = timeFormat.format(Date(msg.timestamp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    doctor: Doctor,
    formattedTime: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isFromUser) {
            Image(
                painter = painterResource(id = doctor.localDrawableRes),
                contentDescription = doctor.name,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = if (message.isFromUser) 14.dp else 2.dp,
                    bottomEnd = if (message.isFromUser) 2.dp else 14.dp
                ),
                color = if (message.isFromUser) MedicalForestTeal else MaterialTheme.colorScheme.surface,
                border = if (!message.isFromUser) androidx.compose.foundation.BorderStroke(1.dp, WarmCardBorder) else null,
                shadowElevation = 1.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (message.isFromUser) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = formattedTime,
                fontSize = 10.sp,
                color = SubtleGrayText,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
