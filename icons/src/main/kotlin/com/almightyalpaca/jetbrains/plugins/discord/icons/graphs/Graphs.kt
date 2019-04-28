package com.almightyalpaca.jetbrains.plugins.discord.icons.graphs

import com.almightyalpaca.jetbrains.plugins.discord.icons.utils.getLocalIcons
import com.almightyalpaca.jetbrains.plugins.discord.shared.source.local.LocalSource
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Paths

@Suppress("BlockingMethodInNonBlockingContext")
fun main() = runBlocking {
    val source = LocalSource(Paths.get("../"), retry = false)

    val languages = source.getLanguages()
    val themes = source.getThemes()

    val graphs = Paths.get("build/graphs/")
    Files.createDirectories(graphs)

    for (theme in themes.keys) {
        val icons = getLocalIcons(theme)

        val exporter = DotGraphExporter(languages, icons)

        val path = graphs.resolve("$theme.dot")

        Files.newBufferedWriter(path).use { writer ->
            exporter.writeTo(writer)
        }
    }
}
