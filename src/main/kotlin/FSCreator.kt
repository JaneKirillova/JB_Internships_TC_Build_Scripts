package org.example

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

object FSCreator {
    fun create(entryToCreate: FSEntry, destination: String, rewriteExisting: Boolean = false) {
        val path = Path.of(destination)
        if (Files.notExists(path)) {
            throw FSException("Destination directory does not exist.")
        }
        if (!Files.isDirectory(path)) {
            throw FSException("Destination is not a directory.")
        }
        entryToCreate.create(path, rewriteExisting)
    }
}