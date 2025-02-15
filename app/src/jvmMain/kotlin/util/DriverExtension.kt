package util

import io.appium.java_client.AppiumBy
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.RemoteWebElement
import java.nio.file.Path
import javax.imageio.ImageIO

// for compose ui
fun AndroidDriver.findComposeElementByTag(tag: String): WebElement =
    findElement(AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"$tag\")"))

fun AppiumDriver.takeScreenshotToFile(path: Path) {
    val imgByteStream = getScreenshotAs(OutputType.BYTES).inputStream()
    val img = ImageIO.read(imgByteStream)
    val croppedImg = img.getSubimage(0, 100, img.width, img.height - 100)
    ImageIO.write(croppedImg, "png", path.toFile())
}

// Gesture docs
// https://github.com/appium/appium-uiautomator2-driver/blob/master/docs/android-mobile-gestures.md

fun AppiumDriver.longClick(view: WebElement) {
    val id = (view as RemoteWebElement).id
    executeScript("mobile: longClickGesture", mapOf("elementId" to id))
}