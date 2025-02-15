package usecase

import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

/**
 * Appiumサーバーが起動した際のメッセージが出力されるまで待つ。
 * 環境によって異なる可能性があるのでタイムアウトをつける。
 * また、既に起動していた場合も出力されない。
 * タイムアウトになってもキャンセルせず続行する。
 */
private const val APPIUM_LAUNCHED_MESSAGE = "Appium REST http interface listener started"
private val LAUNCHING_TIMEOUT_DURATION = 3.seconds

object LaunchServerUseCase {

    suspend operator fun invoke(
        host: String,
        port: Int
    ): Process = withContext(Dispatchers.IO) {
        val process = ProcessBuilder("appium", "-a", host, "-p", port.toString(), "--allow-cors")
            .redirectErrorStream(true)
            .start()

        kotlin.runCatching {
            waitUntilLaunched(process = process)
            println("Appium server started. host=$host port=$port pid=${process.pid()}")
            process
        }.getOrElse { ex ->
            if (ex is TimeoutCancellationException) {
                println("[WARN]Could not confirm launch message. port=$port pid=${process.pid()}")
                process
            } else {
                process.destroy()
                throw ex
            }
        }
    }

    private suspend fun waitUntilLaunched(process: Process) {
        process.inputStream.bufferedReader().use { reader ->
            withTimeout(LAUNCHING_TIMEOUT_DURATION) {
                while (true) {
                    yield()
                    if (reader.ready()) {
                        val line = reader.readLine()
                        println(line)
                        if (line?.contains(APPIUM_LAUNCHED_MESSAGE) == true) {
                            break
                        }
                    }
                }
            }
        }
    }
}