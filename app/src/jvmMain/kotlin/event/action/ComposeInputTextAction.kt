package event.action

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
    override val screenshotName: String?,
    val uiAutomatorText: String,
    val text: String,
) : EventAction() {

    override suspend fun execute(runner: EventRunner) {
        runner.inputTextCompose(this)
    }
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
            screenshotName = screenshotName,
            uiAutomatorText = "new UiSelector().resourceId(\"$tag\")",
            text = text
        )
    }
}
