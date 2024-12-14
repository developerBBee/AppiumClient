package usecase

import data.AppiumConfiguration
import repository.ConfigRepository

object SaveConfigUseCase {
    suspend operator fun invoke(config: AppiumConfiguration) {
        ConfigRepository.saveConfig(config)
    }
}
