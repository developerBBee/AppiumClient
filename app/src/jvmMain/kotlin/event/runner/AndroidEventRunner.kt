package event.runner

import data.AppiumConfiguration
import event.action.*
import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.nativekey.KeyEvent
import io.appium.java_client.android.options.UiAutomator2Options
import kotlinx.coroutines.delay
import org.openqa.selenium.remote.SessionId
import util.DATE_TIME_SEPARATE_FORMAT
import util.NO_DELIMITER_MILLIS_FORMAT
import util.longClick
import util.takeScreenshotToFile
import java.nio.file.Path
import java.time.LocalDateTime
import kotlin.io.path.createDirectories
import kotlin.time.Duration.Companion.seconds

class AndroidEventRunner(
    targetName: String,
    private val config: AppiumConfiguration,
) : EventRunner {

    private var _driver: AndroidDriver? = null
    private val driver: AndroidDriver
        get() = checkNotNull(_driver) { throw DriverNotPreparedException() }

    private val baseDirPath = Path.of(System.getProperty("user.home"))
        .resolve("screenshots")
        .resolve(targetName)

    private lateinit var currentDateTime: LocalDateTime

    private val dirPath
        get() = baseDirPath.resolve(currentDateTime.format(DATE_TIME_SEPARATE_FORMAT))

    /** 最初に必ず呼ぶ */
    override suspend fun start(): SessionId {
        val options = UiAutomator2Options()
            .setUdid(config.udid)
            .setApp(config.app)
            .fullReset()
            .autoGrantPermissions()

        // Open AndroidDriver
        _driver = AndroidDriver(
//            config.uri.toURL(), // このプログラムからサーバーを起動した場合はURLの指定が不要
            options,
        )

        // アプリ起動待ち
        delay(3.seconds)

        currentDateTime = LocalDateTime.now()
        dirPath.createDirectories()

        // 起動時のスクリーンショット
        takeScreenshot()

        return driver.sessionId
    }

    override suspend fun tap(action: TapAction) {
        val view = driver.findElement(AppiumBy.id(action.viewId))
        view.click()
        delayAfterTakeScreenshot(action)
    }

    override suspend fun tapText(action: TapTextAction) {
        val view = driver.findElement(AppiumBy.androidUIAutomator(action.uiAutomatorText))
        view.click()
        delayAfterTakeScreenshot(action)
    }

    override suspend fun tapChild(action: TapChildAction) {
        val parent = driver.findElement(AppiumBy.id(action.viewId))
        val child = parent.findElement(AppiumBy.androidUIAutomator(action.uiAutomatorText))
        child.click()
        delayAfterTakeScreenshot(action)
    }

    override suspend fun longTapText(action: LongTapTextAction) {
        val view = driver.findElement(AppiumBy.androidUIAutomator(action.uiAutomatorText))
        driver.longClick(view)
        delayAfterTakeScreenshot(action)
    }

    override suspend fun repeatTaps(action: RepeatTapsAction) {
        val view = driver.findElement(AppiumBy.id(action.viewId))
        repeat(action.repeat) {
            view.click()
            delay(action.interval)
        }
        delayAfterTakeScreenshot(action)
    }

    override suspend fun inputText(action: InputTextAction) {
        val view = driver.findElement(AppiumBy.id(action.viewId))
        view.sendKeys(action.text)
        delayAfterTakeScreenshot(action)
    }

    override suspend fun inputAndroidKey(action: AndroidKeyAction) {
        driver.pressKey(KeyEvent(action.key))
        delayAfterTakeScreenshot(action)
    }

    override suspend fun scroll(action: ScrollAction) {
        repeat(times = action.repeat) {
            driver.findElement(AppiumBy.androidUIAutomator(action.uiAutomatorText))
            delay(action.interval)
        }
        delayAfterTakeScreenshot(action)
    }

    override suspend fun runUiAutomator(action: UiAutomatorAction) {
        val view = driver.findElement(AppiumBy.androidUIAutomator(action.uiAutomatorText))
        when (action.action) {
            ViewAction.TAP -> view.click()
            ViewAction.INPUT_TEXT -> view.sendKeys(action.actionData)
        }
        delayAfterTakeScreenshot(action)
    }

    override suspend fun hideKeyboard(action: HideKeyboardAction) {
        driver.hideKeyboard()
        delayAfterTakeScreenshot(action)
    }

    override suspend fun tapCompose(action: ComposeTapAction) {
        val view = driver.findElement(AppiumBy.androidUIAutomator(action.uiAutomatorText))
        view.click()
        delayAfterTakeScreenshot(action)
    }

    override suspend fun inputTextCompose(action: ComposeInputTextAction) {
        val view = driver.findElement(AppiumBy.androidUIAutomator(action.uiAutomatorText))
        view.sendKeys(action.text)
        delayAfterTakeScreenshot(action)
    }

    override suspend fun tapChildCompose(action: ComposeTapChildAction) {
        val parent = driver.findElement(AppiumBy.androidUIAutomator(action.parentUiAutomatorText))
        val child = parent.findElement(AppiumBy.androidUIAutomator(action.childUiAutomatorText))
        child.click()
        delayAfterTakeScreenshot(action)
    }

    private suspend fun delayAfterTakeScreenshot(action: EventAction) {
        delay(action.nextWait)
        takeScreenshot(screenshotName = action.screenshotName)
    }

    private fun takeScreenshot(screenshotName: String? = null) {
        val fileName = screenshotName ?: LocalDateTime.now().format(NO_DELIMITER_MILLIS_FORMAT)
        val filePath = dirPath.resolve("$fileName.png")
        driver.takeScreenshotToFile(filePath)
    }

    override fun close() {
        _driver?.quit()
        _driver = null
    }
}
