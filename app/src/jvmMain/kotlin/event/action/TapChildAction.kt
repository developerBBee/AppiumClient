package event.action

import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * 子Viewに対するタップアクション
 */
@Serializable
data class TapChildAction(
    override val nextWait: Duration,
    override val screenshotName: String?,
    val viewId: String,
    private val childIndex: Int,
    private val subViewId: String?, // nullの場合は子View自体をタップ、指定された場合は子Viewが持つviewIdをタップ
) : EventAction() {

    val uiAutomatorText get() = if (subViewId == null) {
        "new UiSelector().index($childIndex)"
    } else {
        "new UiSelector().index($childIndex).childSelector(new UiSelector().resourceId(\"$subViewId\"))"
    }

    override suspend fun execute(runner: EventRunner) {
        runner.tapChild(this)
    }
}

class TapChildActionBuilder(
    private val viewId: String,
    private val childIndex: Int,
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null
    var subViewId: String? = null

    override fun build(): EventAction {
        return TapChildAction(
            viewId = this@TapChildActionBuilder.viewId,
            nextWait = nextWait,
            screenshotName = screenshotName,
            childIndex = childIndex,
            subViewId = subViewId
        )
    }
}
