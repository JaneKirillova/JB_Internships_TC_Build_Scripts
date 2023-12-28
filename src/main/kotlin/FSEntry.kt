package org.example

import java.nio.file.Path
import javax.print.attribute.standard.Destination

abstract class FSEntry(val name: String) {
    abstract fun create(destination: Path, rewriteExisting: Boolean)
}