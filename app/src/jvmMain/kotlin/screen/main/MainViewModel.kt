package screen.main

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import usecase.RunAutomationUseCase
import util.IOScope

class MainViewModel {
    private val handler = CoroutineExceptionHandler { _, th -> detectError(th) }

    private val _mainStateFlow = MutableStateFlow<MainState>(MainState.Idle)
    val mainStateFlow: StateFlow<MainState> = _mainStateFlow.asStateFlow()

    private var job: Job? = null

    suspend fun run() {
        job?.cancelAndJoin()
        _mainStateFlow.value = MainState.Running(log = "Start")
        job = IOScope.launch(handler) {
            RunAutomationUseCase() // TODO: flowにして途中経過をcollectする
            _mainStateFlow.value = MainState.Finished(isCompletion = true)
        }
    }

    suspend fun cancel() {
        _mainStateFlow.value = MainState.Cancelling(log = "Cancelling")
        job?.cancelAndJoin()
        job = null
        _mainStateFlow.value = MainState.Finished(isCompletion = false)
    }

    private fun detectError(th: Throwable) {
        _mainStateFlow.value = MainState.Error(message = th.stackTraceToString())
    }

    fun dispose() {
        job?.cancel()
    }
}

sealed interface MainState {
    data object Idle : MainState
    data class Running(val log: String) : MainState // TODO: logはrepositoryに保持する
    data class Cancelling(val log: String) : MainState
    data class Finished(val isCompletion: Boolean) : MainState
    data class Error(val message: String) : MainState
}
