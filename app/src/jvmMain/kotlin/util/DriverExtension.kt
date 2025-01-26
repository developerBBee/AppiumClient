package util

import io.appium.java_client.AppiumBy
import io.appium.java_client.AppiumDriver
import kotlinx.coroutines.delay
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebElement
import java.io.File
import java.nio.file.Path
import kotlin.time.Duration.Companion.seconds


fun AppiumDriver.findComposeElementByTag(tag: String): WebElement =
    findElement(AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"$tag\")"))

suspend fun AppiumDriver.scrollForward(times: Int = 1) {
    repeat(times = times) {
        try {
            findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollForward()"))
        } catch (_: Throwable) {
        }
        delay(1.seconds)
    }
}

fun AppiumDriver.takeScreenshotToFile(path: Path) {
    val imgFile = getScreenshotAs(OutputType.FILE)
    imgFile.copyTo(target = File("$path"), overwrite = true)
}