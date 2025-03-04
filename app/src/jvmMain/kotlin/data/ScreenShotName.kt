package data

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ScreenShotName(val value: String) {

    init {
        require(FILENAME_INVALID_REGEX.matches(value).not()) {
            "スクリーンショット名にファイル名として使用できない文字が含まれています: $value"
        }
    }

    override fun toString(): String = value

    companion object {
        private val FILENAME_INVALID_REGEX = Regex(".*[\\\\/:*?\"<>|].*")
    }
}

fun String.toSSName() = ScreenShotName(this)
