package data

import kotlinx.serialization.Serializable
import util.Constant
import util.Constant.DEFAULT_APP
import util.Constant.USER_HOME
import java.io.File

/**
 * Represents the Appium capabilities.
 */
@Serializable
data class AppiumConfiguration(
    val host: String = Constant.DEFAULT_HOST,
    val port: Int = Constant.DEFAULT_PORT,
    val path: String = Constant.DEFAULT_PATH,
    val sslEnabled: Boolean = Constant.DEFAULT_SSL_ENABLED,
    val udid: String = Constant.DEFAULT_UDID,
    val app: String = DEFAULT_APP_PATH,
)

private val DEFAULT_APP_PATH = File(System.getProperty(USER_HOME), DEFAULT_APP).absolutePath
