package com.example.button.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isPremium: Boolean,
    privacyOptionsRequired: Boolean,
    onPrivacyOptionsClick: () -> Unit,
    onPurchasePremium: () -> Unit,
    onNavigateToMusicList: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trang chính") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Màn hình Chính", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(20.dp))

            if (isPremium) {
                Text("Premium giả lập đã kích hoạt", color = MaterialTheme.colorScheme.primary)
            } else {
                Button(onClick = onPurchasePremium) {
                    Text("Mô phỏng mở khóa Premium")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mở khóa Terminal và tắt quảng cáo trên thiết bị này (không thanh toán thật).",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (privacyOptionsRequired) {
                TextButton(onClick = onPrivacyOptionsClick) {
                    Text("Tùy chọn quyền riêng tư")
                }
            }
            // NÚT BẤM CHUYỂN MÀN HÌNH
            Button(onClick = { onNavigateToMusicList() }) {
                Text(text = "Mở Danh sách nhạc đặc biệt")
            }
        }
    }
}