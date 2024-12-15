import data.AppiumConfiguration
import io.appium.java_client.AppiumDriver

abstract class ActualDriver : AutoCloseable {
    abstract fun open(config: AppiumConfiguration)

    abstract fun getDriver(): AppiumDriver
}