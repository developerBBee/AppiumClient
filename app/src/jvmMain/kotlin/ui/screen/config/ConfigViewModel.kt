package ui.screen.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.EMU_SAMPLE_TARGET
import data.Target
import data.nextId
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import usecase.GetTargetsUseCase
import usecase.SaveConfigUseCase

class ConfigViewModel : ViewModel() {

    private val handler = CoroutineExceptionHandler { _, th -> detectError(th) }

    private val errorFlow = MutableStateFlow<Throwable?>(null)

    val uiState: StateFlow<ConfigUiState> = combine(
        GetTargetsUseCase(),
        errorFlow,
    ) { targets, error ->
        if (error != null) {
            ConfigUiState.Error(error)
        } else {
            ConfigUiState.Success(targets)
        }
    }
        .flowOn(Dispatchers.IO)
        .catch { emit(ConfigUiState.Error(it)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ConfigUiState.Loading,
        )

    fun addNewTarget() {
        val state = uiState.value as? ConfigUiState.Success ?: return

        val newTarget = EMU_SAMPLE_TARGET.copy(id = state.targets.nextId())

        viewModelScope.launch(handler + Dispatchers.IO) {
            SaveConfigUseCase(state.targets + newTarget)
        }
    }

    fun removeTarget(target: Target) {
        val state = uiState.value as? ConfigUiState.Success ?: return

        val targets = state.targets.toMutableList()
        targets.remove(target)

        viewModelScope.launch(handler + Dispatchers.IO) {
            SaveConfigUseCase(targets)
        }
    }

    fun updateTarget(index: Int, target: Target) {
        val state = uiState.value
        if (state !is ConfigUiState.Success) return

        val targets = state.targets.toMutableList()
        targets[index] = target

        viewModelScope.launch(handler + Dispatchers.IO) {
            SaveConfigUseCase(targets)
        }
    }

    private fun detectError(th: Throwable) {
        errorFlow.value = th
    }
}

sealed interface ConfigUiState {
    data object Loading : ConfigUiState
    data class Success(val targets: List<Target>) : ConfigUiState
    data class Error(val throwable: Throwable) : ConfigUiState
}
