package com.kriptikz.shadowdrivemobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SDMScaffold(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(start = 16.dp)
            ) {
                Box {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Text(
                        text = title,
                        style = TextStyle(
                            color = Color.White,
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                }
            }
        },
        bottomBar = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color.Black)
            ) {
                Icon(Icons.Default.Menu, contentDescription = null, tint = Color.Gray)
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.Gray)
                Icon(Icons.Default.AccountBox, contentDescription = null, tint = Color.Gray)
            }
        },
    ) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}