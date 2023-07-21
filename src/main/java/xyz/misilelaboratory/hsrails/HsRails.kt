package xyz.misilelaboratory.hsrails

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor as ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class HsRails : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig() // copies default file to data folder, will not override existing file
        val logger = logger
        logger.info("Reading config")
        configuration.readConfig(config, logger)
        logger.info("Registering event listener")
        val pm = server.pluginManager
        pm.registerEvents(
            MinecartListener(
                configuration.boostBlock,
                configuration.hardBrakeBlock,
                configuration.isCheatMode
            ), this
        )
    }

    override fun onDisable() {
        logger.info("unloading...")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.equals("hsrails", ignoreCase = true)) {
            if (sender is Player) {
                if (!sender.hasPermission("hsrails.cmd")) {
                    sender.sendMessage(Component.text(ChatColor.RED.toString() + "You don't have permission to use this command"))
                    return true
                }
            }
            try {
                configuration.speedMultiplier = args[0].toDouble()
            } catch (ignore: Exception) {
                sender.sendMessage(Component.text(ChatColor.RED.toString() + "multiplier should be a number"))
                return false
            }
            val speedMultiplier = configuration.speedMultiplier
            if (speedMultiplier > 0 && speedMultiplier <= 8) {
                val message = ChatColor.AQUA.toString() + "Speed multiplier set to: " + speedMultiplier
                val headsUp = """
                    ${ChatColor.YELLOW}
                    Note: multiplier set to more than 4x. Servers often struggle to provide max speeds above 4x, and the carts may appear to be capped at 4x. However, carts will still have their momentum increased, meaning they will coast for longer.
                    """.trimIndent()
                val sendHeadsUp = !receivedHeadsUp.contains(sender) && speedMultiplier > 4
                sender.sendMessage(Component.text(String.format("%s%s", message, if (sendHeadsUp) headsUp else "")))
                if (sendHeadsUp) {
                    receivedHeadsUp.add(sender)
                }
                return true
            }
            sender.sendMessage(Component.text(ChatColor.RED.toString() + "multiplier must be greater than 0 and max 8"))
            return true
        }
        return false
    }

    companion object {
        val configuration = Configuration()
        private val receivedHeadsUp: MutableSet<CommandSender> = HashSet()
    }
}


