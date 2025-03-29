package ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Target
import data.TargetId
import io.appium.java_client.service.local.AppiumDriverLocalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.jetbrains.annotations.Blocking
import usecase.CreateAppiumServerUseCase
import usecase.ExecuteEventsUseCase
import usecase.GetTargetsUseCase
import usecase.SetScreenshotTargetUseCase
import kotlin.coroutines.cancellation.CancellationException

class MainViewModel : ViewModel() {

    private val _errorFlow = MutableStateFlow<Throwable?>(null)
    val errorFlow: StateFlow<Throwable?> = _errorFlow.asStateFlow()

    private val _uiStateFlow = MutableStateFlow<List<MainUiState>>(emptyList())
    val uiStateFlow: StateFlow<List<MainUiState>> = _uiStateFlow.asStateFlow()

    private val tasks = mutableListOf<Task>()

    private val targetsFlow = MutableStateFlow<List<Target>>(emptyList())

    init {
        GetTargetsUseCase()
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
            .catch { _errorFlow.value = it }
            .launchIn(viewModelScope)
    }

    fun runAll() {
        TODO()
    }

    fun run(targetId: TargetId) {
        if (tasks.find { it.targetId == targetId } != null) return

        updateRunState(
            targetId = targetId,
            runState = MainRunState.Running
        )

        val target = targetsFlow.value.first { it.id == targetId }

        val server = try {
            CreateAppiumServerUseCase(
                host = target.configuration.host,
                port = target.configuration.port,
            )
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            detectError(ex = ex, targetId = targetId)
            return
        }

        val job = ExecuteEventsUseCase(target = target)
            .onStart { server.start() }
            .onEach { index ->
                updateIndex(
                    targetId = targetId,
                    index = index,
                )
            }
            .onCompletion { ex ->
                tasks.firstOrNull { it.targetId == targetId }
                    ?.also { removeTask(it) }

                val runState = when (ex) {
                    null -> MainRunState.Finished(isCompletion = true)
                    is CancellationException -> MainRunState.Finished(isCompletion = false)
                    else -> MainRunState.Error(message = ex.stackTraceToString())
                }

                updateRunState(targetId = targetId, runState = runState)
            }
            .launchIn(viewModelScope + Dispatchers.IO)

        val task = Task(
            targetId = targetId,
            server = server,
            job = job,
        )
        tasks.add(task)
    }

    @Blocking
    private fun removeTask(task: Task) {
        println("remove task [targetId=${task.targetId}]")

        tasks.remove(task)
        task.server.stop()
    }

    fun cancel(targetId: TargetId) {
        updateRunState(
            targetId = targetId,
            runState = MainRunState.Cancelling
        )

        viewModelScope.launch(Dispatchers.IO) {
            tasks.find { it.targetId == targetId }
                ?.also { task -> task.job.cancelAndJoin() }

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
        tasks.forEach {
            it.job.cancel()
        }
    }
}

data class Task(
    val targetId: TargetId,
    val server: AppiumDriverLocalService,
    val job: Job,
)