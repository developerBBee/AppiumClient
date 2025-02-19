package screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.TargetId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import usecase.*

class MainViewModel : ViewModel() {

    private val mutex = Mutex()

    private val _mainStateFlow = MutableStateFlow<Map<TargetId, MainState>>(emptyMap())
    val mainStateFlow: StateFlow<Map<TargetId, MainState>> = _mainStateFlow.asStateFlow()

    private val jobs: MutableMap<TargetId, Job> = mutableMapOf()
    private var _serverProcess: Process? = null
    private val serverProcess: Process get() = requireNotNull(_serverProcess)

    private var loadingJob: Job? = null

    private var ipAddress: String? = GetPrivateIpAddressUseCase()

    init {
        loadingJob = GetTargetsUseCase()
            .map { targets ->
                ipAddress?.let { privateIpAddress ->
                    targets.map { target ->
                        target.copy(configuration = target.configuration.copy(host = privateIpAddress))
                    }.also { SaveConfigUseCase(it) }
                } ?: targets
            }
            .onEach { targets ->
                _mainStateFlow.value = targets.associate { target ->
                    target.id to MainState(
                        targetId = target.id,
                        targetName = target.name,
                        runState = MainRunState.Idle,
                    )
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    suspend fun runAll() {
        TODO()
    }

    suspend fun run(targetId: TargetId) {
        if (jobs[targetId] != null) return

        updateRunState(
            targetId = targetId,
            runState = MainRunState.Running(log = "Start")
        )

        val target = GetTargetsUseCase().first().first { it.id == targetId }

        mutex.withLock {
            if (_serverProcess == null) {
                try {
                    _serverProcess = LaunchServerUseCase(
                        host = target.configuration.host,
                        port = target.configuration.port,
                    )
                } catch (ex: CancellationException) {
                    throw ex
                } catch (ex: Exception) {
                    detectError(ex = ex, targetId = targetId)
                    return
                }
            }
        }

        jobs[targetId] = ExecuteEventsUseCase(target = target)
            .onEach { log ->
                updateRunState(
                    targetId = targetId,
                    runState = MainRunState.Running(log = log)
                )
            }
            .onCompletion {
                removeJob(targetId)

                val runState = when (it) {
                    null -> MainRunState.Finished(isCompletion = true)
                    is CancellationException -> MainRunState.Finished(isCompletion = false)
                    else -> MainRunState.Error(message = it.stackTraceToString())
                }

                updateRunState(targetId = targetId, runState = runState)
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope + CoroutineName(targetId.toString()))
    }

    private suspend fun removeJob(targetId: TargetId) {
        println("remove job [targetId=$targetId]")

        jobs.remove(targetId)

        mutex.withLock {
            if (jobs.isEmpty() && _serverProcess != null) {
                StopServerUseCase(process = serverProcess)
                _serverProcess = null
            }
        }
    }

    suspend fun cancel(targetId: TargetId) {
        updateRunState(
            targetId = targetId,
            runState = MainRunState.Cancelling(log = "Cancelling")
        )

        jobs[targetId]?.cancelAndJoin()
        removeJob(targetId)

        updateRunState(
            targetId = targetId,
            runState = MainRunState.Finished(isCompletion = false)
        )
    }

    private fun detectError(
        ex: Exception,
        targetId: TargetId,
    ) {
        updateRunState(
            targetId = targetId,
            runState = MainRunState.Error(message = ex.stackTraceToString())
        )
    }

    private fun updateRunState(targetId: TargetId, runState: MainRunState) {
        val targetState = mainStateFlow.value.getValue(targetId)
        _mainStateFlow.value += (targetId to targetState.copy(runState = runState))
    }

    override fun onCleared() {
        _serverProcess?.destroy()
    }
}

sealed interface MainRunState {
    data object Idle : MainRunState
    data class Running(val log: String) : MainRunState
    data class Cancelling(val log: String) : MainRunState
    data class Finished(val isCompletion: Boolean) : MainRunState
    data class Error(val message: String) : MainRunState
}

data class MainState(
    val targetId: TargetId,
    val targetName: String,
    val runState: MainRunState,
)
