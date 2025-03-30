package usecase

import org.jetbrains.annotations.Blocking
import ui.screen.diff.ComparedFile
import ui.screen.diff.CompareResult
import util.Constant
import util.filterImageFile
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

object CompareFilesUseCase {

    @Blocking
    operator fun invoke(
        leftDir: Path,
        rightDir: Path,
        diffOnly: Boolean,
        noNameExclude: Boolean,
    ): List<ComparedFile> {
        val leftFiles = leftDir.listDirectoryEntries().filterImageFile()
        val rightFiles = rightDir.listDirectoryEntries().filterImageFile()

        val allImageFileNameSet = (leftFiles + rightFiles)
            .map { it.name }
            .filterNot { noNameExclude && it.startsWith(Constant.NO_NAME_PREFIX) }
            .sortedBy { it }
            .toSet()

        return allImageFileNameSet
            .associateWith { checkFileName ->
                val leftFile = leftFiles.find { it.name == checkFileName }
                val rightFile = rightFiles.find { it.name == checkFileName }

                if (leftFile == null) return@associateWith CompareResult.RIGHT_ONLY
                if (rightFile == null) return@associateWith CompareResult.LEFT_ONLY

                compareDifference(leftFile, rightFile)
            }
            .filterNot { (_, result) -> diffOnly && result == CompareResult.SAME }
            .map { (fileName, result) ->
                ComparedFile(fileName = fileName, result = result)
            }
    }

    private fun compareDifference(
        leftFile: Path,
        rightFile: Path,
        toleranceRate: Float = Constant.TOLERANCE_RATE,
    ): CompareResult {
        val leftContent = Files.readAllBytes(leftFile)
        val rightContent = Files.readAllBytes(rightFile)

        if (leftContent.contentEquals(rightContent)) {
            return CompareResult.SAME
        }

        val leftImage = ImageIO.read(leftFile.toFile())
        val rightImage = ImageIO.read(rightFile.toFile())

        if (leftImage.width != rightImage.width || leftImage.height != rightImage.height) {
            return CompareResult.DIFFERENCE
        }

        var diffCount = 0
        val toleranceCount = (leftImage.width * leftImage.height * toleranceRate).toInt()
        for (y in 0 until leftImage.height) {
            for (x in 0 until leftImage.width) {
                val leftRgb = leftImage.getRGB(x, y)
                val rightRgb = rightImage.getRGB(x, y)
                // TODO 色の差異の大きさも考慮するようにしたい
                if (leftRgb != rightRgb) {
                    diffCount++
                    // 許容値を超える差分がある場合は、DIFFERENCEとする
                    if (diffCount > toleranceCount) {
                        return CompareResult.DIFFERENCE
                    }
                }
            }
        }

        return CompareResult.SAME
    }
}

