package usecase

import data.Target
import kotlinx.coroutines.flow.Flow
import repository.TargetsRepository

object GetTargetsUseCase {
    operator fun invoke(): Flow<List<Target>> {
        return TargetsRepository.getTargetsFlow()
    }
}
