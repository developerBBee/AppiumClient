package event.action

import data.ScreenShotName
import data.toSSName
import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * 子Viewに対するタップアクション
 */
@Serializable
data class ComposeTapChildAction(
    override val nextWait: Duration,
    override val screenshotName: ScreenShotName?,
    val tag: String,
    private val childIndex: Int,
    private val subTag: String?, // nullの場合は子View自体をタップ、指定された場合は子Viewが持つviewIdをタップ
) : EventAction() {

    val parentUiAutomatorText = "new UiSelector().resourceId(\"$tag\")"

    val childUiAutomatorText get() = if (subTag == null) {
        "new UiSelector().index($childIndex)"
    } else {
        "new UiSelector().index($childIndex).childSelector(new UiSelector().resourceId(\"$subTag\"))"
    }

    override suspend fun execute(runner: EventRunner) {
        runner.tapChildCompose(this)
    }

    override fun getActionName(): String = "子タップ"

    override fun getActionTarget(): String {
        val subItem = subTag?.let { "の$it" } ?: ""
        return "${tag}の子番号$childIndex$subItem"
    }
}

class ComposeTapChildActionBuilder(
    private val tag: String,
    private val childIndex: Int,
) : EventActionBuilder {
    var nextWait: Duration = 1.seconds
    var screenshotName: String? = null
    var subTag: String? = null

    override fun build(): EventAction {
        return ComposeTapChildAction(
            tag = tag,
            childIndex = childIndex,
            nextWait = nextWait,
            screenshotName = screenshotName?.toSSName(),
            subTag = subTag
        )
    }
}
