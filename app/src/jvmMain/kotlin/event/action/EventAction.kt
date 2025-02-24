package event.action

import event.runner.EventRunner
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@DslMarker
annotation class DslEventAction

@Serializable
sealed class EventAction {
    abstract val nextWait: Duration
    abstract val screenshotName: String?

    abstract suspend fun execute(runner: EventRunner)

    abstract fun getActionName(): String
    abstract fun getActionTarget(): String
}

@DslEventAction
sealed interface EventActionBuilder {
    fun build(): EventAction
}
