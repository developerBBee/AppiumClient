package event.action

import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * スクロールアクション
 */
@Serializable
data class ScrollAction(
    override val nextWait: Duration,
    override val screenshotName: String?,
    val repeat: Int = 1,
    val interval: Duration,
    val uiAutomatorText: String,
) : EventAction() {

    override suspend fun execute(runner: EventRunner) {
        runner.scroll(this)
    }
}

class ScrollActionBuilder(
    private val scrollableText: String
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null
    var direction: ScrollDirection = ScrollDirection.VERTICAL_FORWARD
    var repeat: Int = 1
    var interval: Duration = 1.seconds

    override fun build(): EventAction {
        return ScrollAction(
            nextWait = nextWait,
            screenshotName = screenshotName,
            repeat = repeat,
            interval = interval,
            uiAutomatorText = "$scrollableText${direction.scrollMethod}",
        )
    }
}

enum class ScrollDirection(val scrollMethod: String) {
    VERTICAL_FORWARD(".scrollForward()"),
    VERTICAL_BACKWARD(".scrollBackward()"),
    HORIZONTAL_FORWARD(".setAsHorizontalList().scrollForward()"),
    HORIZONTAL_BACKWARD(".setAsHorizontalList().scrollBackward()"),
}
