package usecase

import io.appium.java_client.service.local.AppiumDriverLocalService
import io.appium.java_client.service.local.AppiumServiceBuilder
import io.appium.java_client.service.local.flags.GeneralServerFlag

object CreateAppiumServerUseCase {
    operator fun invoke(
        host: String,
        port: Int,
    ): AppiumDriverLocalService {
        val builder = AppiumServiceBuilder()
            .withIPAddress(host)
            .usingPort(port)
            .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
        return AppiumDriverLocalService.buildService(builder)
    }
}