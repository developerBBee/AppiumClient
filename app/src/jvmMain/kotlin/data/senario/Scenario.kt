package data.senario

import event.action.*
import kotlinx.serialization.Serializable

@DslEventAction
@Serializable
sealed interface Scenario {
    fun getActions(): List<EventAction>
    fun add(action: EventAction)
}

@Serializable
data class ViewScenario(
    private val actions: MutableList<EventAction> = mutableListOf(),
): Scenario {

    override fun getActions(): List<EventAction> = actions.toList()

    override fun add(action: EventAction) {
        actions.add(action)
    }
}

/**
 * Composeのシナリオ
 *
 * testTagが必要
 * @see <a href=https://developer.android.com/develop/ui/compose/testing/interoperability#uiautomator-interop>Docs</a>
 * <pre>
 * Modifier.semantics { testTagsAsResourceId = true }
 * Modifier.testTag("start_button")
 * </pre>
 */
@Serializable
data class ComposeScenario(
    private val actions: MutableList<EventAction> = mutableListOf(),
): Scenario {

    override fun getActions(): List<EventAction> = actions.toList()

    override fun add(action: EventAction) {
        actions.add(action)
    }
}
