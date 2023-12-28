package org.example

import java.nio.file.Files
import java.nio.file.Path

class FSFolder(name: String, val content: List<FSEntry>): FSEntry(name) {
    override fun create(destination: Path, rewriteExisting: Boolean) {
        val entryPath = destination.resolve(name)
        checkForExisting(entryPath, rewriteExisting)
        createFolder(entryPath)
        content.forEach { it.create(entryPath, rewriteExisting) }
    }

    private fun checkForExisting(entryPath: Path, rewriteExisting: Boolean) {
        if (!rewriteExisting && Files.exists(entryPath)) {
            throw FSException("Folder $name already exists.")
        }
        if (Files.isRegularFile(entryPath)) {
            throw FSException("File $name already exists, you can not create folder with this name.")
        }
    }

    private fun createFolder(entryPath: Path) {
        try {
            if (Files.notExists(entryPath)) Files.createDirectory(entryPath)
        } catch (e: Exception) {
            throw FSException("Problem while creating folder: $name", e)
        }
    }
}