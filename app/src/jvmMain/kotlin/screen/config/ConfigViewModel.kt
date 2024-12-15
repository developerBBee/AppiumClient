package screen.config

import data.AppiumConfiguration
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import usecase.GetConfigUseCase
import usecase.SaveConfigUseCase
import util.IOScope

class ConfigViewModel {
    private val handler = CoroutineExceptionHandler { _, th -> detectError(th) }

    private val _configStateFlow = MutableStateFlow<ConfigState>(ConfigState.Loading)
    val configStateFlow: StateFlow<ConfigState> = _configStateFlow.asStateFlow()

    private val job: Job = GetConfigUseCase()
        .onEach {
            println("Config: $it")
            _configStateFlow.value = ConfigState.Success(it)
        }
        .catch {
            _configStateFlow.value = ConfigState.Error(it)
        }
        .launchIn(IOScope)

    fun saveSettings(config: AppiumConfiguration) {
        IOScope.launch(handler) {
            SaveConfigUseCase(config)
        }
    }

    private fun detectError(th: Throwable) {
        _configStateFlow.value = ConfigState.Error(th)
    }

    fun dispose() {
        job.cancel()
    }
}

sealed interface ConfigState {
    data object Loading : ConfigState
    data class Success(val config: AppiumConfiguration) : ConfigState
    data class Error(val throwable: Throwable) : ConfigState
}
