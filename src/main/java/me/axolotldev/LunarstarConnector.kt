package me.axolotldev

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.axolotldev.cloudflared.CloudflareSoftware
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

class LunarstarConnector : ModInitializer {
    companion object {
        const val MOD_ID: String = "lunarstarconnector"
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
        var CONNECT_PORT = 25565
        lateinit var TUNNEL_PATH: Path
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onInitialize() {
        val platform = CloudflareSoftware.getPlatformFilename().getOrThrow()

        TUNNEL_PATH = CloudflareSoftware.copyTemp(platform)

        if (!CloudflareSoftware.isPortAvailable(CONNECT_PORT)) {
            LOGGER.error("Port $CONNECT_PORT is not available")
            val findPortResult = CloudflareSoftware.findAvailablePort()
            if (findPortResult.isSuccess) {
                CONNECT_PORT = findPortResult.getOrNull() ?: throw IllegalStateException("Never get into here!!!")
            } else {
                throw RuntimeException("Cannot find a available port")
            }
        }

        LOGGER.info("The final port is $CONNECT_PORT")

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                CloudflareSoftware.startTunnel(TUNNEL_PATH, "testroute.axurl.cc", CONNECT_PORT)
            }
        }
    }
}