package usecase

import repository.ScreenshotRepository

object RefreshScreenshotDirsUseCase {

    suspend operator fun invoke() {
        ScreenshotRepository.refreshCurrentTarget()
    }
}
