package com.example.button.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.button.SpecialSong.WorldExec.TerminalScreen
import java.io.File
import java.io.FileOutputStream

data class SongModel(
    val folderName: String,
    val title: String,
    val artist: String,
    val themeBitmap: ImageBitmap?,
    val resourceAudioPath: String
)

class Diagonal70DegreeShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val angleInRadians = Math.toRadians(70.0)
        val slantOffsetPx = (size.height / Math.tan(angleInRadians)).toFloat()

        val path = Path().apply {
            moveTo(slantOffsetPx, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialMusicListScreen(
    isPremium: Boolean,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var songsList by remember { mutableStateOf<List<SongModel>>(emptyList()) }
    var selectedFolder by remember { mutableStateOf<String?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPreviewPlaying by remember { mutableStateOf(false) }
    var showPremiumDialog by remember { mutableStateOf(false) }

    // Quản lý việc mở màn hình Terminal của WorldExec
    var isRunningTerminal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        songsList = loadSongsFromPackage(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    // Nếu bấm nút Play Terminal thì chuyển sang màn hình TerminalScreen
    if (isRunningTerminal) {
        TerminalScreen(
            onBackClick = {
                isRunningTerminal = false
            }
        )
    } else {
        val selectedSong = songsList.find { it.folderName == selectedFolder }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A0D14),
                            Color(0xFF121622),
                            Color(0xFF08090E)
                        )
                    )
                )
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Surface(
                        color = Color(0xFF121622).copy(alpha = 0.9f),
                        tonalElevation = 4.dp,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onBackClick() },
                                modifier = Modifier
                                    .background(Color(0xFF1E2433), CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Quay lại",
                                    tint = Color(0xFF00FF88)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "NHẠC ĐẶC BIỆT",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "WorldExec Audio & Terminal Console",
                                    fontSize = 12.sp,
                                    color = Color(0xFF00FF88).copy(alpha = 0.8f),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Thanh trạng thái hệ thống
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DANH SÁCH BÀI HÁT (${songsList.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontFamily = FontFamily.Monospace
                        )

                        Surface(
                            color = Color(0xFF00FF88).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF00FF88), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SYSTEM ONLINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF88),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(songsList) { song ->
                            val isSelected = (selectedFolder == song.folderName)

                            val scaleAnimation by animateFloatAsState(
                                targetValue = if (isSelected) 1.02f else 1.0f,
                                label = "ScaleAnimation"
                            )

                            SongItemCard(
                                song = song,
                                isSelected = isSelected,
                                isPreviewPlaying = isSelected && isPreviewPlaying,
                                scale = scaleAnimation,
                                onClick = {
                                    if (selectedFolder != song.folderName) {
                                        selectedFolder = song.folderName

                                        mediaPlayer?.stop()
                                        mediaPlayer?.release()
                                        isPreviewPlaying = false

                                        val tempAudioFile = playAudioFromClassLoader(context, song.resourceAudioPath)
                                        if (tempAudioFile != null) {
                                            mediaPlayer = MediaPlayer().apply {
                                                setDataSource(tempAudioFile.absolutePath)
                                                prepare()
                                                start()
                                                setOnCompletionListener {
                                                    isPreviewPlaying = false
                                                }
                                            }
                                            isPreviewPlaying = true
                                        }
                                    } else {
                                        // Toggle play/pause preview
                                        mediaPlayer?.let { player ->
                                            if (player.isPlaying) {
                                                player.pause()
                                                isPreviewPlaying = false
                                            } else {
                                                player.start()
                                                isPreviewPlaying = true
                                            }
                                        }
                                    }
                                },
                                onPlayTerminalClick = {
                                    if (isPremium) {
                                        mediaPlayer?.stop()
                                        isPreviewPlaying = false
                                        isRunningTerminal = true
                                    } else {
                                        showPremiumDialog = true
                                    }
                                }
                            )
                        }
                    }

                    // Bottom Floating Banner khi chọn bài hát
                    AnimatedVisibility(
                        visible = selectedSong != null,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut()
                    ) {
                        selectedSong?.let { song ->
                            Surface(
                                color = Color(0xFF161C2A),
                                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                                border = BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            color = Color(0xFF00FF88).copy(alpha = 0.2f),
                                            shape = CircleShape,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MusicNote,
                                                contentDescription = null,
                                                tint = Color(0xFF00FF88),
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .fillMaxSize()
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = song.title,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = if (isPreviewPlaying) "Đang phát preview..." else "Đã chọn tác phẩm",
                                                fontSize = 12.sp,
                                                color = Color(0xFF00FF88)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            if (isPremium) {
                                                mediaPlayer?.stop()
                                                isPreviewPlaying = false
                                                isRunningTerminal = true
                                            } else {
                                                showPremiumDialog = true
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF00FF88),
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Terminal,
                                            contentDescription = "Terminal",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isPremium) "RUN TERMINAL" else "PREMIUM",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                if (showPremiumDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showPremiumDialog = false },
                                        title = { Text("Yêu cầu Premium") },
                                        text = {
                                            Text(
                                                "Chức năng này yêu cầu tài khoản Premium. " +
                                                    "Bạn có thể mở khóa Premium ở trang chủ."
                                            )
                                        },
                                        confirmButton = {
                                            TextButton(
                                                onClick = { showPremiumDialog = false }
                                            ) {
                                                Text("Đóng")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongItemCard(
    song: SongModel,
    isSelected: Boolean,
    isPreviewPlaying: Boolean,
    scale: Float,
    onClick: () -> Unit,
    onPlayTerminalClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF00FF88) else Color(0xFF232A3B)
    val cardBg = if (isSelected) Color(0xFF1E2536) else Color(0xFF141824)
    val cornerRadius = 16.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(cornerRadius),
        color = cardBg,
        tonalElevation = if (isSelected) 8.dp else 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
            ) {
                // --- BÊN TRÁI: THÔNG TIN BÀI HÁT ---
                Column(
                    modifier = Modifier
                        .weight(1.8f)
                        .fillMaxHeight()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Tag phân loại
                        Surface(
                            color = Color(0xFF00FF88).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "SPECIAL AUDIO • ${song.folderName}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF88),
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = song.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF00FF88) else Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = song.artist,
                            fontSize = 13.sp,
                            color = Color.LightGray.copy(alpha = 0.8f)
                        )
                    }

                    if (isSelected) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPreviewPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Preview state",
                                tint = Color(0xFF00FF88),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPreviewPlaying) "Đang phát preview audio..." else "Nhấn để phát preview",
                                fontSize = 11.sp,
                                color = Color(0xFF00FF88),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // --- BÊN PHẢI: THEME HÌNH CẮT CHÉO ---
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .clip(Diagonal70DegreeShape())
                ) {
                    song.themeBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Theme",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF232A3B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            }

            // HÀNG NÚT BẤM THAO TÁC KHI BÀI HÁT ĐƯỢC CHỌN
            if (isSelected) {
                HorizontalDivider(color = Color(0xFF00FF88).copy(alpha = 0.2f), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onClick() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF00FF88)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF00FF88).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isPreviewPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Toggle Preview",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPreviewPlaying) "PAUSE" else "PREVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = { onPlayTerminalClick() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FF88),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Terminal",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CHẠY PROGRAM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

private fun loadSongsFromPackage(context: Context): List<SongModel> {
    val songs = mutableListOf<SongModel>()
    val classLoader = context.classLoader
    val targetFolders = listOf("WorldExec")
    val packageBasePath = "com/example/button/SpecialSong"

    for (folder in targetFolders) {
        val folderPath = "$packageBasePath/$folder"

        var title = "Unknown Title"
        var artist = "Unknown Artist"
        try {
            val txtStream = classLoader.getResourceAsStream("$folderPath/song_detail.txt")
            txtStream?.bufferedReader()?.use { reader ->
                val lines = reader.readLines()
                if (lines.isNotEmpty()) title = lines[0]
                if (lines.size > 1) artist = lines[1]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var themeBitmap: ImageBitmap? = null
        try {
            val imgStream = classLoader.getResourceAsStream("$folderPath/Theme.png")
            imgStream?.use { stream ->
                themeBitmap = BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val audioResourcePath = "$folderPath/ghost.mp3"

        songs.add(
            SongModel(
                folderName = folder,
                title = title,
                artist = artist,
                themeBitmap = themeBitmap,
                resourceAudioPath = audioResourcePath
            )
        )
    }

    return songs
}

private fun playAudioFromClassLoader(context: Context, resourcePath: String): File? {
    return try {
        val inputStream = context.classLoader.getResourceAsStream(resourcePath) ?: return null
        val tempFile = File.createTempFile("preview_song", ".mp3", context.cacheDir)
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { output -> inputStream.copyTo(output) }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
