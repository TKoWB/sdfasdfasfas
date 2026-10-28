package com.example.button.SpecialSong.WorldExec

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun TerminalScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val logs = remember { mutableStateListOf<AnnotatedString>() }
    var currentActiveLine by remember { mutableStateOf<AnnotatedString?>(null) }
    var countdown by remember { mutableStateOf<Int?>(null) }
    var isWaitingForPassword by remember { mutableStateOf(true) }
    var inputCommand by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    // Quản lý MediaPlayer để giải phóng an toàn tránh Crash
    var activeMediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try {
                activeMediaPlayer?.stop()
                activeMediaPlayer?.release()
                activeMediaPlayer = null
            } catch (_: Exception) {}
        }
    }

    // Tự động cuộn xuống dòng mới nhất (Thêm try-catch để phòng ngừa Crash do cuộn trang)
    LaunchedEffect(logs.size, currentActiveLine, countdown, isWaitingForPassword) {
        val totalItems = logs.size + (if (currentActiveLine != null) 1 else 0) + (if (countdown != null) 1 else 0)
        if (totalItems > 0) {
            try {
                listState.animateScrollToItem(totalItems - 1)
            } catch (_: Exception) {}
        }
    }

    // Tự động nạp file user.txt khi vừa mở Terminal
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            readFileAssets(context, "user.txt") { line ->
                withContext(Dispatchers.Main) {
                    logs.add(AnnotatedString(line))
                }
            }
        }
    }

    // Tự động Focus vào con trỏ nhập lệnh ngay khi sẵn sàng
    LaunchedEffect(isWaitingForPassword) {
        if (isWaitingForPassword) {
            delay(200)
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {}
        }
    }

    fun submitCommand() {
        val cmd = inputCommand.trim()
        if (cmd.isEmpty() || !isWaitingForPassword) return
        inputCommand = ""

        logs.add(buildAnnotatedString {
            withStyle(SpanStyle(color = Color(0xFF00FF66), fontWeight = FontWeight.Bold)) {
                append("> $cmd")
            }
        })

        if (cmd.equals("run world.exec", ignoreCase = true)) {
            isWaitingForPassword = false
            logs.add(buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFF00FF66), fontWeight = FontWeight.Bold)) {
                    append("[SYSTEM] Initializing WorldExec program...\n")
                }
            })

            coroutineScope.launch(Dispatchers.IO) {
                val mediaPlayer = prepareMusicFromClassLoader(context, "com/example/button/SpecialSong/WorldExec/ghost.mp3")
                withContext(Dispatchers.Main) {
                    activeMediaPlayer = mediaPlayer
                }

                // CẬP NHẬT TRẠNG THÁI COMPOSE CHÍNH XÁC TRÊN MAIN THREAD TRÁNH CRASH TẮC NGHẼN BỘ NHỚ
                val worldEngine = WorldEngine(
                    context = context,
                    mediaPlayer = mediaPlayer,
                    onAppendLog = { annotated ->
                        coroutineScope.launch(Dispatchers.Main.immediate) {
                            logs.add(annotated)
                        }
                    },
                    onUpdateActiveLine = { annotated ->
                        coroutineScope.launch(Dispatchers.Main.immediate) {
                            currentActiveLine = annotated
                        }
                    },
                    onClearLogs = {
                        coroutineScope.launch(Dispatchers.Main.immediate) {
                            logs.clear()
                            currentActiveLine = null
                        }
                    }
                )

                try {
                    worldEngine.runWorldExecuteMe()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        mediaPlayer?.release()
                    } catch (_: Exception) {}
                }

                // Đếm ngược 5 giây rồi tự động quay lại
                for (sec in 5 downTo 1) {
                    withContext(Dispatchers.Main) {
                        countdown = sec
                    }
                    delay(1000)
                }
                withContext(Dispatchers.Main) {
                    onBackClick()
                }
            }
        } else {
            logs.add(buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFFFF5555), fontWeight = FontWeight.Bold)) {
                    append("[ERROR] Access denied. Command '$cmd' not recognized.\n")
                }
            })
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0C10))
            .clickable {
                if (isWaitingForPassword) {
                    try { focusRequester.requestFocus() } catch (_: Exception) {}
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // THANH TIÊU ĐỀ TERMINAL NGUYÊN BẢN
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onBackClick() }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color(0xFF00FF66),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TERMINAL",
                        color = Color(0xFF00FF66),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = Color(0xFF161B22),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF00FF66).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = if (isWaitingForPassword) "WAITING AUTH" else "EXECUTING",
                        color = if (isWaitingForPassword) Color.Yellow else Color(0xFF00FF66),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFF00FF66).copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // NỘI DUNG LOG DẠNG TERMINAL PHÔNG NGUYÊN BẢN
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(logs) { log ->
                    Text(
                        text = log,
                        color = Color(0xFF00FF66),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                currentActiveLine?.let { active ->
                    item {
                        Text(
                            text = active,
                            color = Color(0xFF00FF66),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                // DÒNG NHẬP LỆNH CHUẨN
                if (isWaitingForPassword) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "> ",
                                color = Color(0xFF00FF66),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            BasicTextField(
                                value = inputCommand,
                                onValueChange = { inputCommand = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = Color(0xFF00FF66),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                ),
                                cursorBrush = SolidColor(Color(0xFF00FF66)),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { submitCommand() }),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                            )
                        }
                    }
                }

                // DÒNG ĐẾM NGƯỢC 5s TỰ ĐỘNG THOÁT
                countdown?.let { sec ->
                    item {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color.Yellow, fontWeight = FontWeight.Bold)) {
                                    append("\n[PROGRAM FINISHED] Tự động quay lại sau $sec giây...")
                                }
                            },
                            color = Color(0xFF00FF66),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

private fun prepareMusicFromClassLoader(context: Context, resourcePath: String): MediaPlayer? {
    return try {
        val inputStream = context.classLoader.getResourceAsStream(resourcePath) ?: return null
        val tempFile = File.createTempFile("exec_song", ".mp3", context.cacheDir)
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { output -> inputStream.copyTo(output) }

        MediaPlayer().apply {
            setDataSource(tempFile.absolutePath)
            prepare()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}