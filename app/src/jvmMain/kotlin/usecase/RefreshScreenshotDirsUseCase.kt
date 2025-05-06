package usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import repository.ScreenshotRepository

object RefreshScreenshotDirsUseCase {

    suspend operator fun invoke() {
        ScreenshotRepository.refreshCurrentTarget()
    }
}
