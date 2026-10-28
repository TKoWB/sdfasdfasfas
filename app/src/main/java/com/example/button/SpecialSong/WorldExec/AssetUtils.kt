package com.example.button.SpecialSong.WorldExec

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

suspend fun readFileAssets(context: Context, fileName: String, onLineRead: suspend (String) -> Unit) {
    try {
        val cleanName = fileName.substringAfterLast("/")
        val resourcePath = "com/example/button/SpecialSong/WorldExec/Text/$cleanName"
        val inputStream = context.classLoader.getResourceAsStream(resourcePath)
            ?: context.classLoader.getResourceAsStream(fileName)
            ?: context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String? = reader.readLine()
        while (line != null) {
            onLineRead(line)
            line = reader.readLine()
        }
        reader.close()
    } catch (_: Exception) {
        onLineRead("[File Not Found: $fileName]")
    }
}