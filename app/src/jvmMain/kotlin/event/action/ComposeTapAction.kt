package event.action

import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Compose testTag を見つけてタップする
 */
@Serializable
class ComposeTapAction (
    override val nextWait: Duration,
    override val screenshotName: String?,
    private val tag: String,
    val uiAutomatorText: String,
) : EventAction() {

    override suspend fun execute(runner: EventRunner) {
        runner.tapCompose(this)
    }

    override fun getActionName(): String = "タップ"

    override fun getActionTarget(): String = tag
}

class ComposeTapActionBuilder(
    val tag: String,
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null

    override fun build(): EventAction {
        return ComposeTapAction(
            nextWait = nextWait,
            screenshotName = screenshotName,
            tag = tag,
            uiAutomatorText = "new UiSelector().resourceId(\"$tag\")",
        )
    }
}
