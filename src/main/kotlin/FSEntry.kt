package org.example

import java.nio.file.Path

abstract class FSEntry(val name: String) {
    abstract fun create(destination: Path, rewriteExisting: Boolean)
}