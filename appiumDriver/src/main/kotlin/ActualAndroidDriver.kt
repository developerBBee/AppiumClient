import data.AppiumConfiguration
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options

class ActualAndroidDriver : ActualDriver() {
    private var driver: AndroidDriver? = null

    override fun open(config: AppiumConfiguration) {
        val options = UiAutomator2Options()
            .setUdid(config.udid)
            .setApp(config.app)

        driver = AndroidDriver(
            config.uri.toURL(),
            options,
        )
    }

    override fun getDriver(): AppiumDriver {
        return checkNotNull(driver) { throw DriverNotPreparedException() }
    }

    override fun close() {
        driver?.quit()
    }
}