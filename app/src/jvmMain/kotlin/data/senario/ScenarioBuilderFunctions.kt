package data.senario

import event.action.AndroidKeyActionBuilder
import event.action.ComposeInputTextActionBuilder
import event.action.ComposeTapActionBuilder
import event.action.ComposeTapChildActionBuilder
import event.action.HideKeyboardActionBuilder
import event.action.InputTextActionBuilder
import event.action.LongTapTextActionBuilder
import event.action.RepeatTapsActionBuilder
import event.action.ScrollActionBuilder
import event.action.TapActionBuilder
import event.action.TapChildActionBuilder
import event.action.TapTextActionBuilder
import event.action.UiAutomatorInputActionBuilder
import event.action.UiAutomatorTapActionBuilder
import io.appium.java_client.android.nativekey.AndroidKey

/*
 * シナリオスコープ
 */
inline fun ViewScenario.scenario(block: ViewScenario.() -> Unit): ViewScenario {
    return apply { block() }
}

inline fun ComposeScenario.scenario(block: ComposeScenario.() -> Unit): ComposeScenario {
    return apply { block() }
}

/*
 * View Compose 共通のアクション
 */
inline fun Scenario.androidKey(
    key: AndroidKey,
    block: AndroidKeyActionBuilder.() -> Unit = {}
) {
    AndroidKeyActionBuilder(key = key)
        .apply(block)
        .build()
        .also { add(it) }
}

/** 画面内にScrollViewが１つなら、引数指定なしで動作する */
inline fun Scenario.scroll(
    viewId: String? = null,
    scrollable: String = "new UiSelector().className(\"android.widget.ScrollView\")",
    block: ScrollActionBuilder.() -> Unit = {}
) {
    val scrollUiSelector = if (viewId != null) {
        "new UiSelector().resourceId(\"$viewId\")"
    } else {
        scrollable
    }

    ScrollActionBuilder(
        scrollableText = "new UiScrollable($scrollUiSelector)"
    )
        .apply(block)
        .build()
        .also { add(it) }
}

inline fun Scenario.uiAutomatorTap(
    uiAutomatorText: String,
    block: UiAutomatorTapActionBuilder.() -> Unit = {}
) {
    UiAutomatorTapActionBuilder(uiAutomatorText = uiAutomatorText,)
        .apply(block)
        .build()
        .also { add(it) }
}

inline fun Scenario.uiAutomatorInput(
    uiAutomatorText: String,
    text: String,
    block: UiAutomatorInputActionBuilder.() -> Unit = {}
) {
    UiAutomatorInputActionBuilder(uiAutomatorText = uiAutomatorText, text = text)
        .apply(block)
        .build()
        .also { add(it) }
}

inline fun Scenario.hideKeyboard(
    block: HideKeyboardActionBuilder.() -> Unit = {}
) {
    HideKeyboardActionBuilder()
        .apply(block)
        .build()
        .also { add(it) }
}

/*
 * View 専用のアクション
 */
inline fun ViewScenario.tap(
    viewId: String,
    block: TapActionBuilder.() -> Unit = {}
) {
    TapActionBuilder(viewId = viewId)
        .apply(block)
        .build()
        .also { add(it) }
}

inline fun ViewScenario.tapChild(
    viewId: String,
    childIndex: Int,
    block: TapChildActionBuilder.() -> Unit = {}
) {
    TapChildActionBuilder(viewId = viewId, childIndex = childIndex)
        .apply(block)
        .build()
        .also { add(it) }
}

inline fun ViewScenario.tapText(
    text: String,
    block: TapTextActionBuilder.() -> Unit = {}
) {
    TapTextActionBuilder(text = text)
        .apply(block)
        .build()
        .also { add(it) }
}

inline fun ViewScenario.longTapText(
    text: String,
    block: LongTapTextActionBuilder.() -> Unit = {}
) {
    LongTapTextActionBuilder(text = text)
        .apply(block)
        .build()
        .also { add(it) }
}

inline fun ViewScenario.inputText(
    viewId: String,
    text: String,
    block: InputTextActionBuilder.() -> Unit = {}
) {
    InputTextActionBuilder(viewId = viewId, text = text)
        .apply(block)
        .build()
        .also { add(it) }
}

inline fun ViewScenario.repeatTaps(
    viewId: String,
    block: RepeatTapsActionBuilder.() -> Unit = {}
) {
    RepeatTapsActionBuilder(viewId = viewId)
        .apply(block)
        .build()
        .also { add(it) }
}

/*
 * Compose 専用のアクション
 */
inline fun ComposeScenario.tap(
    tag: String,
    block: ComposeTapActionBuilder.() -> Unit = {},
) {
    ComposeTapActionBuilder(tag = tag)
        .apply(block)
        .build()
        .also { add(it) }
}

inline fun ComposeScenario.inputText(
    tag: String,
    text: String,
    block: ComposeInputTextActionBuilder.() -> Unit = {},
) {
    ComposeInputTextActionBuilder(tag = tag, text = text)
        .apply(block)
        .build()
        .also { add(it) }
}

inline fun ComposeScenario.tapChild(
    tag: String,
    childIndex: Int,
    block: ComposeTapChildActionBuilder.() -> Unit = {}
) {
    ComposeTapChildActionBuilder(tag = tag, childIndex = childIndex)
        .apply(block)
        .build()
        .also { add(it) }
}
