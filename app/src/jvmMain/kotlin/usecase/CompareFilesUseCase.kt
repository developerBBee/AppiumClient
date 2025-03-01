package usecase

import ui.screen.diff.ComparedFile
import ui.screen.diff.CompareResult
import util.filterImageFile
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

object CompareFilesUseCase {

    operator fun invoke(
        leftDir: Path,
        rightDir: Path,
    ): List<ComparedFile> {
        val leftFiles = leftDir.listDirectoryEntries().filterImageFile()
        val rightFiles = rightDir.listDirectoryEntries().filterImageFile()

        val allImageFileNameSet = (leftFiles + rightFiles)
            .map { it.name }
            .sortedBy { it }
            .toSet()

        return allImageFileNameSet
            .associateWith { checkFileName ->
                val leftFile = leftFiles.find { it.name == checkFileName }
                val rightFile = rightFiles.find { it.name == checkFileName }

                if (leftFile == null) return@associateWith CompareResult.RIGHT_ONLY

                if (rightFile == null) return@associateWith CompareResult.LEFT_ONLY

                val leftContent = Files.readAllBytes(leftFile)
                val rightContent = Files.readAllBytes(rightFile)

                if (leftContent.contentEquals(rightContent)) {
                    CompareResult.SAME
                } else {
                    CompareResult.DIFFERENCE
                }
            }
            .map { (fileName, result) ->
                ComparedFile(fileName = fileName, result = result)
            }
    }
}

