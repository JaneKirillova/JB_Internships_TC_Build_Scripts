package org.example

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class FSFile(name: String, val content: String) : FSEntry(name) {
    override fun create(destination: Path, rewriteExisting: Boolean) {
        val entryPath = destination.resolve(name)
        checkForExisting(entryPath, rewriteExisting)
        try {
            Files.write(entryPath, content.toByteArray(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
        } catch (e: Exception) {
            throw FSException("Could not create file: $name", e)
        }
    }

    private fun checkForExisting(entryPath: Path, rewriteExisting: Boolean) {
        if (!rewriteExisting && Files.exists(entryPath)) {
            throw FSException("File $name already exists.")
        }
        if (Files.isDirectory(entryPath)) {
            throw FSException("Folder $name already exists, you can not create file with this name.")
        }
    }
}