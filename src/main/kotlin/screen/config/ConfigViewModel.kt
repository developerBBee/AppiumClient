package screen.config

import data.AppiumConfiguration
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import usecase.GetConfigUseCase
import usecase.SaveConfigUseCase
import util.IOScope

class ConfigViewModel {

    private val _configStateFlow = MutableStateFlow<ConfigState>(ConfigState.Loading)
    val configStateFlow: StateFlow<ConfigState> = _configStateFlow.asStateFlow()

    init {
        GetConfigUseCase()
            .onEach {
                println("Config: $it")
                _configStateFlow.value = ConfigState.Success(it)
            }
            .catch {
                _configStateFlow.value = ConfigState.Error(it)
            }
            .launchIn(IOScope)
    }

    fun saveSettings(config: AppiumConfiguration) {
        IOScope.launch {
            SaveConfigUseCase(config)
        }
    }
}

sealed interface ConfigState {
    data object Loading : ConfigState
    data class Success(val config: AppiumConfiguration) : ConfigState
    data class Error(val throwable: Throwable) : ConfigState
}
