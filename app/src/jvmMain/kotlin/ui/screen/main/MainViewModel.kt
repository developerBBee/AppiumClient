package ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Target
import data.TargetId
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import usecase.ExecuteEventsUseCase
import usecase.GetPrivateIpAddressUseCase
import usecase.GetTargetsUseCase
import usecase.LaunchServerUseCase
import usecase.SaveConfigUseCase
import usecase.SetScreenshotTargetUseCase
import usecase.StopServerUseCase
import kotlin.coroutines.cancellation.CancellationException

class MainViewModel : ViewModel() {

    private val mutex = Mutex()

    private val _uiStateFlow = MutableStateFlow<List<MainUiState>>(emptyList())
    val uiStateFlow: StateFlow<List<MainUiState>> = _uiStateFlow.asStateFlow()

    private val jobs: MutableMap<TargetId, Job> = mutableMapOf()
    private var _serverProcess: Process? = null
    private val serverProcess: Process get() = requireNotNull(_serverProcess)

    private var privateIpAddress: String? = GetPrivateIpAddressUseCase()

    private val targetsFlow = MutableStateFlow<List<Target>>(emptyList())

    init {
        GetTargetsUseCase()
            .map { targets ->
                privateIpAddress
                    ?.let { privateIpAddress ->
                        // ホストマシンのプライベートIPアドレスでコンフィグのホストを設定する
                        targets
                            .map { it.copy(configuration = it.configuration.copy(host = privateIpAddress)) }
                            .also { SaveConfigUseCase(it) }
                    }
                    ?: targets
            }
            .onEach { targetsFlow.value = it }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)

        targetsFlow
            .onEach { targets ->
                _uiStateFlow.value = targets
                    .map { target ->
                        MainUiState(
                            targetId = target.id,
                            targetName = target.name,
                            currentIndex = -1,
                            actions = target.scenario.getActions(),
                            runState = MainRunState.Idle,
                        )
                    }
            }
            .launchIn(viewModelScope)
    }

    fun runAll() {
        TODO()
    }

    fun run(targetId: TargetId) {
        if (jobs[targetId] != null) return

        updateRunState(
            targetId = targetId,
            runState = MainRunState.Running
        )

        viewModelScope.launch {
            runEvents(targetId = targetId)
        }
    }

    suspend fun runEvents(targetId: TargetId) {
        val target = targetsFlow.value.first { it.id == targetId }

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
            .onEach { index ->
                updateIndex(
                    targetId = targetId,
                    index = index,
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

    fun cancel(targetId: TargetId) {
        updateRunState(
            targetId = targetId,
            runState = MainRunState.Cancelling
        )

        viewModelScope.launch {
            jobs[targetId]?.cancelAndJoin()
            removeJob(targetId)

            updateRunState(
                targetId = targetId,
                runState = MainRunState.Finished(isCompletion = false)
            )
        }
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

    private fun updateRunState(
        targetId: TargetId,
        runState: MainRunState,
    ) {
        _uiStateFlow.update { states ->
            states.map {
                if (it.targetId == targetId) {
                    it.copy(runState = runState)
                } else {
                    it
                }
            }
        }
    }

    private fun updateIndex(
        targetId: TargetId,
        index: Int,
    ) {
        _uiStateFlow.update { states ->
            states.map {
                if (it.targetId == targetId) {
                    it.copy(currentIndex = index)
                } else {
                    it
                }
            }
        }
    }

    fun changeCurrentTarget(targetId: TargetId) {
        val target = targetsFlow.value.first { it.id == targetId }

        SetScreenshotTargetUseCase(target = target)
    }

    override fun onCleared() {
        _serverProcess?.destroy()
    }
}
