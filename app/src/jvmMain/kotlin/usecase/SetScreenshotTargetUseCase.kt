package usecase

import data.Target
import repository.ScreenshotRepository

object SetScreenshotTargetUseCase {

    operator fun invoke(target: Target) {
        ScreenshotRepository.changeTarget(target = target)
    }
}
