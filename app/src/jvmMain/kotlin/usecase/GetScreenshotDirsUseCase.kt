package usecase

import kotlinx.coroutines.flow.Flow
import repository.ScreenshotRepository
import java.nio.file.Path

object GetScreenshotDirsUseCase {

    operator fun invoke(): Flow<List<Path>> {
        return ScreenshotRepository.screenshotDirsFlow
    }
}
