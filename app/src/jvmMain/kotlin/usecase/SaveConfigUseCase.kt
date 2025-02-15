package usecase

import data.Target
import repository.TargetsRepository

internal object SaveConfigUseCase {
    suspend operator fun invoke(targets: List<Target>) {
        TargetsRepository.saveConfig(targets)
    }
}
