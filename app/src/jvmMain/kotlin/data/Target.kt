package data

import data.senario.SAMPLE_PHONE_EMU_SCENARIO
import data.senario.Scenario
import data.senario.ScenarioName
import kotlinx.serialization.Serializable

/**
 * Represents the auto running target.
 */
@Serializable
data class Target(
    val id: TargetId,
    val name: String,
    val deviceInfo: DeviceInfo,
    val scenarioName: ScenarioName,
    val configuration: AppiumConfiguration,
) {

    val scenario: Scenario get() = when (scenarioName) {
        ScenarioName.EMU_PHONE_SAMPLE -> SAMPLE_PHONE_EMU_SCENARIO
    }
}

fun List<Target>.nextId(): TargetId = maxOf { it.id }.next()

@Serializable
@JvmInline
value class TargetId(private val id: Long) : Comparable<TargetId> {

    fun next(): TargetId = TargetId(id + 1)

    override fun compareTo(other: TargetId): Int {
        return id.compareTo(other.id)
    }

    override fun toString(): String = id.toString()
}

// Preset targets
val EMU_SAMPLE_TARGET = Target(
    id = TargetId(0),
    name = "サンプル(Pixel 8 Pro)",
    deviceInfo = PresetModel.PIXEL_8_PRO_BUTTON_NAVIGATION.deviceInfo,
    scenarioName = ScenarioName.EMU_PHONE_SAMPLE,
    configuration = AppiumConfiguration(),
)