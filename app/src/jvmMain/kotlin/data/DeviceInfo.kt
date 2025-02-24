package data

import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    val deviceName: String,
    val statusBarHeight: Int,
    val navigationBarHeight: Int,
) {
    val info: String = "$deviceName [Top=$statusBarHeight, Bottom=$navigationBarHeight]"
}

/**
 * StatusBar / NavigationBar heights
 * ```
 * adb shell dumpsys window StatusBar | grep statusBars
 * adb shell dumpsys window Taskbar | grep navigationBars
 * ```
 */
enum class PresetModel(val deviceInfo: DeviceInfo) {
    PIXEL_8_PRO_BUTTON_NAVIGATION(
        DeviceInfo(
            deviceName = "Pixel 8 Pro (Button)",
            statusBarHeight = 72,
            navigationBarHeight = 144,
        ),
    ),
    PIXEL_8_PRO_GESTURE_NAVIGATION(
        DeviceInfo(
            deviceName = "Pixel 8 Pro (Gesture)",
            statusBarHeight = 72,
            navigationBarHeight = 48,
        ),
    ),
    PIXEL_TABLET_BUTTON_NAVIGATION(
        DeviceInfo(
            deviceName = "Pixel Tablet (Button)",
            statusBarHeight = 48,
            navigationBarHeight = 96,
        ),
    ),
    PIXEL_TABLET_GESTURE_NAVIGATION(
        DeviceInfo(
            deviceName = "Pixel Tablet (Gesture)",
            statusBarHeight = 48,
            navigationBarHeight = 64,
        ),
    ),
}
