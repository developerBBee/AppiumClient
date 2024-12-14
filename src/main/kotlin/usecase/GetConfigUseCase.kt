package usecase

import data.AppiumConfiguration
import kotlinx.coroutines.flow.Flow
import repository.ConfigRepository

object GetConfigUseCase {
    operator fun invoke(): Flow<AppiumConfiguration> {
        return ConfigRepository.getConfigFlow()
    }
}
