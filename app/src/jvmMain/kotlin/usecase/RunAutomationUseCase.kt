package usecase

import ActualAndroidDriver
import io.appium.java_client.AppiumBy
import io.appium.java_client.AppiumDriver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import org.openqa.selenium.WebElement
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object RunAutomationUseCase {
    suspend operator fun invoke() {
        ActualAndroidDriver().use { driver ->
            val config = GetConfigUseCase().first()

            withTimeout(30.seconds) {
                driver.open(config)
            }

            val appiumDriver = driver.getDriver()
            val assemblyName = "お試し構成"

            // アプリ起動待ち
            delay(3.seconds)

            appiumDriver.apply {
                // 開始ボタンクリック
                findComposeElementByTag("start_button").click()
//                findElement(AppiumBy.id("start_button")).click() // case of AndroidViewId

                // 構成名入力
                delay(1.seconds)
                val textField = findComposeElementByTag("assembly_name_text_field")
                textField.click()
                delay(250.milliseconds)
                textField.sendKeys(assemblyName)

                // 作成ボタンクリック
                delay(1.seconds)
                findComposeElementByTag("create_assembly_button").click()

                // PCケース
                delay(1.seconds)
                findComposeElementByTag("pccase").click()

                // 3回スクロールして表示されている中からindex=2のアイテムをクリック
                delay(1.seconds)
                scrollForward(times = 3)
                findElement(AppiumBy.androidUIAutomator("new UiSelector().index(2)")).click()

                // 追加する
                delay(1.seconds)
                findComposeElementByTag("edit_assembly_button").click()

                // 追加したアイテムをクリック
                delay(1.seconds)
                findComposeElementByTag("pccase").click()

                // プラス、マイナス、プラス、変更
                delay(1.seconds)
                findComposeElementByTag("plus_button").click()
                delay(250.milliseconds)
                findComposeElementByTag("minus_button").click()
                delay(250.milliseconds)
                findComposeElementByTag("plus_button").click()
                delay(500.milliseconds)
                findComposeElementByTag("edit_assembly_button").click()
                delay(250.milliseconds)
                // 同一パーツの複数登録時の表示があるかチェック
                assert(findComposeElementByTag("multiple_total_price").isDisplayed)

                // パーツ選択画面へ
                delay(1.seconds)
                findComposeElementByTag("DeviceScreen").click()

                // パーツ種別選択画面へ
                delay(1.seconds)
                findComposeElementByTag("SelectionScreen").click()

                // ホーム画面へ
                delay(1.seconds)
                findComposeElementByTag("TopScreen").click()

                // 作成済みの構成を選択
                delay(1.seconds)
                findComposeElementByTag(assemblyName).click()

                // パーツを追加ボタンをクリック
                delay(1.seconds)
                findComposeElementByTag("add_parts_button").click()

                // スクロールしてマウスを選択
                delay(1.seconds)
                scrollForward()
                findComposeElementByTag("mouse").click()

                // 2回スクロールして表示されている中からindex=4のアイテムをクリック
                delay(1.seconds)
                scrollForward(times = 2)
                findElement(AppiumBy.androidUIAutomator("new UiSelector().index(4)")).click()

                // 追加する
                delay(1.seconds)
                findComposeElementByTag("edit_assembly_button").click()

                // パーツ選択画面へ
                delay(1.seconds)
                findComposeElementByTag("DeviceScreen").click()

                delay(30.seconds)
            }
        }
    }

    private fun AppiumDriver.findComposeElementByTag(tag: String): WebElement =
        findElement(AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"$tag\")"))

    private suspend fun AppiumDriver.scrollForward(times: Int = 1) {
        repeat(times = times) {
            try {
                findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollForward()"))
            } catch (_: Throwable) {
            }
            delay(1.seconds)
        }
    }
}
