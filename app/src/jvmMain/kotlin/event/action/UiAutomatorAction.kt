package event.action

import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * UiAutomator実行アクション
 */
@Serializable
data class UiAutomatorAction(
    override val nextWait: Duration,
    override val screenshotName: String?,
    val action: ViewAction,
    val uiAutomatorText: String,
    val actionData: String? = null,
) : EventAction() {

    override suspend fun execute(runner: EventRunner) {
        runner.runUiAutomator(this)
    }
}

class UiAutomatorTapActionBuilder(
    private val uiAutomatorText: String,
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null

    override fun build(): EventAction {
        return UiAutomatorAction(
            nextWait = nextWait,
            screenshotName = screenshotName,
            action = ViewAction.TAP,
            uiAutomatorText = uiAutomatorText,
        )
    }
}

class UiAutomatorInputActionBuilder(
    private val uiAutomatorText: String,
    private val text: String,
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null

    override fun build(): EventAction {
        return UiAutomatorAction(
            nextWait = nextWait,
            screenshotName = screenshotName,
            action = ViewAction.INPUT_TEXT,
            uiAutomatorText = uiAutomatorText,
            actionData = text,
        )
    }
}

enum class ViewAction {
    TAP,
    INPUT_TEXT,
}
