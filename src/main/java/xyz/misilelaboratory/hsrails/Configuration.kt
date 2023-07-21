package xyz.misilelaboratory.hsrails

import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import java.util.logging.Logger

class Configuration {
    var boostBlock: Material? = null
        private set
    var hardBrakeBlock: Material? = null
        private set
    var speedMultiplier = 0.0
    var hardBrakeMultiplier = 0.0
    var isCheatMode = false
        private set

    fun readConfig(fileConfig: FileConfiguration, logger: Logger) {
        readBoostBlock(fileConfig, logger)
        readHardBrakeBlock(fileConfig, logger)
        readSpeedMultiplier(fileConfig, logger)
        readHardBrakeMultiplier(fileConfig, logger)
    }

    private fun readBoostBlock(fileConfig: FileConfiguration, logger: Logger) {
        val boostBlockKey = fileConfig.getString("boostBlock")
        if (boostBlockKey != null) {
            if (boostBlockKey.equals("any", ignoreCase = true)) {
                isCheatMode = true
            } else {
                boostBlock = Material.matchMaterial(boostBlockKey)
            }
        }
        if (boostBlock == null && !isCheatMode) {
            val fallbackMat = Material.REDSTONE_BLOCK
            logger.warning(
                String.format(
                    "Warning: option 'boostBlock' was '%s' in config which is an illegal value. Falling back to using '%s'",
                    boostBlockKey ?: "(undefined)",
                    fallbackMat.key
                )
            )
            boostBlock = fallbackMat
        }
        if (isCheatMode) {
            logger.info("Boost block was set to 'any'. Every powered rail is now a high speed rail.")
        } else {
            logger.info(String.format("Setting boost block to '%s'", boostBlock!!.key))
        }
    }

    private fun readHardBrakeBlock(fileConfig: FileConfiguration, logger: Logger) {
        val hardBrakeBlockKey = fileConfig.getString("hardBrakeBlock")
        if (hardBrakeBlockKey != null) {
            hardBrakeBlock = Material.matchMaterial(hardBrakeBlockKey)
        }
        if (hardBrakeBlock == null) {
            logger.warning("Warning: option 'hardBrakeBlock' was not specified or invalid value was given. Hard braking disabled.")
        }
    }

    private fun readSpeedMultiplier(fileConfig: FileConfiguration, logger: Logger) {
        var speedMultiplier = fileConfig.getDouble("speedMultiplier")
        if (speedMultiplier <= 0) {
            logger.warning("Warning: speed multiplier set to 0 or below in config. Using value of 0.1 as fallback.")
            speedMultiplier = 0.1
        } else if (speedMultiplier > 8) {
            logger.warning("Warning: speed multiplier set above 8 in config. Using value of 8 as fallback.")
            speedMultiplier = 8.0
        } else {
            logger.info("Setting speed multiplier to $speedMultiplier")
        }
        if (speedMultiplier > 4) {
            logger.info(
                "Note: speed multiplier is set above 4. Typically, due to server limitations you may not see an increase in speed greater than 4x,"
                        + " however the carts will have more momentum. This means they will coast for longer even though the max speed is seemingly 4x."
            )
        }
        this.speedMultiplier = speedMultiplier
    }

    private fun readHardBrakeMultiplier(fileConfig: FileConfiguration, logger: Logger) {
        var hardBrakeMultiplier = fileConfig.getDouble("hardBrakeMultiplier")
        if (hardBrakeMultiplier < 1.0) {
            logger.warning("Warning: brake multiplier not set or set to below 1 in config. Using value of 1 as fallback.")
            hardBrakeMultiplier = 1.0
        } else {
            logger.info("Setting brake multiplier to $hardBrakeMultiplier")
        }
        this.hardBrakeMultiplier = 1.0 / hardBrakeMultiplier
    }
}
