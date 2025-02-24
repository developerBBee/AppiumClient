package event.action

import event.runner.EventRunner
import io.appium.java_client.android.nativekey.AndroidKey
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Androidのキーコード入力アクション
 */
@Serializable
data class AndroidKeyAction(
    override val nextWait: Duration,
    override val screenshotName: String?,
    val key: AndroidKey,
) : EventAction() {

    override suspend fun execute(runner: EventRunner) {
        runner.inputAndroidKey(this)
    }

    override fun getActionName(): String = key.name

    override fun getActionTarget(): String = ""
}

class AndroidKeyActionBuilder(
    private val key: AndroidKey
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null

    override fun build(): EventAction {
        return AndroidKeyAction(
            nextWait = nextWait,
            screenshotName = screenshotName,
            key = key
        )
    }
}
