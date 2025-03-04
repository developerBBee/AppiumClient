package event.action

import data.ScreenShotName
import data.toSSName
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
    override val screenshotName: ScreenShotName?,
    val action: ViewAction,
    val uiAutomatorText: String,
) : EventAction() {

    private val actionName = when (action) {
        ViewAction.TapAction -> "タップ"
        is ViewAction.InputText -> "テキスト入力[${action.text}]"
    }
    override suspend fun execute(runner: EventRunner) {
        runner.runUiAutomator(this)
    }

    override fun getActionName(): String = actionName

    override fun getActionTarget(): String = ""
}

class UiAutomatorTapActionBuilder(
    private val uiAutomatorText: String,
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null

    override fun build(): EventAction {
        return UiAutomatorAction(
            nextWait = nextWait,
            screenshotName = screenshotName?.toSSName(),
            action = ViewAction.TapAction,
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
            screenshotName = screenshotName?.toSSName(),
            action = ViewAction.InputText(text = text),
            uiAutomatorText = uiAutomatorText,
        )
    }
}

sealed interface ViewAction {
    data object TapAction : ViewAction
    data class InputText(val text: String) : ViewAction
}
