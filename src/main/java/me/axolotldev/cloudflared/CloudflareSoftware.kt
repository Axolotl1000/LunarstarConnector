package me.axolotldev.cloudflared

import java.net.ServerSocket
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.axolotldev.LunarstarConnector.Companion.LOGGER
import me.axolotldev.LunarstarConnector.Companion.MOD_ID
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.PosixFilePermission


object CloudflareSoftware{

    private lateinit var process: Process

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
          destroy()
        })
    }

    fun getPlatformFilename(): Result<String> {
        val os = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch").lowercase()

        val filename = when {
            os.contains("win") && arch.contains("64") -> "windows-amd64.exe"
            os.contains("win") && (arch.contains("86") || arch.contains("32")) -> "windows-386.exe"

            os.contains("mac") && arch.contains("x86_64") -> "darwin-amd64"
            os.contains("mac") && (arch.contains("arm") || arch.contains("aarch64")) -> "darwin-arm64"

            os.contains("linux") && arch == "x86" -> "linux-386"
            os.contains("linux") && arch == "amd64" -> "linux-amd64"
            os.contains("linux") && arch == "arm" -> "linux-arm"
            os.contains("linux") && arch == "aarch64" -> "linux-arm64"
            os.contains("linux") && arch == "armhf" -> "linux-armhf"

            else -> null
        }

        return if (filename != null) Result.success(filename) else Result.failure(UnsupportedOperationException("Unsupported Operating System"))
    }

    fun copyTemp(filename: String): Path {
        val fileStream = CloudflareSoftware::class.java.getResourceAsStream("/cloudflared/$filename")
        requireNotNull(fileStream)
        val tempFile = Files.createTempFile("cloudflare", filename)
        Files.copy(fileStream, tempFile, StandardCopyOption.REPLACE_EXISTING)

        val osName = System.getProperty("os.name").lowercase()
        if (osName.contains("linux") || osName.contains("unix")) {
            val perms = Files.getPosixFilePermissions(tempFile).toMutableSet()
            perms.add(PosixFilePermission.OWNER_EXECUTE)
            perms.add(PosixFilePermission.GROUP_EXECUTE)
            perms.add(PosixFilePermission.OTHERS_EXECUTE)
            Files.setPosixFilePermissions(tempFile, perms)
        }

        tempFile.toFile().deleteOnExit()

        return tempFile
    }

    suspend fun startTunnel(path: Path, hostname: String, port: Int) = withContext(Dispatchers.IO) {
        val command = listOf(
            path.toAbsolutePath().toString(),
            "access",
            "tcp",
            "--hostname", hostname,
            "--url", "tcp://127.127.127.127:$port"
        )

        try {
            process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()

            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    LOGGER.info(line)
                }
            }

            process.waitFor()
        } catch (e: Exception) {
            LOGGER.error("Failed to start tunnel", e)
            throw e
        }
    }

    fun isPortAvailable(port: Int): Boolean {
        if (port !in 1..65535) {
            return false
        }

        try {
            ServerSocket(port).use {
                return true
            }
        } catch (_: Exception) {
            return false
        }
    }

    fun destroy() {
        try {
            if (::process.isInitialized && process.isAlive) {
                LOGGER.info("Stopping Cloudflared process...")
                process.destroy()
                if (!process.waitFor(3, java.util.concurrent.TimeUnit.SECONDS)) {
                    LOGGER.warn("Process did not exit in time, forcing shutdown.")
                    process.destroyForcibly()
                }
                LOGGER.info("Cloudflared process stopped.")
            } else {
                LOGGER.info("No active Cloudflared process to stop.")
            }
        } catch (e: Exception) {
            LOGGER.error("Error while stopping Cloudflared process", e)
        }
    }

    fun findAvailablePort(retry: Int = 10): Result<Int> {
        repeat(retry) {
            val port = (50000..59999).random()
            if (isPortAvailable(port)) return Result.success(port)
        }

        return Result.failure(IllegalStateException("No available port found after $retry attempts"))
    }

}