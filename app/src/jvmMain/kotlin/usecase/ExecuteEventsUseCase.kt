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
            runner.start()

            target.scenario.getActions().forEachIndexed { index, action ->
                emit(index)

                action.execute(runner)
            }
        }
    }
}