package usecase

import org.jetbrains.annotations.Blocking
import util.createImageDifference
import java.nio.file.Path
import kotlin.io.path.exists

object GetImagePathUseCase {

    @Blocking
    operator fun invoke(
        leftFilePath: Path,
        rightFilePath: Path,
        useImageDiff: Boolean,
    ): Pair<Path?, Path?> {
        val leftImagePath = leftFilePath.takeIf { it.exists() }
        val rightImagePath = rightFilePath.takeIf { it.exists() }

        if (useImageDiff && leftImagePath != null && rightImagePath != null) {
            val rightDiffPath = createImageDifference(leftImagePath, rightImagePath)
            return leftImagePath to rightDiffPath
        } else {
            return leftImagePath to rightImagePath
        }
    }
}

