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
data class InputTextAction(
    override val nextWait: Duration,
    override val screenshotName: ScreenShotName?,
    val viewId: String,
    val text: String,
) : EventAction() {

    override suspend fun execute(runner: EventRunner) {
        runner.inputText(this)
    }

    override fun getActionName(): String = "テキスト入力[$text]"

    override fun getActionTarget(): String = viewId
}

class InputTextActionBuilder(
    private val viewId: String,
    private val text: String,
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null

    override fun build(): EventAction {
        return InputTextAction(
            viewId = viewId,
            nextWait = nextWait,
            screenshotName = screenshotName?.toSSName(),
            text = text
        )
    }
}
