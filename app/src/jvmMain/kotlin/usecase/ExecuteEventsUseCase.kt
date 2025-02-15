package usecase

import data.Target
import event.runner.AndroidEventRunner
import kotlinx.coroutines.flow.flow

object ExecuteEventsUseCase {

    operator fun invoke(target: Target) = flow {
        AndroidEventRunner(
            targetName = target.name,
            config = target.configuration
        ).use { runner ->
            val sessionId = runner.start()
            emit(sessionId.toString())

            target.scenario.getActions().forEach { action ->
                action.execute(runner)
            }
        }
    }
}