package screen.config

import data.*
import data.Target
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import usecase.GetTargetsUseCase
import usecase.SaveConfigUseCase
import util.IOScope

class ConfigViewModel {
    private val handler = CoroutineExceptionHandler { _, th -> detectError(th) }

    private val _configUiStateFlow = MutableStateFlow<ConfigUiState>(ConfigUiState.Loading)
    val configUiStateFlow: StateFlow<ConfigUiState> = _configUiStateFlow.asStateFlow()

    private val job: Job = GetTargetsUseCase()
        .onEach {
            println(it.map { it.configuration to it.deviceInfo })
            _configUiStateFlow.value = ConfigUiState.Success(it)
        }
        .catch {
            _configUiStateFlow.value = ConfigUiState.Error(it)
        }
        .launchIn(IOScope)

    fun addNewTarget() {
        val state = _configUiStateFlow.value as? ConfigUiState.Success ?: return

        val newTarget = EMU_SAMPLE_TARGET.copy(id = state.targets.nextId())

        IOScope.launch(handler) {
            SaveConfigUseCase(state.targets + newTarget)
        }
    }

    fun removeTarget(target: Target) {
        val state = _configUiStateFlow.value as? ConfigUiState.Success ?: return

        val targets = state.targets.toMutableList()
        targets.remove(target)

        IOScope.launch(handler) {
            SaveConfigUseCase(targets)
        }
    }

    fun updateTarget(index: Int, target: Target) {
        val state = _configUiStateFlow.value
        if (state !is ConfigUiState.Success) return

        val targets = state.targets.toMutableList()
        targets[index] = target

        IOScope.launch(handler) {
            SaveConfigUseCase(targets)
        }
    }

    private fun detectError(th: Throwable) {
        _configUiStateFlow.value = ConfigUiState.Error(th)
    }

    fun dispose() {
        job.cancel()
    }
}

sealed interface ConfigUiState {
    data object Loading : ConfigUiState
    data class Success(val targets: List<Target>) : ConfigUiState
    data class Error(val throwable: Throwable) : ConfigUiState
}
