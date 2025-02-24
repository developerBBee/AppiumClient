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
) : EventAction() {

    private val actionName = when (action) {
        ViewAction.TapAction -> "タップ"
        is ViewAction.InputText -> "テキスト入力[${action.text}]"
    }
    override suspend fun execute(runner: EventRunner) {
        runner.runUiAutomator(this)
    }

    override fun getActionName(): String = actionName

    override fun getActionTarget(): String {
        TODO("Not yet implemented")
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
            screenshotName = screenshotName,
            action = ViewAction.InputText(text = text),
            uiAutomatorText = uiAutomatorText,
        )
    }
}

sealed interface ViewAction {
    data object TapAction : ViewAction
    data class InputText(val text: String) : ViewAction
}
