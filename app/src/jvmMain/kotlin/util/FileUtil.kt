package util

import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

private val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "bmp")

fun List<Path>.filterImageFile(): List<Path> {
    return this.filter { it.isRegularFile() && IMAGE_EXTENSIONS.contains(it.extension) }
}
