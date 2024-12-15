package usecase

import ActualAndroidDriver
import io.appium.java_client.AppiumBy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

object RunAutomationUseCase {
    suspend operator fun invoke() {
        ActualAndroidDriver().use { driver ->
            val config = GetConfigUseCase().first()

            withTimeout(30.seconds) {
                driver.open(config)
            }

            val appiumDriver = driver.getDriver()

            delay(5000)
            repeat(100) {
                appiumDriver.findElement(AppiumBy.id("button_first")).click()

                delay(1000)

                appiumDriver.findElement(AppiumBy.id("button_second")).click()

                delay(1000)
            }
        }
    }
}
