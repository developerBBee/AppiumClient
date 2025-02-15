package usecase

import kotlinx.coroutines.future.await

object StopServerUseCase {

    suspend operator fun invoke(process: Process) {
        process.destroy()
        process.onExit().await()
        println("Appium server stopped. pid=${process.pid()}")
    }
}