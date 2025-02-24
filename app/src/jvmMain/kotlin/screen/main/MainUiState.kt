package screen.main

import data.TargetId
import event.action.EventAction

sealed interface MainRunState {
    data object Idle : MainRunState
    data object Running : MainRunState
    data object Cancelling : MainRunState
    data class Finished(val isCompletion: Boolean) : MainRunState
    data class Error(val message: String) : MainRunState
}

data class MainUiState(
    val targetId: TargetId,
    val targetName: String,
    val currentIndex: Int,
    val actions: List<EventAction>,
    val runState: MainRunState,
) {

    val progress: Boolean = when (runState) {
        MainRunState.Running,
        MainRunState.Cancelling -> true
        else -> false
    }

    val showCopyToClipboard: Boolean = runState is MainRunState.Error

    val buttonText: String = when (runState) {
        MainRunState.Running -> "キャンセル"
        MainRunState.Cancelling -> "キャンセル中"
        else -> "実行"
    }

    val message: String = when (runState) {
        is MainRunState.Finished -> "終了 [実行完了=${runState.isCompletion}]"
        is MainRunState.Error -> runState.message
        else -> ""
    }

    val buttonState: ButtonState = when (runState) {
        MainRunState.Idle,
        is MainRunState.Error,
        is MainRunState.Finished -> ButtonState.RUNNABLE
        MainRunState.Running -> ButtonState.CANCELABLE
        MainRunState.Cancelling -> ButtonState.DISABLE
    }
}

enum class ButtonState {
    DISABLE,
    RUNNABLE,
    CANCELABLE
}
