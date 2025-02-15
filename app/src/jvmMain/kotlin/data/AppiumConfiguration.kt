package data

import kotlinx.serialization.Serializable
import util.Constant
import java.io.File
import java.net.URI

/**
 * Represents the Appium capabilities.
 */
@Serializable
data class AppiumConfiguration(
    val host: String = Constant.DEFAULT_HOST,
    val port: Int = Constant.DEFAULT_PORT,
//    val path: String = Constant.DEFAULT_PATH,
//    val sslEnabled: Boolean = Constant.DEFAULT_SSL_ENABLED,
    val udid: String = Constant.DEFAULT_UDID,
    val app: String = DEFAULT_APP_PATH,
) {
//    @Transient
//    private val scheme = if (sslEnabled) Constant.SECURE_SCHEME else Constant.DEFAULT_SCHEME
//
//    @Transient
//    val uri: URI = runCatching { URI.create("$scheme://$host:$port$path") }.getOrDefault(DEFAULT_URI)
}

private val DEFAULT_APP_PATH = File(System.getProperty(Constant.USER_HOME), Constant.DEFAULT_APP).absolutePath

val DEFAULT_URI = URI(
    Constant.DEFAULT_SCHEME,
    null,
    Constant.DEFAULT_HOST,
    Constant.DEFAULT_PORT,
    Constant.DEFAULT_PATH,
    null,
    null
)
