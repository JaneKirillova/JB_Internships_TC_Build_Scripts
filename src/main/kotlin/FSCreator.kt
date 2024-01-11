package org.example

import java.nio.file.Files
import java.nio.file.Path

object FSCreator {
    fun create(entryToCreate: FSEntry, destination: String, rewriteExisting: Boolean = false) {
        checkForCycles(entryToCreate, mutableMapOf())
        val path = Path.of(destination)
        if (Files.notExists(path)) {
            throw FSException("Destination directory does not exist.")
        }
        if (!Files.isDirectory(path)) {
            throw FSException("Destination is not a directory.")
        }
        entryToCreate.create(path, rewriteExisting)
    }

    private fun checkForCycles(currentEntry: FSEntry, visited: MutableMap<FSEntry, FSEntryStatus>) {
        visited[currentEntry] = FSEntryStatus.IN_PROGRESS
        if (currentEntry is FSFile) {
            visited[currentEntry] = FSEntryStatus.FINISHED
            return
        }
        val folderContent = (currentEntry as FSFolder).content
        for (entry in folderContent) {
            when {
                entry !in visited -> checkForCycles(entry, visited)
                visited[entry] == FSEntryStatus.IN_PROGRESS -> throw FSException("File structure contains a loop")
                visited[entry] == FSEntryStatus.FINISHED -> continue
            }
        }
        visited[currentEntry] = FSEntryStatus.FINISHED
    }

}