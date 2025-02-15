package usecase

import java.net.NetworkInterface

object GetPrivateIpAddressUseCase {

    operator fun invoke(): String? {
        val networkIFs = NetworkInterface.getNetworkInterfaces().toList()

        val addresses = networkIFs.flatMap { networkIF ->
            networkIF.inetAddresses.toList()
        }

        return addresses.firstOrNull { address ->
            !address.isLoopbackAddress && address.isSiteLocalAddress
        }?.hostAddress
    }
}