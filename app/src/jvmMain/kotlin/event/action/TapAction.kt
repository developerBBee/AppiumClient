package event.action

import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * タップアクション
 */
@Serializable
data class TapAction(
    override val nextWait: Duration,
    override val screenshotName: String?,
    val viewId: String,
) : EventAction() {

    override suspend fun execute(runner: EventRunner) {
        runner.tap(this)
    }

    override fun getActionName(): String = "タップ"

    override fun getActionTarget(): String = viewId
}

class TapActionBuilder(
    private val viewId: String,
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null

    override fun build(): EventAction {
        return TapAction(
            viewId = viewId,
            nextWait = nextWait,
            screenshotName = screenshotName
        )
    }
}
