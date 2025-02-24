package event.action

import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class LongTapTextAction(
    override val nextWait: Duration,
    override val screenshotName: String?,
    private val text: String,
) : EventAction() {

    val uiAutomatorText = "new UiSelector().text(\"$text\")"

    override suspend fun execute(runner: EventRunner) {
        runner.longTapText(this)
    }

    override fun getActionName(): String = "長押し"

    override fun getActionTarget(): String = text
}

class LongTapTextActionBuilder(
    private val text: String,
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null

    override fun build(): EventAction {
        return LongTapTextAction(
            nextWait = nextWait,
            screenshotName = screenshotName,
            text = text
        )
    }
}
