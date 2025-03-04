package event.action

import data.ScreenShotName
import data.toSSName
import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * テキスト入力アクション
 */
@Serializable
data class ComposeInputTextAction(
    override val nextWait: Duration,
    override val screenshotName: ScreenShotName?,
    private val tag: String,
    val uiAutomatorText: String,
    val text: String,
) : EventAction() {

    override suspend fun execute(runner: EventRunner) {
        runner.inputTextCompose(this)
    }

    override fun getActionName(): String = "テキスト入力[$text]"

    override fun getActionTarget(): String = tag
}

class ComposeInputTextActionBuilder(
    private val tag: String,
    private val text: String,
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null

    override fun build(): EventAction {
        return ComposeInputTextAction(
            nextWait = nextWait,
            screenshotName = screenshotName?.toSSName(),
            tag = tag,
            uiAutomatorText = "new UiSelector().resourceId(\"$tag\")",
            text = text
        )
    }
}
