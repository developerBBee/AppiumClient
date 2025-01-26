package usecase

import ActualAndroidDriver
import io.appium.java_client.AppiumBy
import io.appium.java_client.AppiumDriver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import util.NO_DELIMITER_FORMAT
import util.findComposeElementByTag
import util.scrollForward
import util.takeScreenshotToFile
import java.nio.file.Path
import java.time.LocalDateTime
import kotlin.io.path.createDirectories
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object RunAutomationUseCase {
    private val baseDir = Path.of(System.getProperty("user.home")).resolve("screenshots")

    private lateinit var currentDateTime: LocalDateTime

    private val dir
        get() = baseDir.resolve(currentDateTime.format(NO_DELIMITER_FORMAT))

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

            currentDateTime = LocalDateTime.now()
            dir.createDirectories()

            appiumDriver.apply {
                takeScreenshot("start.png")

                // 開始ボタンクリック
                findComposeElementByTag("start_button").click()
//                findElement(AppiumBy.id("start_button")).click() // case of AndroidViewId
                delay(1.seconds)
                takeScreenshot(fileName = "start_dialog.png")

                // 構成名入力
                val nameField = findComposeElementByTag("assembly_name_text_field")
                nameField.click()
                delay(250.milliseconds)
                nameField.sendKeys(assemblyName)
                delay(1.seconds)
                takeScreenshot(fileName = "start_dialog_input_text.png")

                // 作成ボタンクリック
                findComposeElementByTag("create_assembly_button").click()
                delay(1.seconds)
                takeScreenshot(fileName = "select_parts_type.png")

                // PCケース
                findComposeElementByTag("pccase").click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_pc_case_list.png")

                // 3回スクロールして表示されている中からindex=2のアイテムをクリック
                scrollForward(times = 3)
                findElement(AppiumBy.androidUIAutomator("new UiSelector().index(2)")).click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_pc_case_add_dialog.png")

                // 追加する
                findComposeElementByTag("edit_assembly_button").click()
                delay(1.seconds)
                takeScreenshot(fileName = "assembly_list.png")

                // 追加したアイテムをクリック
                findComposeElementByTag("pccase").click()
                delay(1.seconds)
                takeScreenshot(fileName = "assembly_dialog_pc_case.png")

                // プラス、マイナス、プラス、変更
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
                delay(1.seconds)
                takeScreenshot(fileName = "assembly_list_2_items.png")

                // パーツ選択画面(PCケース)へ
                findComposeElementByTag("DeviceScreen").click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_pc_case_list_added_item.png")

                // ホーム画面へ
                findComposeElementByTag("TopScreen").click()
                delay(1.seconds)
                takeScreenshot(fileName = "top_composition_exists.png")

                // 作成済みの構成を選択
                findComposeElementByTag(assemblyName).click()
                delay(1.seconds)
                takeScreenshot(fileName = "top_edit_composition_dialog.png")

                // パーツを追加ボタンをクリック
                findComposeElementByTag("add_parts_button").click()
                delay(1.seconds)
                takeScreenshot(fileName = "select_parts_type_2.png")

                // スクロールしてマウスを選択
                scrollForward()
                findComposeElementByTag("mouse").click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_mouse_list.png")

                // 2回スクロールして表示されている中からindex=4のアイテムをクリック
                scrollForward(times = 2)
                findElement(AppiumBy.androidUIAutomator("new UiSelector().index(4)")).click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_mouse_add_dialog.png")

                // 追加する
                findComposeElementByTag("edit_assembly_button").click()
                delay(1.seconds)
                takeScreenshot(fileName = "assembly_list_3_items.png")

                // パーツ種類選択画面へ
                findComposeElementByTag("SelectionScreen").click()
                delay(1.seconds)
                takeScreenshot(fileName = "select_parts_type_3.png")

                // マザーボード
                findComposeElementByTag("motherboard").click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_motherboard_list.png")

                // ソートメニューボタンをクリック
                findComposeElementByTag("sort_menu_button").click()
                delay(1.seconds)
                takeScreenshot(fileName = "sort_menu_popularity_selected.png")

                // 新着順をクリック
                findComposeElementByTag("NEW_ARRIVAL").click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_motherboard_list_new_arrival_sort.png")

                // ソートメニューボタンをクリック
                findComposeElementByTag("sort_menu_button").click()
                delay(1.seconds)
                takeScreenshot(fileName = "sort_menu_new_arrival_selected.png")

                // 価格の安い順をクリック
                findComposeElementByTag("PRICE_ASC").click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_motherboard_list_price_asc_sort.png")

                // 検索バーをクリックしてasusを入力
                val searchField = findComposeElementByTag("search_text_field")
                searchField.click()
                delay(250.milliseconds)
                searchField.sendKeys("asus")
                delay(1.seconds)
                takeScreenshot(fileName = "parts_motherboard_list_search_asus.png")

                // index=0のアイテムをクリック
                findElement(AppiumBy.androidUIAutomator("new UiSelector().index(0)")).click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_motherboard_add_dialog.png")

                // 追加する
                findComposeElementByTag("edit_assembly_button").click()
                delay(1.seconds)
                takeScreenshot(fileName = "assembly_list_4_items.png")

                // 構成内のマウスをクリック
                findComposeElementByTag("mouse").click()
                delay(1.seconds)
                takeScreenshot(fileName = "assembly_dialog_mouse.png")

                // 削除をクリック
                findComposeElementByTag("delete_assembly_button").click()
                delay(1.seconds)
                takeScreenshot(fileName = "assembly_list_mouse_deleted.png")

                // パーツ選択画面(マザーボード)へ
                findComposeElementByTag("DeviceScreen").click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_motherboard_list_added_item.png")

                // 選択中のパーツの0番目をクリック
                findComposeElementByTag("selected_parts_list")
                    .findElement(AppiumBy.androidUIAutomator("new UiSelector().index(0)")).click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_motherboard_edit_dialog.png")

                // 削除をクリック
                findComposeElementByTag("delete_assembly_button").click()
                delay(1.seconds)
                takeScreenshot(fileName = "parts_motherboard_list_deleted.png")

                // 構成画面へ
                findComposeElementByTag("AssemblyScreen").click()
                delay(1.seconds)
                takeScreenshot(fileName = "assembly_list_motherboard_deleted.png")

                delay(30.seconds)
            }
        }
    }

    private fun AppiumDriver.takeScreenshot(fileName: String) {
        takeScreenshotToFile(dir.resolve(fileName))
    }
}
