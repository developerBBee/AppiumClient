package event.action

import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * 一致するテキストをタップするアクション
 */
@Serializable
data class TapTextAction(
    override val nextWait: Duration,
    override val screenshotName: String?,
    private val text: String,
) : EventAction() {

    val uiAutomatorText
        get() = "new UiSelector().text(\"$text\")"

    override suspend fun execute(runner: EventRunner) {
        runner.tapText(this)
    }

    override fun getActionName(): String = "タップ"

    override fun getActionTarget(): String = text
}

class TapTextActionBuilder(
    private val text: String,
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null

    override fun build(): EventAction {
        return TapTextAction(
            nextWait = nextWait,
            screenshotName = screenshotName,
            text = text
        )
    }
}
