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
    private val direction: ScrollDirection,
    val repeat: Int = 1,
    val interval: Duration,
    val uiAutomatorText: String,
) : EventAction() {

    private val directionName = when (direction) {
        ScrollDirection.VERTICAL_FORWARD -> "下スクロール"
        ScrollDirection.VERTICAL_BACKWARD -> "上スクロール"
        ScrollDirection.HORIZONTAL_FORWARD -> "右スクロール"
        ScrollDirection.HORIZONTAL_BACKWARD -> "左スクロール"
    }
    override suspend fun execute(runner: EventRunner) {
        runner.scroll(this)
    }

    override fun getActionName(): String {
        val repeatAction = if (repeat > 1) {
            "${interval}間隔 ${repeat}回 "
        } else {
            ""
        }
        return "$repeatAction$directionName"
    }

    override fun getActionTarget(): String = ""
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
            direction = direction,
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
