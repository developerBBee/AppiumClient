package event.action

import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * 連打アクション
 */
@Serializable
data class RepeatTapsAction(
    override val nextWait: Duration,
    override val screenshotName: String?,
    val viewId: String,
    val repeat: Int,
    val interval: Duration,
) : EventAction() {

    override suspend fun execute(runner: EventRunner) {
        runner.repeatTaps(this)
    }

    override fun getActionName(): String = "連打 ${interval}間隔 ${repeat}回"

    override fun getActionTarget(): String = viewId
}

class RepeatTapsActionBuilder(
    private val viewId: String
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null
    var repeat: Int = 10
    var interval: Duration = 100.milliseconds

    override fun build(): EventAction {
        return RepeatTapsAction(
            viewId = viewId,
            nextWait = nextWait,
            screenshotName = screenshotName,
            repeat = repeat,
            interval = interval
        )
    }
}
