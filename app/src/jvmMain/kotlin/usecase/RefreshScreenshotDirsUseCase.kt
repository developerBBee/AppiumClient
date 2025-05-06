package usecase

import repository.ScreenshotRepository

object RefreshScreenshotDirsUseCase {

    operator fun invoke() {
        ScreenshotRepository.refreshCurrentTarget()
    }
}
