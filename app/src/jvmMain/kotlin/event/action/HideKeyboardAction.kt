package event.action

import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * ソフトキーボードを消すアクション
 */
@Serializable
data class HideKeyboardAction(
    override val nextWait: Duration,
    override val screenshotName: String?,
) : EventAction() {

    override suspend fun execute(runner: EventRunner) {
        runner.hideKeyboard(this)
    }
}

class HideKeyboardActionBuilder : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null

    override fun build(): EventAction {
        return HideKeyboardAction(
            nextWait = nextWait,
            screenshotName = screenshotName,
        )
    }
}
