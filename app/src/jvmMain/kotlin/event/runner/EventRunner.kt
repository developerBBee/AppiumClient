package event.runner

import event.action.*
import org.openqa.selenium.remote.SessionId

interface EventRunner : AutoCloseable {
    suspend fun start(): SessionId
    suspend fun tap(action: TapAction)
    suspend fun tapText(action: TapTextAction)
    suspend fun tapChild(action: TapChildAction)
    suspend fun longTapText(action: LongTapTextAction)
    suspend fun repeatTaps(action: RepeatTapsAction)
    suspend fun inputText(action: InputTextAction)
    suspend fun inputAndroidKey(action: AndroidKeyAction)
    suspend fun scroll(action: ScrollAction)
    suspend fun runUiAutomator(action: UiAutomatorAction)
    suspend fun hideKeyboard(action: HideKeyboardAction)

    // For compose
    suspend fun tapCompose(action: ComposeTapAction)
    suspend fun inputTextCompose(action: ComposeInputTextAction)
    suspend fun tapChildCompose(action: ComposeTapChildAction)
}
