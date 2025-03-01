package util

import androidx.compose.ui.graphics.painter.BitmapPainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.io.path.isRegularFile
import kotlin.io.path.notExists
import kotlin.io.path.outputStream
import kotlin.math.abs

private val IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "bmp")

val USER_DIR_PATH: Path = Path(System.getProperty(Constant.USER_HOME))
val DEFAULT_APP_FILE_PATH: Path = USER_DIR_PATH / Constant.DEFAULT_APP
val SCREENSHOT_DIR_PATH: Path = USER_DIR_PATH / Constant.SCREENSHOT_DIR

fun List<Path>.filterImageFile(): List<Path> {
    return this.filter { it.isRegularFile() && IMAGE_EXTENSIONS.contains(it.extension) }
}

@OptIn(ExperimentalResourceApi::class)
fun Path.decodeToBitmapPainter(): BitmapPainter? = takeIf { it.exists() }
    ?.inputStream()
    ?.use {
        val bmp = it.readAllBytes().decodeToImageBitmap()
        BitmapPainter(bmp)
    }

fun createImageDifference(imagePath1: Path, imagePath2: Path): Path? {
    if (imagePath1.notExists() || imagePath2.notExists()) {
        return null
    }

    val image1 = ImageIO.read(imagePath1.toFile())
    val image2 = ImageIO.read(imagePath2.toFile())

    if (image1.width != image2.width || image1.height != image2.height) {
        return null
    }

    val diffImage = BufferedImage(image1.width, image1.height, BufferedImage.TYPE_INT_RGB)

    for (x in 0 until image1.width) {
        for (y in 0 until image1.height) {
            val rgb1 = Color(image1.getRGB(x, y))
            val rgb2 = Color(image2.getRGB(x, y))

            val diffRed = abs(rgb1.red - rgb2.red)
            val diffGreen = abs(rgb1.green - rgb2.green)
            val diffBlue = abs(rgb1.blue - rgb2.blue)

            val diffColor = Color(diffRed, diffGreen, diffBlue)
            diffImage.setRGB(x, y, diffColor.rgb)
        }
    }

    val tempFilePath = (SCREENSHOT_DIR_PATH / "_temp.png")
    ImageIO.write(diffImage, "png", tempFilePath.outputStream())

    return tempFilePath
}